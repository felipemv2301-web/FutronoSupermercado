package com.example.intento1app.data.services

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.example.intento1app.data.config.MercadoPagoConfig
import com.example.intento1app.data.models.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Servicio para manejar la integración con MercadoPago Checkout API
 */
class MercadoPagoService(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${MercadoPagoConfig.ACCESS_TOKEN}")
                .build()
            chain.proceed(request)
        }
        .build()
    
    private val gson = Gson()
    
    /**
     * Crea una preferencia de pago en MercadoPago
     * @param cartItems Lista de items del carrito
     * @return Pair con la URL del checkout y el preference_id
     */
    suspend fun createPaymentPreference(cartItems: List<com.example.intento1app.data.models.CartItem>): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        try {
            // Validar que las keys estén configuradas
            if (MercadoPagoConfig.ACCESS_TOKEN == "TU_ACCESS_TOKEN_AQUI" || 
                MercadoPagoConfig.ACCESS_TOKEN.isEmpty()) {
                return@withContext Result.failure(
                    Exception("Access Token no configurado. Por favor, configura tus credenciales de MercadoPago en MercadoPagoConfig.kt")
                )
            }
            
            // Validar que haya items en el carrito
            if (cartItems.isEmpty()) {
                return@withContext Result.failure(
                    Exception("El carrito está vacío. No se puede crear una preferencia de pago sin items.")
                )
            }
            // Convertir items del carrito a items de preferencia
            // MercadoPago requiere unit_price como entero (sin decimales para CLP)
            val preferenceItems = cartItems.map { cartItem ->
                PreferenceItem(
                    title = cartItem.product.name,
                    quantity = cartItem.quantity,
                    unitPrice = kotlin.math.round(cartItem.product.price).toInt() // Redondear y convertir a entero
                )
            }
            
            // Calcular subtotal e IVA
            val subtotal = cartItems.sumOf { it.totalPrice }
            val iva = subtotal * 0.19 // 19% IVA
            
            // Agregar el IVA como un item adicional en la preferencia
            // Convertir a entero redondeando
            val ivaItem = PreferenceItem(
                title = "IVA (19%)",
                quantity = 1,
                unitPrice = kotlin.math.round(iva).toInt() // Redondear y convertir a entero
            )
            
            val allItems = preferenceItems + ivaItem
            
            // Calcular el total (subtotal + IVA)
            val totalAmount = subtotal + iva
            
            android.util.Log.d("MercadoPago", "Subtotal: $subtotal, IVA: $iva, Total: $totalAmount")
            
            // MercadoPago requiere URLs HTTP/HTTPS válidas y accesibles desde internet
            // Si hay un servidor de redirección configurado, usarlo
            // Si no, crear la preferencia sin back_urls y usar verificación manual
            
            val redirectServiceUrl = MercadoPagoConfig.REDIRECT_SERVICE_URL
            
            val preferenceRequest = if (redirectServiceUrl != null) {
                // Usar servidor de redirección público
                android.util.Log.d("MercadoPago", "Usando servidor de redirección: $redirectServiceUrl")
                val successUrl = MercadoPagoConfig.getSuccessUrl()
                val pendingUrl = MercadoPagoConfig.getPendingUrl()
                val failureUrl = MercadoPagoConfig.getFailureUrl()
                val webhookUrl = MercadoPagoConfig.getWebhookUrl()
                
                android.util.Log.d("MercadoPago", "Success URL: $successUrl")
                android.util.Log.d("MercadoPago", "Pending URL: $pendingUrl")
                android.util.Log.d("MercadoPago", "Failure URL: $failureUrl")
                android.util.Log.d("MercadoPago", "Webhook URL: $webhookUrl")
                
                PaymentPreferenceRequest(
                    items = allItems, // Incluye productos + IVA
                    backUrls = BackUrls(
                        success = successUrl,
                        pending = pendingUrl,
                        failure = failureUrl
                    ),
                    autoReturn = "all", // Redirigir siempre
                    payer = null,
                    notificationUrl = webhookUrl // Configurar webhook
                )
            } else {
                // Sin servidor de redirección: crear sin back_urls
                android.util.Log.d("MercadoPago", "Sin servidor de redirección - usando verificación manual")
                PaymentPreferenceRequest(
                    items = allItems, // Incluye productos + IVA
                    backUrls = null,
                    autoReturn = null,
                    payer = null,
                    notificationUrl = null
                )
            }
            
            // Convertir a JSON
            val jsonBody = gson.toJson(preferenceRequest)
            
            // Log para debugging (remover en producción)
            android.util.Log.d("MercadoPago", "=== CREANDO PREFERENCIA ===")
            android.util.Log.d("MercadoPago", "Request JSON: $jsonBody")
            android.util.Log.d("MercadoPago", "Items: ${preferenceItems.size}")
            
            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
            
            // Crear la petición HTTP
            val request = Request.Builder()
                .url("${MercadoPagoConfig.API_BASE_URL}/checkout/preferences")
                .post(requestBody)
                .build()
            
            // Ejecutar la petición
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                
                // Log para debugging
                android.util.Log.d("MercadoPago", "=== RESPUESTA EXITOSA ===")
                android.util.Log.d("MercadoPago", "Response code: ${response.code}")
                android.util.Log.d("MercadoPago", "Response body: $responseBody")
                
                try {
                    val preferenceResponse = gson.fromJson(responseBody, PaymentPreferenceResponse::class.java)
                    
                    android.util.Log.d("MercadoPago", "Preference ID: ${preferenceResponse.id}")
                    android.util.Log.d("MercadoPago", "Init Point: ${preferenceResponse.initPoint}")
                    android.util.Log.d("MercadoPago", "Sandbox Init Point: ${preferenceResponse.sandboxInitPoint}")
                    
                    // Determinar si estamos en modo prueba o producción
                    val isTestMode = MercadoPagoConfig.ACCESS_TOKEN.startsWith("TEST-")
                    android.util.Log.d("MercadoPago", "Modo: ${if (isTestMode) "SANDBOX (Prueba) - Usa tarjetas de prueba" else "PRODUCCIÓN - Usa tarjetas reales"}")
                    
                    // Usar init_point o sandbox_init_point según corresponda
                    // IMPORTANTE: Si estás usando keys de prueba (TEST-), debes usar sandbox_init_point
                    // Si estás usando keys de producción (APP_USR-), usa init_point
                    val checkoutUrl = if (isTestMode) {
                        preferenceResponse.sandboxInitPoint ?: preferenceResponse.initPoint
                    } else {
                        preferenceResponse.initPoint ?: preferenceResponse.sandboxInitPoint
                    }
                    
                    if (isTestMode && checkoutUrl?.contains("sandbox") == false) {
                        android.util.Log.w("MercadoPago", "⚠️ ADVERTENCIA: Estás en modo TEST pero la URL no es de sandbox")
                        android.util.Log.w("MercadoPago", "⚠️ Asegúrate de usar tarjetas de prueba: https://www.mercadopago.com.ar/developers/es/docs/checkout-pro/test-cards")
                    }
                    
                    if (checkoutUrl.isNullOrEmpty()) {
                        android.util.Log.e("MercadoPago", "ERROR: URL de checkout vacía")
                        Result.failure(Exception("No se recibió URL de checkout en la respuesta"))
                    } else {
                        android.util.Log.d("MercadoPago", "URL de checkout generada: $checkoutUrl")
                        android.util.Log.d("MercadoPago", "Preference ID guardado: ${preferenceResponse.id}")
                        // Guardar preference_id en SharedPreferences para verificar después
                        val prefs = context.getSharedPreferences("MercadoPagoPrefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("last_preference_id", preferenceResponse.id).apply()
                        Result.success(Pair(checkoutUrl, preferenceResponse.id))
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MercadoPago", "Error parseando respuesta: ${e.message}", e)
                    Result.failure(Exception("Error al procesar respuesta: ${e.message}"))
                }
            } else {
                val errorBody = response.body?.string() ?: "Error desconocido"
                android.util.Log.e("MercadoPago", "=== ERROR EN RESPUESTA ===")
                android.util.Log.e("MercadoPago", "Response code: ${response.code}")
                android.util.Log.e("MercadoPago", "Error body: $errorBody")
                Result.failure(Exception("Error al crear preferencia: ${response.code} - $errorBody"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}", e))
        }
    }
    
    /**
     * Abre el checkout de MercadoPago en Custom Tabs
     * @param checkoutUrl URL del init_point
     * @param preferenceId ID de la preferencia para verificar después
     */
    fun openCheckout(checkoutUrl: String, preferenceId: String) {
        android.util.Log.d("MercadoPago", "=== ABRIENDO CHECKOUT ===")
        android.util.Log.d("MercadoPago", "URL: $checkoutUrl")
        android.util.Log.d("MercadoPago", "Preference ID: $preferenceId")
        
        try {
            val builder = CustomTabsIntent.Builder()
            builder.setShowTitle(true)
            builder.setToolbarColor(0xFF009EE3.toInt()) // Color de MercadoPago
            
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(checkoutUrl))
            
            android.util.Log.d("MercadoPago", "Checkout abierto exitosamente")
        } catch (e: Exception) {
            android.util.Log.e("MercadoPago", "Error al abrir checkout: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * Verifica el estado de un pago consultando los pagos asociados a una preferencia
     * @param preferenceId ID de la preferencia
     * @return Resultado del pago o null si no se puede determinar
     */
    suspend fun checkPaymentStatus(preferenceId: String): PaymentResult? = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("MercadoPago", "=== VERIFICANDO ESTADO DEL PAGO ===")
            android.util.Log.d("MercadoPago", "Preference ID: $preferenceId")
            
            // Primero obtener la preferencia
            val preferenceRequest = Request.Builder()
                .url("${MercadoPagoConfig.API_BASE_URL}/checkout/preferences/$preferenceId")
                .get()
                .build()
            
            val preferenceResponse = client.newCall(preferenceRequest).execute()
            
            if (preferenceResponse.isSuccessful) {
                val responseBody = preferenceResponse.body?.string()
                android.util.Log.d("MercadoPago", "Response de preferencia: $responseBody")
                
                // Buscar pagos asociados a esta preferencia
                // MercadoPago permite buscar pagos por preference_id
                val searchRequest = Request.Builder()
                    .url("${MercadoPagoConfig.API_BASE_URL}/v1/payments/search?preference_id=$preferenceId")
                    .get()
                    .build()
                
                val searchResponse = client.newCall(searchRequest).execute()
                
                if (searchResponse.isSuccessful) {
                    val searchBody = searchResponse.body?.string()
                    android.util.Log.d("MercadoPago", "Response de búsqueda de pagos: $searchBody")
                    
                    // Parsear la respuesta para encontrar el estado del pago
                    try {
                        val jsonObject = com.google.gson.JsonParser.parseString(searchBody).asJsonObject
                        val results = jsonObject.getAsJsonArray("results")
                        
                        if (results != null && results.size() > 0) {
                            val payment = results.get(0).asJsonObject
                            val status = payment.get("status")?.asString
                            val paymentId = payment.get("id")?.asString
                            
                            android.util.Log.d("MercadoPago", "Pago encontrado - ID: $paymentId, Status: $status")
                            
                            return@withContext when (status?.lowercase()) {
                                "approved" -> PaymentResult(
                                    status = PaymentResultStatus.SUCCESS,
                                    paymentId = paymentId,
                                    message = "¡Pago aprobado exitosamente!"
                                )
                                "pending", "in_process", "in_mediation" -> PaymentResult(
                                    status = PaymentResultStatus.PENDING,
                                    paymentId = paymentId,
                                    message = "Pago pendiente de confirmación"
                                )
                                "rejected", "cancelled", "refunded" -> PaymentResult(
                                    status = PaymentResultStatus.FAILURE,
                                    paymentId = paymentId,
                                    message = "Pago rechazado o cancelado"
                                )
                                else -> null
                            }
                        } else {
                            android.util.Log.d("MercadoPago", "No se encontraron pagos para esta preferencia")
                            return@withContext PaymentResult(
                                status = PaymentResultStatus.PENDING,
                                message = "Aún no se ha procesado ningún pago para esta preferencia"
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MercadoPago", "Error parseando respuesta: ${e.message}", e)
                    }
                } else {
                    android.util.Log.e("MercadoPago", "Error al buscar pagos: ${searchResponse.code}")
                }
            } else {
                android.util.Log.e("MercadoPago", "Error al obtener preferencia: ${preferenceResponse.code}")
            }
            
            null
        } catch (e: Exception) {
            android.util.Log.e("MercadoPago", "Error al verificar estado del pago: ${e.message}", e)
            null
        }
    }
    
    /**
     * Procesa el resultado del pago desde la URL de retorno
     * @param uri URI de retorno con los parámetros del pago
     * @return Resultado del pago
     */
    fun processPaymentResult(uri: Uri): PaymentResult {
        // Obtener parámetros de la URL
        val status = uri.getQueryParameter("status")
        val paymentId = uri.getQueryParameter("payment_id")
        val preferenceId = uri.getQueryParameter("preference_id")
        val paymentType = uri.getQueryParameter("payment_type_id")
        val errorMessage = uri.getQueryParameter("error")
        val errorDescription = uri.getQueryParameter("error_description")
        
        // Determinar el estado basado en la ruta y parámetros
        val path = uri.path ?: ""
        val isSuccessPath = path.contains("success", ignoreCase = true)
        val isPendingPath = path.contains("pending", ignoreCase = true)
        val isFailurePath = path.contains("failure", ignoreCase = true)
        
        return when {
            // Caso 1: Status explícito en parámetros
            status != null -> {
                when (status.lowercase()) {
                    "approved", "aprobado" -> PaymentResult(
                        status = PaymentResultStatus.SUCCESS,
                        paymentId = paymentId,
                        message = "¡Pago aprobado exitosamente! Tu compra ha sido procesada correctamente."
                    )
                    "pending", "pendiente", "in_process", "in_mediation" -> PaymentResult(
                        status = PaymentResultStatus.PENDING,
                        paymentId = paymentId,
                        message = buildPendingMessage(paymentType, errorDescription)
                    )
                    "rejected", "rechazado", "cancelled", "cancelado", "refunded", "reembolsado" -> PaymentResult(
                        status = PaymentResultStatus.FAILURE,
                        paymentId = paymentId,
                        message = buildFailureMessage(errorMessage, errorDescription, paymentType)
                    )
                    else -> PaymentResult(
                        status = PaymentResultStatus.CANCELLED,
                        paymentId = paymentId,
                        message = "El pago fue cancelado o no se completó."
                    )
                }
            }
            // Caso 2: Determinar por la ruta del deep link
            isSuccessPath -> PaymentResult(
                status = PaymentResultStatus.SUCCESS,
                paymentId = paymentId,
                message = "¡Pago aprobado exitosamente! Tu compra ha sido procesada correctamente."
            )
            isPendingPath -> PaymentResult(
                status = PaymentResultStatus.PENDING,
                paymentId = paymentId,
                message = buildPendingMessage(paymentType, errorDescription)
            )
            isFailurePath -> PaymentResult(
                status = PaymentResultStatus.FAILURE,
                paymentId = paymentId,
                message = buildFailureMessage(errorMessage, errorDescription, paymentType)
            )
            // Caso 3: Sin información clara
            else -> PaymentResult(
                status = PaymentResultStatus.CANCELLED,
                paymentId = paymentId,
                message = "No se pudo determinar el estado del pago. Por favor, verifica en tu cuenta de MercadoPago."
            )
        }
    }
    
    /**
     * Construye un mensaje descriptivo para pagos pendientes
     */
    private fun buildPendingMessage(paymentType: String?, errorDescription: String?): String {
        val baseMessage = "Tu pago está pendiente de confirmación."
        
        val additionalInfo = when (paymentType?.lowercase()) {
            "ticket", "atm" -> " Debes completar el pago en el punto de pago seleccionado."
            "bank_transfer" -> " Debes realizar la transferencia bancaria para completar el pago."
            "account_money" -> " El pago se procesará desde tu cuenta de MercadoPago."
            else -> ""
        }
        
        val description = errorDescription?.let { " $it" } ?: ""
        
        return baseMessage + additionalInfo + description
    }
    
    /**
     * Construye un mensaje descriptivo para pagos fallidos
     */
    private fun buildFailureMessage(
        errorMessage: String?,
        errorDescription: String?,
        paymentType: String?
    ): String {
        val baseMessage = when (errorMessage?.lowercase()) {
            "cc_rejected_insufficient_amount" -> "Fondos insuficientes en la tarjeta."
            "cc_rejected_bad_filled_security_code" -> "Código de seguridad incorrecto."
            "cc_rejected_bad_filled_date" -> "Fecha de vencimiento incorrecta."
            "cc_rejected_bad_filled_card_number" -> "Número de tarjeta incorrecto."
            "cc_rejected_other_reason" -> "La tarjeta fue rechazada."
            "cc_rejected_call_for_authorize" -> "Debes autorizar el pago con tu banco."
            "cc_rejected_duplicated_payment" -> "Este pago ya fue procesado anteriormente."
            else -> "El pago fue rechazado."
        }
        
        val description = errorDescription?.let { " $it" } ?: ""
        val suggestion = " Por favor, intenta con otro método de pago o verifica los datos ingresados."
        
        return baseMessage + description + suggestion
    }
}

