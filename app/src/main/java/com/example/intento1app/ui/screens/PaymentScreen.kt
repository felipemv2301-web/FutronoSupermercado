package com.example.intento1app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.intento1app.data.models.*
import com.example.intento1app.viewmodel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    cartItems: List<CartItem>,
    currentUser: User? = null, // Agregar usuario actual
    onPaymentComplete: () -> Unit,
    onBackToCart: () -> Unit,
    onNavigateToCheckout: (String) -> Unit = {}, // Nueva función para navegar al checkout
    viewModel: PaymentViewModel = PaymentViewModel()
) {
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()
    val paymentSummary by viewModel.paymentSummary.collectAsStateWithLifecycle()
    val currentError by viewModel.currentError.collectAsStateWithLifecycle()
    
    // Manejar redirección a Mercado Pago cuando el pago esté pendiente
    LaunchedEffect(uiState) {
        println("PaymentScreen: LaunchedEffect ejecutado - paymentState: $paymentState, hasResponse: ${uiState.currentPaymentResponse != null}")
        println("PaymentScreen: currentPaymentResponse: ${uiState.currentPaymentResponse}")
        
        if (paymentState == PaymentState.PENDING && uiState.currentPaymentResponse != null) {
            val paymentResponse = uiState.currentPaymentResponse!!
            println("PaymentScreen: Redirigiendo a Mercado Pago: ${paymentResponse.initPoint}")
            
            // Redirigir a la pantalla de checkout de Mercado Pago
            onNavigateToCheckout(paymentResponse.initPoint)
        } else {
            println("PaymentScreen: No se cumple condición para redirección - paymentState: $paymentState, response: ${uiState.currentPaymentResponse}")
        }
    }
    
    // Estado para el formulario de invitado
    var guestName by remember { mutableStateOf("") }
    var guestPhone by remember { mutableStateOf("") }
    var showGuestForm by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf("") }
    
    LaunchedEffect(cartItems) {
        if (cartItems.isNotEmpty()) {
            viewModel.preparePaymentSummary(cartItems)
        }
    }
    
    // Mostrar formulario de invitado si es necesario
    LaunchedEffect(currentUser) {
        if (currentUser?.id == "guest") {
            showGuestForm = true
        }
    }
    
    // Ya no necesitamos estos efectos porque eliminamos la validación de pago
    
    // Función para validar número de teléfono chileno
    fun validateChileanPhone(phone: String): String {
        // Limpiar el número (quitar espacios, guiones, etc.)
        val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
        
        // Verificar si está vacío
        if (cleanPhone.isEmpty()) {
            return "El teléfono es obligatorio"
        }
        
        // Verificar si comienza con +56 (código de Chile)
        if (cleanPhone.startsWith("+56")) {
            val number = cleanPhone.substring(3)
            // Debe tener 9 dígitos después del +56
            if (number.length == 9 && number.matches(Regex("^[2-9][0-9]{8}$"))) {
                return "" // Válido
            }
        }
        
        // Verificar si comienza con 56 (sin +)
        if (cleanPhone.startsWith("56")) {
            val number = cleanPhone.substring(2)
            // Debe tener 9 dígitos después del 56
            if (number.length == 9 && number.matches(Regex("^[2-9][0-9]{8}$"))) {
                return "" // Válido
            }
        }
        
        // Verificar si es solo el número (9 dígitos)
        if (cleanPhone.length == 9 && cleanPhone.matches(Regex("^[2-9][0-9]{8}$"))) {
            return "" // Válido
        }
        
        // Si no cumple ningún patrón
        return "Formato inválido. Use: +56912345678, 56912345678 o 912345678"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago Seguro") },
                navigationIcon = {
                    IconButton(onClick = onBackToCart) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Header de seguridad
            item {
                SecurityHeader()
            }
            
            // Resumen de compra
            item {
                PaymentSummaryCard(
                    paymentSummary = paymentSummary,
                    cartItems = cartItems
                )
            }
            
            // Métodos de pago
            item {
                PaymentMethodsCard()
            }
            
            // Formulario de invitado
            if (showGuestForm && currentUser?.id == "guest") {
                item {
                    GuestInfoForm(
                        guestName = guestName,
                        guestPhone = guestPhone,
                        phoneError = phoneError,
                        onNameChange = { guestName = it },
                        onPhoneChange = { 
                            guestPhone = it
                            // Limpiar error cuando el usuario empiece a escribir
                            if (phoneError.isNotEmpty()) {
                                phoneError = ""
                            }
                        },
                        onContinue = {
                            // Validar teléfono
                            phoneError = validateChileanPhone(guestPhone)
                            
                            if (guestName.isNotEmpty() && guestPhone.isNotEmpty() && phoneError.isEmpty()) {
                                // Ocultar formulario y mostrar botón de pago
                                showGuestForm = false
                            }
                        }
                    )
                }
            }
            
            // Botón de pago (siempre visible después del formulario o para usuarios autenticados)
            if (!showGuestForm || currentUser?.id != "guest") {
                item {
                    PaymentButton(
                        paymentState = paymentState,
                        isLoading = uiState.isLoading,
                        isEnabled = cartItems.isNotEmpty() && (!showGuestForm || (guestName.isNotEmpty() && guestPhone.isNotEmpty() && phoneError.isEmpty())),
                        onPaymentClick = {
                            println("PaymentScreen: Botón de pago presionado - Iniciando flujo de Mercado Pago")
                            println("PaymentScreen: CartItems: ${cartItems.size} items")
                            println("PaymentScreen: Current user: ${currentUser?.email}")
                            
                            // Iniciar el proceso de pago con Mercado Pago
                            viewModel.initiatePayment(cartItems)
                            
                            // Verificación manual como respaldo
                            kotlinx.coroutines.GlobalScope.launch {
                                kotlinx.coroutines.delay(1000) // Esperar 1 segundo
                                val currentState = viewModel.uiState.value
                                val currentPaymentState = viewModel.paymentState.value
                                
                                println("PaymentScreen: Verificación manual - paymentState: $currentPaymentState, hasResponse: ${currentState.currentPaymentResponse != null}")
                                
                                if (currentPaymentState == PaymentState.PENDING && currentState.currentPaymentResponse != null) {
                                    val paymentResponse = currentState.currentPaymentResponse!!
                                    println("PaymentScreen: Redirigiendo manualmente a Mercado Pago: ${paymentResponse.initPoint}")
                                    onNavigateToCheckout(paymentResponse.initPoint)
                                }
                            }
                        }
                    )
                }
            }
            
            // Información adicional
            item {
                AdditionalInfoCard()
            }
            
            // Ya no mostramos estado del pago porque eliminamos la validación
            
            // Manejo de errores
            currentError?.let { error ->
                item {
                    ErrorCard(
                        error = error,
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Seguridad",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
            Text(
                text = "Confirmación de Pedido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "Tu pedido será procesado y aparecerá en tu historial de compras",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            }
        }
    }
}

@Composable
private fun PaymentSummaryCard(
    paymentSummary: PaymentSummary?,
    cartItems: List<CartItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen de Compra",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Items del carrito
            cartItems.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.product.name} x${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$${String.format("%,.0f", item.totalPrice).replace(",", ".")}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
                        // Totales
            if (paymentSummary != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total a Pagar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${paymentSummary.total.toInt()}", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                // Mostrar total calculado directamente si no hay PaymentSummary
                val total = cartItems.sumOf { it.totalPrice }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total a Pagar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${total.toInt()}", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información del Pedido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Pedido",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Pedido Directo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Confirmado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Tu pedido será guardado automáticamente en tu historial de compras",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Ventajas",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Procesamiento inmediato • Historial disponible • Sin validación externa",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun PaymentButton(
    paymentState: PaymentState,
    isLoading: Boolean,
    isEnabled: Boolean,
    onPaymentClick: () -> Unit
) {
    Button(
        onClick = onPaymentClick,
        enabled = isEnabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when (paymentState) {
                PaymentState.PENDING -> Color(0xFF00A1E0) // Color de Mercado Pago
                else -> MaterialTheme.colorScheme.primary
            }
        )
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Procesando...")
            }
            paymentState == PaymentState.PENDING -> {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Redirigiendo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Redirigiendo a Mercado Pago...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Pagar",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Pagar con Mercado Pago",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GuestInfoForm(
    guestName: String,
    guestPhone: String,
    phoneError: String,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información de Contacto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Como invitado, necesitamos tu información para procesar el pedido",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de nombre
            OutlinedTextField(
                value = guestName,
                onValueChange = onNameChange,
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Campo de teléfono
            OutlinedTextField(
                value = guestPhone,
                onValueChange = onPhoneChange,
                label = { Text("Teléfono") },
                placeholder = { Text("+56912345678 o 912345678") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = phoneError.isNotEmpty(),
                supportingText = if (phoneError.isNotEmpty()) {
                    { Text(phoneError, color = MaterialTheme.colorScheme.error) }
                } else {
                    { Text("Formato: +56912345678, 56912345678 o 912345678") }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón continuar
            Button(
                onClick = onContinue,
                enabled = guestName.isNotEmpty() && guestPhone.isNotEmpty() && phoneError.isEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar al pago")
            }
        }
    }
}

@Composable
private fun AdditionalInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Información",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información Importante",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "• Tu pedido será guardado automáticamente en Firebase\n" +
                       "• Podrás verlo en tu historial de pedidos\n" +
                       "• El procesamiento es inmediato sin validación externa",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PaymentStatusCard(
    paymentState: PaymentState,
    paymentResponse: PaymentResponse?,
    paymentStatus: PaymentStatus?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (paymentState) {
                PaymentState.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                PaymentState.ERROR -> MaterialTheme.colorScheme.errorContainer
                PaymentState.PENDING -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                            Icon(
                    imageVector = when (paymentState) {
                        PaymentState.SUCCESS -> Icons.Default.CheckCircle
                        PaymentState.ERROR -> Icons.Default.Warning
                        PaymentState.PENDING -> Icons.Default.Info
                        else -> Icons.Default.Info
                    },
                contentDescription = "Estado del pago",
                tint = when (paymentState) {
                    PaymentState.SUCCESS -> MaterialTheme.colorScheme.primary
                    PaymentState.ERROR -> MaterialTheme.colorScheme.error
                    PaymentState.PENDING -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = when (paymentState) {
                    PaymentState.SUCCESS -> "¡Pago Exitoso!"
                    PaymentState.ERROR -> "Error en el Pago"
                    PaymentState.PENDING -> "Procesando Pago"
                    else -> "Estado del Pago"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            when (paymentState) {
                PaymentState.SUCCESS -> {
                    paymentStatus?.let { status ->
                        Text(
                            text = "Código de autorización: ${status.authorizationCode}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                PaymentState.ERROR -> {
                    paymentStatus?.let { status ->
                        Text(
                            text = status.responseMessage ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                PaymentState.PENDING -> {
                    paymentResponse?.let { response ->
                        Text(
                            text = "Redirigiendo a Mercado Pago...",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Preferencia ID: ${response.preferenceId}",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                else -> { /* No mostrar nada */ }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    error: PaymentError,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                                    Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = error.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            error.details?.let { details ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
