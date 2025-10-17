package com.example.intento1app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.*
import com.example.intento1app.data.repository.PaymentRepository
import com.example.intento1app.data.services.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PaymentViewModel(
    private val repository: PaymentRepository = PaymentRepository(),
    private val firebaseService: FirebaseService = FirebaseService()
) : ViewModel() {
    
    // Estado de la UI
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()
    
    // Estado del pago
    private val _paymentState = MutableStateFlow(PaymentState.IDLE)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()
    
    // Resumen de pago
    private val _paymentSummary = MutableStateFlow<PaymentSummary?>(null)
    val paymentSummary: StateFlow<PaymentSummary?> = _paymentSummary.asStateFlow()
    
    // Error actual
    private val _currentError = MutableStateFlow<PaymentError?>(null)
    val currentError: StateFlow<PaymentError?> = _currentError.asStateFlow()
    
    // Historial de pedidos del usuario
    private val _userOrders = MutableStateFlow<List<FirebasePurchase>>(emptyList())
    val userOrders: StateFlow<List<FirebasePurchase>> = _userOrders.asStateFlow()
    
    init {
        // Inicializar estado
        _uiState.update { it.copy(isLoading = false) }
    }
    
    /**
     * Prepara el resumen de pago para los items del carrito
     */
    fun preparePaymentSummary(cartItems: List<CartItem>) {
        viewModelScope.launch {
            try {
                // Validar carrito
                val validationResult = repository.validateCartForPayment(cartItems)
                if (validationResult.isFailure) {
                    val error = PaymentError(
                        code = "CART_VALIDATION_ERROR",
                        message = validationResult.exceptionOrNull()?.message ?: "Error de validación del carrito"
                    )
                    _currentError.value = error
                    return@launch
                }
                
                // Obtener resumen de pago
                repository.getPaymentSummary(cartItems).collect { summary ->
                    _paymentSummary.value = summary
                    _uiState.update { it.copy(paymentSummary = summary) }
                }
            } catch (e: Exception) {
                val error = PaymentError(
                    code = "SUMMARY_ERROR",
                    message = "Error al preparar el resumen de pago",
                    details = e.message
                )
                _currentError.value = error
            }
        }
    }
    
    /**
     * Inicia el proceso de pago
     */
    fun initiatePayment(cartItems: List<CartItem>) {
        viewModelScope.launch {
            try {
                println(" PaymentViewModel: Iniciando pago con ${cartItems.size} items")
                _paymentState.value = PaymentState.LOADING
                _uiState.update { it.copy(isLoading = true) }
                _currentError.value = null
                
                // Iniciar pago
                val result = repository.initiatePayment(cartItems)
                
                if (result.isSuccess) {
                    val paymentResponse = result.getOrNull()!!
                    println(" PaymentViewModel: Pago iniciado exitosamente")
                    println("PaymentViewModel: URL de checkout: ${paymentResponse.initPoint}")
                    
                    _uiState.update { 
                        it.copy(
                            currentPaymentResponse = paymentResponse,
                            isLoading = false
                        )
                    }
                    _paymentState.value = PaymentState.PENDING
                    
                    println("PaymentViewModel: Estado actualizado - paymentState: PENDING, hasResponse: ${_uiState.value.currentPaymentResponse != null}")
                    println("PaymentViewModel: currentPaymentResponse: ${_uiState.value.currentPaymentResponse}")
                } else {
                    val exception = result.exceptionOrNull()
                    println(" PaymentViewModel: Error al iniciar pago: ${exception?.message}")
                    val error = PaymentError(
                        code = "PAYMENT_INIT_ERROR",
                        message = "Error al iniciar el pago: ${exception?.message}",
                        details = exception?.message
                    )
                    _currentError.value = error
                    _paymentState.value = PaymentState.ERROR
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                println(" PaymentViewModel: Excepción inesperada: ${e.message}")
                val error = PaymentError(
                    code = "UNEXPECTED_ERROR",
                    message = "Error inesperado: ${e.message}",
                    details = e.message
                )
                _currentError.value = error
                _paymentState.value = PaymentState.ERROR
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * Verifica el estado de una transacción en Mercado Pago
     */
    fun checkPaymentStatus(paymentId: Long, orderId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val result = repository.checkPaymentStatus(paymentId, orderId)
                
                if (result.isSuccess) {
                    val status = result.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            currentPaymentStatus = status,
                            isLoading = false
                        )
                    }
                    _paymentState.value = status.status
                } else {
                    val exception = result.exceptionOrNull()
                    val error = PaymentError(
                        code = "STATUS_CHECK_ERROR",
                        message = "Error al verificar el estado del pago",
                        details = exception?.message
                    )
                    _currentError.value = error
                    _paymentState.value = PaymentState.ERROR
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                val error = PaymentError(
                    code = "UNEXPECTED_ERROR",
                    message = "Error inesperado",
                    details = e.message
                )
                _currentError.value = error
                _paymentState.value = PaymentState.ERROR
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * Procesa la respuesta de Mercado Pago
     */
    fun processMercadoPagoResponse(
        paymentId: Long,
        orderId: String,
        sessionId: String,
        status: String,
        statusDetail: String? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val result = repository.processMercadoPagoResponse(
                    paymentId = paymentId,
                    orderId = orderId,
                    sessionId = sessionId,
                    status = status,
                    statusDetail = statusDetail
                )
                
                if (result.isSuccess) {
                    val paymentStatus = result.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            currentPaymentStatus = paymentStatus,
                            isLoading = false
                        )
                    }
                    _paymentState.value = paymentStatus.status
                } else {
                    val exception = result.exceptionOrNull()
                    val error = PaymentError(
                        code = "MERCADOPAGO_PROCESS_ERROR",
                        message = "Error al procesar la respuesta de Mercado Pago",
                        details = exception?.message
                    )
                    _currentError.value = error
                    _paymentState.value = PaymentState.ERROR
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                val error = PaymentError(
                    code = "UNEXPECTED_ERROR",
                    message = "Error inesperado",
                    details = e.message
                )
                _currentError.value = error
                _paymentState.value = PaymentState.ERROR
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * Procesa la respuesta de WebPay (mantenido para compatibilidad)
     */
    fun processWebPayResponse(
        token: String,
        orderId: String,
        sessionId: String,
        responseCode: String,
        authorizationCode: String? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val result = repository.processWebPayResponse(
                    token = token,
                    orderId = orderId,
                    sessionId = sessionId,
                    responseCode = responseCode,
                    authorizationCode = authorizationCode
                )
                
                if (result.isSuccess) {
                    val status = result.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            currentPaymentStatus = status,
                            isLoading = false
                        )
                    }
                    _paymentState.value = status.status
                } else {
                    val exception = result.exceptionOrNull()
                    val error = PaymentError(
                        code = "WEBPAY_PROCESS_ERROR",
                        message = "Error al procesar la respuesta de WebPay",
                        details = exception?.message
                    )
                    _currentError.value = error
                    _paymentState.value = PaymentState.ERROR
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                val error = PaymentError(
                    code = "UNEXPECTED_ERROR",
                    message = "Error inesperado",
                    details = e.message
                )
                _currentError.value = error
                _paymentState.value = PaymentState.ERROR
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * Limpia el error actual
     */
    fun clearError() {
        _currentError.value = null
    }
    
    /**
     * Resetea el estado del pago
     */
    fun resetPaymentState() {
        _paymentState.value = PaymentState.IDLE
        _uiState.update { 
            it.copy(
                currentPaymentResponse = null,
                currentPaymentStatus = null,
                isLoading = false
            )
        }
        _currentError.value = null
    }
    
    /**
     * Simula un pago exitoso para testing
     */
    fun simulateSuccessfulPayment(
        orderId: String, 
        sessionId: String, 
        amount: Double,
        userId: String? = null,
        userEmail: String? = null,
        userName: String? = null,
        cartItems: List<CartItem>? = null
    ) {
        viewModelScope.launch {
            try {
                _paymentState.value = PaymentState.LOADING
                _uiState.update { it.copy(isLoading = true) }
                
                val result = repository.simulateSuccessfulPayment(orderId, sessionId, amount)
                
                if (result.isSuccess) {
                    val status = result.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            currentPaymentStatus = status,
                            isLoading = false
                        )
                    }
                    _paymentState.value = PaymentState.SUCCESS
                    
                    // Guardar compra en historial si se proporcionan los datos del usuario
                    if (userId != null && userEmail != null && userName != null && cartItems != null) {
                        val paymentId = "sim_${System.currentTimeMillis()}"
                        val orderNumber = "ORD-${orderId.takeLast(6)}"
                        savePurchaseToHistory(userId, userEmail, userName, "", cartItems, paymentId, orderNumber)
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    val error = PaymentError(
                        code = "SIMULATION_ERROR",
                        message = "Error en la simulación del pago",
                        details = exception?.message
                    )
                    _currentError.value = error
                    _paymentState.value = PaymentState.ERROR
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                val error = PaymentError(
                    code = "UNEXPECTED_ERROR",
                    message = "Error inesperado",
                    details = e.message
                )
                _currentError.value = error
                _paymentState.value = PaymentState.ERROR
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * Obtiene la public key de Mercado Pago
     */
    fun getMercadoPagoPublicKey(): String {
        return repository.getMercadoPagoPublicKey()
    }
    
    /**
     * Verifica si está en modo sandbox
     */
    fun isSandboxMode(): Boolean {
        return repository.isSandboxMode()
    }
    
    /**
     * Guarda una compra en el historial del usuario después de un pago exitoso
     */
    fun savePurchaseToHistory(
        userId: String,
        userEmail: String,
        userName: String,
        userPhone: String,
        cartItems: List<CartItem>,
        paymentId: String,
        orderNumber: String
    ) {
        viewModelScope.launch {
            try {
                println(" PaymentViewModel: Guardando compra en historial para usuario: $userId")
                println("PaymentViewModel: Email del usuario: $userEmail")
                println(" PaymentViewModel: Nombre del usuario: $userName")
                println(" PaymentViewModel: Teléfono del usuario: $userPhone")
                println("PaymentViewModel: Items en carrito: ${cartItems.size}")
                
                // Calcular totales
                // Los precios de los productos ya incluyen IVA, sin envío por ahora
                val subtotal = cartItems.sumOf { it.totalPrice }
                val shipping = 0.0 // Sin envío por ahora
                val totalPrice = subtotal // Solo el valor de los productos
                val totalItems = cartItems.sumOf { it.quantity }
                
                // Para el desglose, calculamos el IVA que ya está incluido en el precio
                val ivaIncluded = subtotal * 0.19 / 1.19 // IVA incluido en el precio
                val subtotalWithoutIva = subtotal - ivaIncluded
                
                println("PaymentViewModel: Subtotal (con IVA): $subtotal, IVA incluido: $ivaIncluded, Envío: $shipping, Total: $totalPrice")
                
                // Convertir CartItems a FirebaseCartItems
                val firebaseItems = cartItems.map { cartItem ->
                    FirebaseCartItem(
                        productId = cartItem.product.id,
                        productName = cartItem.product.name,
                        productImageUrl = cartItem.product.imageUrl,
                        quantity = cartItem.quantity,
                        unitPrice = cartItem.product.price,
                        totalPrice = cartItem.totalPrice
                    )
                }
                
                println("PaymentViewModel: Items convertidos: ${firebaseItems.size}")
                
                // Crear objeto de compra
                val purchase = FirebasePurchase(
                    userId = userId,
                    userEmail = userEmail,
                    userName = userName,
                    userPhone = userPhone, // Agregar teléfono del usuario
                    items = firebaseItems,
                    subtotal = subtotalWithoutIva, // Subtotal sin IVA
                    iva = ivaIncluded, // IVA que ya estaba incluido en el precio
                    shipping = shipping,
                    totalPrice = totalPrice, // Total final (subtotal + envío)
                    totalItems = totalItems,
                    paymentMethod = "Mercado Pago",
                    paymentId = paymentId,
                    paymentStatus = "approved",
                    orderNumber = orderNumber,

                )
                
                println("PaymentViewModel: Objeto de compra creado: ${purchase.orderNumber}")
                
                // Ya no necesitamos las pruebas, el guardado funciona correctamente
                
                // Guardar en Firebase usando el nuevo sistema de pedidos por usuario
                println("PaymentViewModel: Llamando a firebaseService.saveUserOrder...")
                val result = firebaseService.saveUserOrder(userId, purchase)
                
                if (result.isSuccess) {
                    val purchaseId = result.getOrNull()!!
                    println(" PaymentViewModel: Pedido guardado exitosamente con ID: $purchaseId")
                    println(" PaymentViewModel: El listener en tiempo real actualizará automáticamente la lista")
                } else {
                    val exception = result.exceptionOrNull()
                    println(" PaymentViewModel: Error al guardar pedido: ${exception?.message}")
                    exception?.printStackTrace()
                }
                
            } catch (e: Exception) {
                println(" PaymentViewModel: Excepción al guardar compra: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Obtiene el historial de compras de un usuario desde su perfil
     * NOTA: Este método ya no se usa, se reemplazó por el listener en tiempo real
     * @deprecated Usar startUserOrdersListener() en su lugar
     */
    @Deprecated("Usar startUserOrdersListener() para obtener datos en tiempo real")
    fun getUserPurchaseHistory(userId: String) {
        // Este método ya no se usa - los datos se obtienen en tiempo real
        println("PaymentViewModel: getUserPurchaseHistory() está deprecado, usar startUserOrdersListener()")
    }
    
    /**
     * Inicia el listener en tiempo real para los pedidos del usuario
     * Se actualiza automáticamente cuando hay cambios en la base de datos
     * Solo muestra pedidos vinculados al userId del usuario autenticado
     */
    fun startUserOrdersListener(userId: String) {
        viewModelScope.launch {
            try {
                println(" PaymentViewModel: Iniciando listener en tiempo real para usuario: $userId")
                println("🆔 PaymentViewModel: ID del usuario recibido: '$userId'")
                
                // Limpiar pedidos anteriores para evitar mostrar datos de otros usuarios
                _userOrders.value = emptyList()
                println("🧹 PaymentViewModel: Lista de pedidos limpiada")
                
                firebaseService.listenToUserOrders(userId).collect { orders ->
                    println("PaymentViewModel: Recibidos ${orders.size} pedidos del listener")
                    
                    // Mostrar información de cada pedido recibido
                    orders.forEachIndexed { index, order ->
                        println("PaymentViewModel: Pedido $index - ID: ${order.id}, UserID: '${order.userId}', OrderNumber: ${order.orderNumber}")
                    }
                    
                    // Filtrar solo pedidos del usuario autenticado (doble verificación)
                    val userOrders = orders.filter { 
                        val matches = it.userId == userId
                        println("PaymentViewModel: Pedido ${it.orderNumber} - UserID: '${it.userId}' == '$userId' ? $matches")
                        matches
                    }
                    
                    println(" PaymentViewModel: Después del filtro: ${userOrders.size} pedidos para usuario $userId")
                    _userOrders.value = userOrders
                }
            } catch (e: Exception) {
                println(" PaymentViewModel: Error en listener de pedidos: ${e.message}")
                e.printStackTrace()
                // En caso de error, limpiar la lista
                _userOrders.value = emptyList()
            }
        }
    }
    
    /**
     * Obtiene un pedido específico del usuario
     */
    fun getUserOrder(userId: String, orderId: String) {
        viewModelScope.launch {
            try {
                println(" PaymentViewModel: Obteniendo pedido específico: $orderId")
                
                val result = firebaseService.getUserOrder(userId, orderId)
                
                if (result.isSuccess) {
                    val order = result.getOrNull()
                    println(" PaymentViewModel: Pedido obtenido: ${order?.id ?: "No encontrado"}")
                    // Aquí podrías actualizar un StateFlow específico para el pedido
                } else {
                    val exception = result.exceptionOrNull()
                    println(" PaymentViewModel: Error al obtener pedido: ${exception?.message}")
                }
            } catch (e: Exception) {
                println(" PaymentViewModel: Excepción al obtener pedido: ${e.message}")
            }
        }
    }
    
    /**
     * Método temporal para debug - verificar todos los pedidos en la base de datos
     */
    fun debugAllOrders() {
        viewModelScope.launch {
            try {
                println("PaymentViewModel: Iniciando debug de todos los pedidos")
                val result = firebaseService.debugAllOrders()
                
                if (result.isSuccess) {
                    val allOrders = result.getOrNull()!!
                    println(" PaymentViewModel: Debug completado - ${allOrders.size} pedidos encontrados en total")
                } else {
                    val exception = result.exceptionOrNull()
                    println(" PaymentViewModel: Error en debug: ${exception?.message}")
                }
            } catch (e: Exception) {
                println(" PaymentViewModel: Excepción en debug: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Actualiza el estado de un pedido del usuario
     */
    fun updateUserOrderStatus(userId: String, orderId: String, status: String) {
        viewModelScope.launch {
            try {
                println(" PaymentViewModel: Actualizando estado del pedido: $orderId -> $status")
                
                val result = firebaseService.updateUserOrderStatus(userId, orderId, status)
                
                if (result.isSuccess) {
                    println(" PaymentViewModel: Estado del pedido actualizado exitosamente")
                } else {
                    val exception = result.exceptionOrNull()
                    println(" PaymentViewModel: Error al actualizar estado: ${exception?.message}")
                }
            } catch (e: Exception) {
                println(" PaymentViewModel: Excepción al actualizar estado: ${e.message}")
            }
        }
    }
    
    /**
     * Cambia el estado de un pedido (para trabajadores)
     */
    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                println("PaymentViewModel: Actualizando estado del pedido $orderId a $newStatus")
                
                val result = firebaseService.updateOrderStatus(orderId, newStatus)
                
                if (result.isSuccess) {
                    println("✅ PaymentViewModel: Estado del pedido actualizado exitosamente")
                    // Refrescar la lista de pedidos (usar listener de todas las órdenes)
                    startAllOrdersListener()
                } else {
                    val exception = result.exceptionOrNull()
                    println("❌ PaymentViewModel: Error al actualizar estado: ${exception?.message}")
                }
            } catch (e: Exception) {
                println("PaymentViewModel: Excepción al actualizar estado: ${e.message}")
            }
        }
    }
    
    /**
     * Marca un pedido como listo (para trabajadores)
     */
    fun markOrderAsReady(orderId: String) {
        updateOrderStatus(orderId, "pedido_listo")
    }
    
    /**
     * Marca un pedido como en preparación (para trabajadores)
     */
    fun markOrderAsInPreparation(orderId: String) {
        updateOrderStatus(orderId, "en_preparacion")
    }
    
    /**
     * Inicia el listener de todas las órdenes (para trabajadores)
     */
    fun startAllOrdersListener() {
        viewModelScope.launch {
            try {
                println("PaymentViewModel: Iniciando listener de todas las órdenes...")
                firebaseService.listenToAllOrders().collect { orders ->
                    _userOrders.value = orders
                    println("✅ PaymentViewModel: Todas las órdenes actualizadas: ${orders.size} órdenes")
                }
            } catch (e: Exception) {
                println("PaymentViewModel: Error en listener de todas las órdenes: ${e.message}")
            }
        }
    }
}

/**
 * Estado de la UI del pago
 */
data class PaymentUiState(
    val isLoading: Boolean = false,
    val paymentSummary: PaymentSummary? = null,
    val currentPaymentResponse: PaymentResponse? = null,
    val currentPaymentStatus: PaymentStatus? = null
)
