package com.example.intento1app.data.repository

import com.example.intento1app.data.models.*
import com.example.intento1app.data.services.PaymentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class PaymentRepository(
    private val paymentService: PaymentService = PaymentService()
) {
    
    /**
     * Inicia el proceso de pago
     */
    suspend fun initiatePayment(cartItems: List<CartItem>): Result<PaymentResponse> {
        return try {
            println("PaymentRepository: Iniciando pago con ${cartItems.size} items")
            
            // Generar IDs únicos
            val orderId = paymentService.generateOrderId()
            val sessionId = paymentService.generateSessionId()
            println("🆔 PaymentRepository: OrderId: $orderId, SessionId: $sessionId")
            
            // Crear resumen de pago
            val paymentSummary = PaymentSummary.fromCartItems(cartItems)
            println("PaymentRepository: Total: ${paymentSummary.total}")
            
            // Crear solicitud de pago
            val paymentRequest = PaymentRequest(
                amount = paymentSummary.total,
                orderId = orderId,
                sessionId = sessionId,
                returnUrl = "intento1app://payment-result",
                items = cartItems
            )
            
            println("PaymentRepository: Llamando a PaymentService...")
            // Iniciar pago con Mercado Pago
            paymentService.initiatePayment(paymentRequest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Verifica el estado de una transacción en Mercado Pago
     */
    suspend fun checkPaymentStatus(paymentId: Long, orderId: String): Result<PaymentStatus> {
        return try {
            paymentService.checkPaymentStatus(paymentId, orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Procesa la respuesta de Mercado Pago
     */
    suspend fun processMercadoPagoResponse(
        paymentId: Long,
        orderId: String,
        sessionId: String,
        status: String,
        statusDetail: String? = null
    ): Result<PaymentStatus> {
        return try {
            paymentService.processMercadoPagoResponse(
                paymentId = paymentId,
                orderId = orderId,
                sessionId = sessionId,
                status = status,
                statusDetail = statusDetail
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene el resumen de pago para los items del carrito
     */
    fun getPaymentSummary(cartItems: List<CartItem>): Flow<PaymentSummary> = flow {
        val summary = PaymentSummary.fromCartItems(cartItems)
        emit(summary)
    }
    
    /**
     * Valida que el carrito esté listo para el pago
     */
    fun validateCartForPayment(cartItems: List<CartItem>): Result<Unit> {
        return try {
            require(cartItems.isNotEmpty()) { "El carrito está vacío" }
            require(cartItems.all { it.quantity > 0 }) { "Cantidades inválidas en el carrito" }
            require(cartItems.all { it.product.isAvailable }) { "Algunos productos no están disponibles" }
            require(cartItems.all { it.product.stock >= it.quantity }) { "Stock insuficiente para algunos productos" }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Simula el procesamiento de una transacción exitosa
     */
    suspend fun simulateSuccessfulPayment(
        orderId: String,
        sessionId: String,
        amount: Double
    ): Result<PaymentStatus> {
        return try {
            // Simular delay de procesamiento
            kotlinx.coroutines.delay(2000)
            
            val paymentStatus = PaymentStatus(
                orderId = orderId,
                sessionId = sessionId,
                status = PaymentState.SUCCESS,
                amount = amount,
                authorizationCode = "AUTH${Random().nextInt(999999)}",
                responseCode = "0",
                responseMessage = "Transacción aprobada",
                transactionDate = Date().toString()
            )
            
            Result.success(paymentStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene el historial de transacciones (simulado)
     */
    fun getTransactionHistory(): Flow<List<PaymentStatus>> = flow {
        // Simular historial de transacciones
        val history = listOf(
            PaymentStatus(
                orderId = "ORD-1234567890",
                sessionId = "SESS-1234567890",
                status = PaymentState.SUCCESS,
                amount = 45000.0,
                authorizationCode = "AUTH123456",
                responseCode = "0",
                responseMessage = "Transacción aprobada",
                transactionDate = "2024-01-15 14:30:00"
            ),
            PaymentStatus(
                orderId = "ORD-0987654321",
                sessionId = "SESS-0987654321",
                status = PaymentState.SUCCESS,
                amount = 32000.0,
                authorizationCode = "AUTH654321",
                responseCode = "0",
                responseMessage = "Transacción aprobada",
                transactionDate = "2024-01-14 16:45:00"
            )
        )
        
        emit(history)
    }
    
    /**
     * Obtiene la public key de Mercado Pago
     */
    fun getMercadoPagoPublicKey(): String {
        return paymentService.getPublicKey()
    }
    
    /**
     * Verifica si está en modo sandbox
     */
    fun isSandboxMode(): Boolean {
        return paymentService.isSandboxMode()
    }
    
    /**
     * Procesa la respuesta de WebPay (mantenido para compatibilidad)
     */
    suspend fun processWebPayResponse(
        token: String,
        orderId: String,
        sessionId: String,
        responseCode: String,
        authorizationCode: String? = null
    ): Result<PaymentStatus> {
        return try {
            // Convertir a formato de Mercado Pago para compatibilidad
            val paymentId = token.toLongOrNull() ?: 0L
            val status = when (responseCode) {
                "0" -> "approved"
                "1" -> "rejected"
                "2" -> "cancelled"
                else -> "error"
            }
            
            processMercadoPagoResponse(
                paymentId = paymentId,
                orderId = orderId,
                sessionId = sessionId,
                status = status,
                statusDetail = authorizationCode
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
