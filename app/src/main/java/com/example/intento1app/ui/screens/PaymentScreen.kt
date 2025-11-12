package com.example.intento1app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intento1app.data.models.CartItem
import com.example.intento1app.data.models.User
import com.example.intento1app.data.services.MercadoPagoService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    cartItems: List<CartItem>,
    currentUser: User? = null,
    onPaymentComplete: () -> Unit,
    onBackToCart: () -> Unit
) {
    val context = LocalContext.current
    val mercadoPagoService = remember { MercadoPagoService(context) }
    val scope = rememberCoroutineScope()
    
    // Estado para el formulario de invitado
    var guestName by remember { mutableStateOf("") }
    var guestPhone by remember { mutableStateOf("") }
    var showGuestForm by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Mostrar formulario de invitado si es necesario
    LaunchedEffect(currentUser) {
        if (currentUser?.id == "guest") {
            showGuestForm = true
        }
    }

    // Función para validar número de teléfono chileno
    fun validateChileanPhone(phone: String): String {
        val cleanPhone = phone.replace(Regex("[^0-9+]"), "")

        if (cleanPhone.isEmpty()) {
            return "El teléfono es obligatorio"
        }

        if (cleanPhone.startsWith("+56")) {
            val number = cleanPhone.substring(3)
            if (number.length == 9 && number.matches(Regex("^[2-9][0-9]{8}$"))) {
                return ""
            }
        }

        if (cleanPhone.startsWith("56")) {
            val number = cleanPhone.substring(2)
            if (number.length == 9 && number.matches(Regex("^[2-9][0-9]{8}$"))) {
                return ""
            }
        }

        if (cleanPhone.length == 9 && cleanPhone.matches(Regex("^[2-9][0-9]{8}$"))) {
            return ""
        }

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
            item(key = "security_header") {
                SecurityHeader()
            }

            // Resumen de compra
            item(key = "payment_summary") {
                PaymentSummaryCard(cartItems = cartItems)
            }

            // Métodos de pago
            item(key = "payment_methods") {
                PaymentMethodsCard()
            }

            // Formulario de invitado
            if (showGuestForm && currentUser?.id == "guest") {
                item(key = "guest_form") {
                    GuestInfoForm(
                        guestName = guestName,
                        guestPhone = guestPhone,
                        phoneError = phoneError,
                        onNameChange = { guestName = it },
                        onPhoneChange = {
                            guestPhone = it
                            if (phoneError.isNotEmpty()) {
                                phoneError = ""
                            }
                        },
                        onContinue = {
                            phoneError = validateChileanPhone(guestPhone)
                            if (guestName.isNotEmpty() && guestPhone.isNotEmpty() && phoneError.isEmpty()) {
                                showGuestForm = false
                            }
                        }
                    )
                }
            }

            // Botón de pago con Mercado Pago
            item(key = "payment_button") {
                val buttonEnabled = cartItems.isNotEmpty() && (!showGuestForm || (guestName.isNotEmpty() && guestPhone.isNotEmpty() && phoneError.isEmpty())) && !isLoading
                
                PaymentButton(
                    isLoading = isLoading,
                    isEnabled = buttonEnabled,
                    onPaymentClick = {
                        if (!isLoading && buttonEnabled) {
                            scope.launch {
                                try {
                                    isLoading = true
                                    val result = mercadoPagoService.createPaymentPreference(cartItems)
                                    result.onSuccess { (checkoutUrl, preferenceId) ->
                                        mercadoPagoService.openCheckout(checkoutUrl, preferenceId)
                                        // El estado se resetea cuando se abre el checkout
                                        isLoading = false
                                    }.onFailure { error ->
                                        val errorMessage = error.message ?: "Error desconocido"
                                        android.util.Log.e("MercadoPago", "Error: $errorMessage", error)
                                        
                                        // Mostrar mensaje de error al usuario
                                        android.widget.Toast.makeText(
                                            context,
                                            "Error: $errorMessage",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                        
                                        isLoading = false
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("MercadoPago", "Error inesperado: ${e.message}", e)
                                    isLoading = false
                                }
                            }
                        }
                    }
                )
            }

            // Información adicional
            item(key = "additional_info") {
                AdditionalInfoCard()
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
private fun PaymentSummaryCard(cartItems: List<CartItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen de Compra",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Items del carrito
            cartItems.forEach { cartItem ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${cartItem.product.name} x${cartItem.quantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$${String.format("%,.0f", cartItem.totalPrice).replace(",", ".")}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Total
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

@Composable
private fun PaymentMethodsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
    isLoading: Boolean,
    isEnabled: Boolean,
    onPaymentClick: () -> Unit
) {
    Button(
        onClick = onPaymentClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF009EE3) // Color de Mercado Pago
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Procesando...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        } else {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Pagar",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Pagar con Mercado Pago",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
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
        Column(modifier = Modifier.padding(16.dp)) {
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

            OutlinedTextField(
                value = guestName,
                onValueChange = onNameChange,
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
