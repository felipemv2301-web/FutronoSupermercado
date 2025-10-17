package com.example.intento1app.data.services

import com.example.intento1app.data.models.*
import kotlinx.coroutines.delay
import java.util.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class PaymentService {
    
    companion object {
        // Credenciales de Mercado Pago Chile - CREDENCIALES REALES DE PRUEBA
        private const val MERCADOPAGO_ACCESS_TOKEN = "TEST-4475320570080614-100917-971534aed0d731aa024ff5ce5cff5bc3-249609398" // Token de prueba real
        private const val MERCADOPAGO_PUBLIC_KEY = "TEST-b5913914-0d55-4bd9-b802-4524602295d8" // Public key de prueba real
        private const val MERCADOPAGO_ENVIRONMENT = "sandbox" // sandbox, production
        private const val MERCADOPAGO_BASE_URL = "https://api.mercadopago.com"
        
        // URLs de retorno
        private const val SUCCESS_URL = "intento1app://payment-success"
        private const val FAILURE_URL = "intento1app://payment-failure"
        private const val PENDING_URL = "intento1app://payment-pending"
        
        // URL base para checkout
        private const val CHECKOUT_BASE_URL = "https://www.mercadopago.cl/checkout/v1/redirect?pref_id="
    }
    
    private val apiService: MercadoPagoApiService by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(MERCADOPAGO_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        retrofit.create(MercadoPagoApiService::class.java)
    }
    
    /**
     * Inicia una transacción de pago con Mercado Pago
     */
    suspend fun initiatePayment(paymentRequest: PaymentRequest): Result<PaymentResponse> {
        return try {
            println("PaymentService: Iniciando pago para orden ${paymentRequest.orderId}")
            println("PaymentService: Monto total: ${paymentRequest.amount}")
            println("PaymentService: Items: ${paymentRequest.items.size}")
            println("PaymentService: Usando Access Token: ${MERCADOPAGO_ACCESS_TOKEN.take(20)}...")
            
            // Validar datos de pago
            validatePaymentRequest(paymentRequest)
            
            // Crear preferencia de Mercado Pago
            val preferenceRequest = createMercadoPagoPreference(paymentRequest)
            println("PaymentService: Preferencia creada con ${preferenceRequest.items.size} items")
            
            // Integración real con Mercado Pago
            println("PaymentService: Conectando con Mercado Pago API...")
            
            try {
                // Log del JSON que se enviará a Mercado Pago
                println("PaymentService: Enviando preferencia a Mercado Pago:")
                println("PaymentService: Items: ${preferenceRequest.items.map { "${it.title} - $${it.unitPrice/100.0} x${it.quantity}" }}")
                println("PaymentService: External Reference: ${preferenceRequest.externalReference}")
                println("PaymentService: Auto Return: ${preferenceRequest.autoReturn}")
                
                // Crear preferencia real en Mercado Pago
                val response = apiService.createPreference(
                    authorization = "Bearer $MERCADOPAGO_ACCESS_TOKEN",
                    preference = preferenceRequest
                )
                
                if (response.isSuccessful) {
                    val preference = response.body()
                    if (preference != null) {
                        println("✅ PaymentService: Preferencia creada exitosamente en Mercado Pago")
                        println("🆔 PaymentService: ID de preferencia: ${preference.id}")
                        println("PaymentService: URL de checkout: ${preference.initPoint}")
                        println("PaymentService: Sandbox URL: ${preference.sandboxInitPoint}")
                        
                        val paymentResponse = PaymentResponse(
                            preferenceId = preference.id,
                            initPoint = preference.initPoint,
                            orderId = paymentRequest.orderId,
                            sessionId = paymentRequest.sessionId,
                            sandboxInitPoint = preference.sandboxInitPoint ?: preference.initPoint
                        )
                        return Result.success(paymentResponse)
                    } else {
                        println("❌ PaymentService: Respuesta vacía de Mercado Pago")
                        return Result.failure(Exception("Respuesta vacía de Mercado Pago"))
                    }
                } else {
                    println("❌ PaymentService: Error en API de Mercado Pago: ${response.code()} - ${response.message()}")
                    return Result.failure(Exception("Error en API de Mercado Pago: ${response.code()}"))
                }
            } catch (e: Exception) {
                println("❌ PaymentService: Error de conexión con Mercado Pago: ${e.message}")
                return Result.failure(e)
            }
        } catch (e: Exception) {
            println("PaymentService: Excepción: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Verifica el estado de una transacción en Mercado Pago
     */
    suspend fun checkPaymentStatus(paymentId: Long, orderId: String): Result<PaymentStatus> {
        return try {
            // Buscar pagos por external_reference (orderId)
            val response = apiService.searchPayments(
                authorization = "Bearer $MERCADOPAGO_ACCESS_TOKEN",
                externalReference = orderId
            )
            
            if (response.isSuccessful) {
                val searchResponse = response.body()!!
                if (searchResponse.results.isNotEmpty()) {
                    val payment = searchResponse.results.first()
                    val status = mapMercadoPagoStatusToPaymentState(payment.status)
                    
                    val paymentStatus = PaymentStatus(
                        orderId = orderId,
                        sessionId = payment.orderId ?: "",
                        status = status,
                        amount = payment.transactionAmount ?: 0.0,
                        authorizationCode = payment.id?.toString(),
                        responseCode = payment.status,
                        responseMessage = payment.statusDetail,
                        transactionDate = payment.dateApproved ?: payment.dateCreated
                    )
                    Result.success(paymentStatus)
                } else {
                    Result.failure(Exception("No se encontró el pago"))
                }
            } else {
                Result.failure(Exception("Error al verificar pago: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Procesa la respuesta de Mercado Pago después del pago
     */
    suspend fun processMercadoPagoResponse(
        paymentId: Long,
        orderId: String,
        sessionId: String,
        status: String,
        statusDetail: String? = null
    ): Result<PaymentStatus> {
        return try {
            // Obtener detalles del pago desde Mercado Pago
            val response = apiService.getPayment(
                authorization = "Bearer $MERCADOPAGO_ACCESS_TOKEN",
                paymentId = paymentId
            )
            
            if (response.isSuccessful) {
                val payment = response.body()!!
                val paymentState = mapMercadoPagoStatusToPaymentState(payment.status)
            
            val paymentStatus = PaymentStatus(
                orderId = orderId,
                sessionId = sessionId,
                    status = paymentState,
                    amount = payment.transactionAmount ?: 0.0,
                    authorizationCode = payment.id?.toString(),
                    responseCode = payment.status,
                    responseMessage = payment.statusDetail,
                    transactionDate = payment.dateApproved ?: payment.dateCreated
            )
            
            Result.success(paymentStatus)
            } else {
                Result.failure(Exception("Error al obtener pago: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Valida los datos de pago antes de enviar a Mercado Pago
     */
    private fun validatePaymentRequest(paymentRequest: PaymentRequest) {
        require(paymentRequest.amount > 0) { "El monto debe ser mayor a 0" }
        require(paymentRequest.orderId.isNotBlank()) { "El ID de orden es requerido" }
        require(paymentRequest.sessionId.isNotBlank()) { "El ID de sesión es requerido" }
        require(paymentRequest.items.isNotEmpty()) { "El carrito no puede estar vacío" }
        require(paymentRequest.currency == "CLP") { "Solo se acepta moneda CLP" }
    }
    
    /**
     * Crea una preferencia de Mercado Pago a partir de la solicitud de pago
     */
    private fun createMercadoPagoPreference(paymentRequest: PaymentRequest): MercadoPagoPreferenceRequest {
        val items = paymentRequest.items.map { cartItem ->
            // Los precios ya están en pesos chilenos, enviar directamente
            val unitPrice = cartItem.product.price.toInt()
            println("PaymentService: Item - ${cartItem.product.name}: precio=${cartItem.product.price}, unitPrice=$unitPrice, cantidad=${cartItem.quantity}")
            
            MercadoPagoItem(
                id = cartItem.product.id,
                title = cartItem.product.name,
                description = cartItem.product.description,
                pictureUrl = cartItem.product.imageUrl,
                categoryId = cartItem.product.category.name.lowercase(), // Usar la categoría del producto
                quantity = cartItem.quantity,
                currencyId = "CLP",
                unitPrice = unitPrice // Precio en pesos chilenos (entero)
            )
        }
        
        val backUrls = MercadoPagoBackUrls(
            success = SUCCESS_URL,
            failure = FAILURE_URL,
            pending = PENDING_URL
        )
        
        return MercadoPagoPreferenceRequest(
            items = items,
            backUrls = backUrls,
            autoReturn = "approved",
            externalReference = paymentRequest.orderId,
            expires = true,
            statementDescriptor = "Mi Tienda"
        )
    }
    
    /**
     * Mapea el estado de Mercado Pago al estado interno de la aplicación
     */
    private fun mapMercadoPagoStatusToPaymentState(status: String?): PaymentState {
        return when (status?.lowercase()) {
            "approved" -> PaymentState.SUCCESS
            "pending" -> PaymentState.PENDING
            "in_process" -> PaymentState.PENDING
            "rejected" -> PaymentState.FAILED
            "cancelled" -> PaymentState.CANCELLED
            "refunded" -> PaymentState.CANCELLED
            "charged_back" -> PaymentState.CANCELLED
            else -> PaymentState.ERROR
        }
    }
    
    /**
     * Genera un ID de orden único
     */
    fun generateOrderId(): String {
        return "ORD-${System.currentTimeMillis()}-${Random().nextInt(9999)}"
    }
    
    /**
     * Genera un ID de sesión único
     */
    fun generateSessionId(): String {
        return "SESS-${System.currentTimeMillis()}-${Random().nextInt(9999)}"
    }
    
    /**
     * Obtiene la public key de Mercado Pago para el SDK
     */
    fun getPublicKey(): String {
        return MERCADOPAGO_PUBLIC_KEY
    }
    
    /**
     * Verifica si está en modo sandbox
     */
    fun isSandboxMode(): Boolean {
        return MERCADOPAGO_ENVIRONMENT == "sandbox"
    }
}
