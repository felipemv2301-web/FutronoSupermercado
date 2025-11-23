package com.example.intento1app.data.services

import android.util.Log
import com.example.intento1app.data.config.EmailJSConfig
import com.example.intento1app.data.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Servicio para enviar emails usando EmailJS
 */
class EmailJSService {
    
    private val client = OkHttpClient()
    private val mediaType = "application/json".toMediaType()
    
    /**
     * Envía un email de confirmación de compra al cliente
     * 
     * @param userName Nombre del usuario
     * @param userEmail Email del usuario
     * @param orderNumber Número de pedido/tracking
     * @param totalPrice Precio total de la compra
     * @param subtotal Subtotal de la compra
     * @param iva IVA de la compra
     * @param shipping Costo de envío
     * @param totalItems Cantidad total de items
     * @param cartItems Lista de items comprados
     * @param paymentId ID del pago de MercadoPago
     * @return Result indicando si el email se envió exitosamente
     */
    suspend fun sendPurchaseConfirmationEmail(
        userName: String,
        userEmail: String,
        orderNumber: String,
        totalPrice: Double,
        subtotal: Double,
        iva: Double,
        shipping: Double,
        totalItems: Int,
        cartItems: List<CartItem>,
        paymentId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Verificar que la configuración esté completa
            if (!EmailJSConfig.isConfigured()) {
                Log.w("EmailJSService", "⚠️ EmailJS no está configurado. No se enviará email.")
                return@withContext Result.failure(Exception("EmailJS no está configurado"))
            }
            
            Log.d("EmailJSService", "=== INICIANDO ENVÍO DE EMAIL ===")
            Log.d("EmailJSService", "Destinatario: $userEmail")
            Log.d("EmailJSService", "Orden: $orderNumber")
            
            // Formatear fecha
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))
            val purchaseDate = dateFormat.format(Date())
            
            // Formatear números como enteros (sin decimales ni comas)
            fun formatPrice(price: Double): String {
                return String.format(Locale.US, "%.0f", price)
            }
            
            // Crear array de items para el template
            // EmailJS/Handlebars necesita el array en formato específico
            val itemsArray = JSONArray()
            cartItems.forEach { item ->
                val itemObj = JSONObject().apply {
                    put("name", item.product.name)
                    put("quantity", item.quantity.toString())
                    put("unit_price", formatPrice(item.product.price))
                    put("total_price", formatPrice(item.totalPrice))
                    // EmailJS puede manejar URLs de imagen, si está disponible
                    if (item.product.imageUrl.isNotEmpty()) {
                        put("image_url", item.product.imageUrl)
                    }
                }
                itemsArray.put(itemObj)
            }
            
            // Construir template_params con todas las variables
            val templateParams = JSONObject().apply {
                // Variables que el template espera
                put("to_name", userName)
                put("to_email", userEmail)
                put("tracking_number", orderNumber)
                put("payment_id", paymentId)
                put("purchase_date", purchaseDate)
                put("subtotal", formatPrice(subtotal))
                put("iva", formatPrice(iva))
                if (shipping > 0) {
                    put("shipping", formatPrice(shipping))
                }
                put("total_price", formatPrice(totalPrice))
                // Agregar el array de items directamente
                put("items", itemsArray)
            }
            
            // Construir el cuerpo de la petición
            val requestBody = JSONObject().apply {
                put("service_id", EmailJSConfig.SERVICE_ID)
                put("template_id", EmailJSConfig.TEMPLATE_ID)
                put("user_id", EmailJSConfig.PUBLIC_KEY)
                put("template_params", templateParams)
                
                // Agregar accessToken si está disponible (necesario para modo estricto)
                if (EmailJSConfig.hasPrivateKey()) {
                    put("accessToken", EmailJSConfig.PRIVATE_KEY)
                    Log.d("EmailJSService", "Usando modo estricto con Private Key")
                }
            }
            
            val requestBodyString = requestBody.toString()
            Log.d("EmailJSService", "Request body: $requestBodyString")
            Log.d("EmailJSService", "Service ID: ${EmailJSConfig.SERVICE_ID}")
            Log.d("EmailJSService", "Template ID: ${EmailJSConfig.TEMPLATE_ID}")
            Log.d("EmailJSService", "Public Key: ${EmailJSConfig.PUBLIC_KEY}")
            Log.d("EmailJSService", "Private Key configurada: ${EmailJSConfig.hasPrivateKey()}")
            
            // Crear la petición HTTP
            val requestBuilder = Request.Builder()
                .url(EmailJSConfig.API_BASE_URL)
                .post(requestBodyString.toRequestBody(mediaType))
                .addHeader("Content-Type", "application/json")
            
            // Agregar header de autorización si hay private key (alternativa al campo accessToken)
            if (EmailJSConfig.hasPrivateKey()) {
                requestBuilder.addHeader("Authorization", "Bearer ${EmailJSConfig.PRIVATE_KEY}")
            }
            
            val request = requestBuilder.build()
            
            // Ejecutar la petición
            val response = client.newCall(request).execute()
            
            // Leer el response body una sola vez
            val responseBodyString = response.body?.string() ?: ""
            
            if (response.isSuccessful) {
                Log.d("EmailJSService", "✅ EMAIL ENVIADO EXITOSAMENTE")
                Log.d("EmailJSService", "Response code: ${response.code}")
                Log.d("EmailJSService", "Response body: $responseBodyString")
                Result.success(Unit)
            } else {
                Log.e("EmailJSService", "❌ ERROR AL ENVIAR EMAIL")
                Log.e("EmailJSService", "Response code: ${response.code}")
                Log.e("EmailJSService", "Error body: $responseBodyString")
                Log.e("EmailJSService", "Request que falló: $requestBodyString")
                Result.failure(Exception("Error ${response.code}: $responseBodyString"))
            }
        } catch (e: Exception) {
            Log.e("EmailJSService", "❌ EXCEPCIÓN AL ENVIAR EMAIL")
            Log.e("EmailJSService", "Mensaje: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Envía un email de notificación al administrador sobre una nueva compra
     * (Opcional: requiere un template adicional)
     */
    suspend fun sendAdminNotificationEmail(
        userName: String,
        userEmail: String,
        orderNumber: String,
        totalPrice: Double,
        totalItems: Int,
        paymentId: String
    ): Result<Unit> {
        // Implementación similar a sendPurchaseConfirmationEmail
        // pero con un template diferente para notificar al admin
        // Por ahora, retornamos success sin implementar
        Log.d("EmailJSService", "Notificación a admin no implementada aún")
        return Result.success(Unit)
    }
}

