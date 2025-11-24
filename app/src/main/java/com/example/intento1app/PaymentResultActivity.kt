package com.example.intento1app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.PaymentResultStatus
import com.example.intento1app.data.models.PaymentResult
import com.example.intento1app.data.models.CartItem
import com.example.intento1app.data.models.User
import com.example.intento1app.data.services.MercadoPagoService
import com.example.intento1app.data.services.FirebaseService
import com.example.intento1app.ui.theme.AccessibleFutronoTheme
import com.example.intento1app.viewmodel.AccessibilityViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.SharedPreferences
import com.example.intento1app.ui.theme.FutronoBlanco
import com.example.intento1app.ui.theme.FutronoCafe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

/**
 * Activity que maneja el retorno de MercadoPago después del pago
 */
class PaymentResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Log para debugging
        android.util.Log.d("PaymentResult", "Activity iniciada")
        android.util.Log.d("PaymentResult", "Intent: ${intent}")
        android.util.Log.d("PaymentResult", "Intent data: ${intent.data}")
        android.util.Log.d("PaymentResult", "Intent extras: ${intent.extras}")
        
        val mercadoPagoService = MercadoPagoService(this)
        val uri = intent.data
        
        if (uri != null) {
            android.util.Log.d("PaymentResult", "URI recibida: $uri")
            android.util.Log.d("PaymentResult", "URI scheme: ${uri.scheme}")
            android.util.Log.d("PaymentResult", "URI host: ${uri.host}")
            android.util.Log.d("PaymentResult", "URI path: ${uri.path}")
            android.util.Log.d("PaymentResult", "URI query: ${uri.query}")
            
            val paymentResult = mercadoPagoService.processPaymentResult(uri)
            android.util.Log.d("PaymentResult", "Resultado procesado: ${paymentResult.status} - ${paymentResult.message}")
            
            // Recuperar los items del carrito guardados
            val cartItems = getCartItemsFromSharedPreferences()
            
            // Si el pago fue exitoso, guardar el registro en Firebase
            if (paymentResult.status == PaymentResultStatus.SUCCESS && cartItems.isNotEmpty() && paymentResult.paymentId != null) {
                android.util.Log.d("PaymentResult", "=== CONDICIONES PARA GUARDAR PAGO ===")
                android.util.Log.d("PaymentResult", "Status: ${paymentResult.status}")
                android.util.Log.d("PaymentResult", "CartItems count: ${cartItems.size}")
                android.util.Log.d("PaymentResult", "PaymentId: ${paymentResult.paymentId}")
                android.util.Log.d("PaymentResult", "Llamando a savePaymentToFirebase...")
                savePaymentToFirebase(paymentResult.paymentId!!, cartItems)
            } else {
                android.util.Log.w("PaymentResult", "⚠️ No se guardará el pago:")
                android.util.Log.w("PaymentResult", "Status: ${paymentResult.status}")
                android.util.Log.w("PaymentResult", "CartItems vacío: ${cartItems.isEmpty()}")
                android.util.Log.w("PaymentResult", "PaymentId null: ${paymentResult.paymentId == null}")
            }
            
            // Si el pago falló, restaurar el stock y marcar que se debe limpiar el carrito
            if (paymentResult.status == PaymentResultStatus.FAILURE || paymentResult.status == PaymentResultStatus.CANCELLED) {
                restoreStockFromSharedPreferences()
                // Marcar que el carrito debe limpiarse cuando se vuelva a MainActivity
                val prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE)
                prefs.edit().putBoolean("should_clear_cart", true).apply()
            }
            
            setContent {
                val accessibilityViewModel: AccessibilityViewModel = viewModel()
                AccessibleFutronoTheme(accessibilityViewModel = accessibilityViewModel) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Recuperar el tracking number si existe
                        val prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE)
                        val trackingNumber = prefs.getString("tracking_number", null)
                        
                        PaymentResultScreen(
                            paymentResult = paymentResult,
                            cartItems = cartItems,
                            trackingNumber = trackingNumber,
                            onBackToHome = {
                                // Limpiar los items guardados
                                clearCartItemsFromSharedPreferences()
                                // Volver a MainActivity y limpiar el stack
                                val intent = Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                finish()
                            }
                        )
                    }
                }
            }
        } else {
            // Si no hay URI, mostrar error y volver
            setContent {
                val accessibilityViewModel: AccessibilityViewModel = viewModel()
                AccessibleFutronoTheme(accessibilityViewModel = accessibilityViewModel) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        PaymentResultScreen(
                            paymentResult = PaymentResult(
                                status = PaymentResultStatus.FAILURE,
                                message = "No se pudo procesar el resultado del pago"
                            ),
                            cartItems = emptyList(),
                            trackingNumber = null,
                            onBackToHome = {
                                clearCartItemsFromSharedPreferences()
                                val intent = Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Recupera los items del carrito desde SharedPreferences
     */
    private fun getCartItemsFromSharedPreferences(): List<CartItem> {
        return try {
            val prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE)
            val json = prefs.getString("cart_items", null)
            if (json != null) {
                val gson = Gson()
                val type = object : TypeToken<List<CartItem>>() {}.type
                gson.fromJson<List<CartItem>>(json, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("PaymentResult", "Error al recuperar cartItems: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * Limpia los items del carrito guardados en SharedPreferences
     */
    private fun clearCartItemsFromSharedPreferences() {
        val prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE)
        prefs.edit()
            .remove("cart_items")
            .remove("original_stock_map")
            .remove("tracking_number")
            .remove("user_info")
            .remove("user_id")
            .remove("user_name")
            .remove("user_email")
            .remove("user_phone")
            .apply()
    }
    
    /**
     * Recupera el mapa de stock original desde SharedPreferences
     */
    private fun getOriginalStockMapFromSharedPreferences(): Map<String, Int> {
        return try {
            val prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE)
            val json = prefs.getString("original_stock_map", null)
            if (json != null) {
                val gson = Gson()
                val type = object : TypeToken<Map<String, Int>>() {}.type
                gson.fromJson<Map<String, Int>>(json, type) ?: emptyMap()
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            android.util.Log.e("PaymentResult", "Error al recuperar originalStockMap: ${e.message}", e)
            emptyMap()
        }
    }
    
    /**
     * Data class para la información del usuario
     */
    private data class UserInfo(
        val userId: String,
        val userName: String,
        val userEmail: String,
        val userPhone: String,
        val userAddress: String = ""
    )
    
    /**
     * Recupera la información del usuario desde SharedPreferences
     */
    private fun getUserInfoFromSharedPreferences(): UserInfo {
        return try {
            val prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE)
            val userJson = prefs.getString("user_info", null)
            
            if (userJson != null) {
                // Usuario registrado
                val gson = Gson()
                val user = gson.fromJson<User>(userJson, User::class.java)
                UserInfo(
                    userId = user.id,
                    userName = user.nombre + " " + user.apellido,
                    userEmail = user.email ?: "",
                    userPhone = user.telefono ?: "",
                    userAddress = user.direccion ?: ""
                )
            } else {
                // Usuario invitado
                val userId = prefs.getString("user_id", "guest") ?: "guest"
                val userName = prefs.getString("user_name", "") ?: ""
                val userEmail = prefs.getString("user_email", "") ?: ""
                val userPhone = prefs.getString("user_phone", "") ?: ""
                val userAddress = prefs.getString("user_address", "") ?: ""
                UserInfo(userId, userName, userEmail, userPhone, userAddress)
            }
        } catch (e: Exception) {
            android.util.Log.e("PaymentResult", "Error al recuperar información del usuario: ${e.message}", e)
            UserInfo("guest", "", "", "", "")
        }
    }
    
    /**
     * Guarda el pago exitoso en Firebase
     */
    private fun savePaymentToFirebase(paymentId: String, cartItems: List<CartItem>) {
        android.util.Log.d("PaymentResult", "=== savePaymentToFirebase INICIADO ===")
        android.util.Log.d("PaymentResult", "PaymentId recibido: $paymentId")
        android.util.Log.d("PaymentResult", "CartItems recibidos: ${cartItems.size}")
        
        val userInfo = getUserInfoFromSharedPreferences()
        android.util.Log.d("PaymentResult", "UserInfo recuperado:")
        android.util.Log.d("PaymentResult", "  - userId: ${userInfo.userId}")
        android.util.Log.d("PaymentResult", "  - userName: ${userInfo.userName}")
        android.util.Log.d("PaymentResult", "  - userEmail: ${userInfo.userEmail}")
        android.util.Log.d("PaymentResult", "  - userPhone: ${userInfo.userPhone}")
        
        val firebaseService = FirebaseService()
        android.util.Log.d("PaymentResult", "FirebaseService creado, iniciando corrutina...")
        
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                android.util.Log.d("PaymentResult", "Dentro de la corrutina, llamando a savePaymentRecord...")
                val result = firebaseService.savePaymentRecord(
                    paymentId = paymentId,
                    userId = userInfo.userId,
                    userName = userInfo.userName,
                    userEmail = userInfo.userEmail,
                    userPhone = userInfo.userPhone,
                    userAddress = userInfo.userAddress,
                    cartItems = cartItems
                )
                
                result.onSuccess { pairResult ->
                    val docId = pairResult.first
                    val trackingNumber = pairResult.second
                    android.util.Log.d("PaymentResult", "✅ Pago guardado exitosamente en Firebase")
                    android.util.Log.d("PaymentResult", "Document ID: $docId")
                    android.util.Log.d("PaymentResult", "Tracking Number: $trackingNumber")
                    
                    // Guardar el tracking number en SharedPreferences para mostrarlo en la pantalla
                    val prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE)
                    prefs.edit().putString("tracking_number", trackingNumber).apply()
                    android.util.Log.d("PaymentResult", "Tracking number guardado en SharedPreferences")
                }.onFailure { error ->
                    android.util.Log.e("PaymentResult", "❌ Error al guardar pago en Firebase")
                    android.util.Log.e("PaymentResult", "Mensaje: ${error.message}")
                    android.util.Log.e("PaymentResult", "Tipo: ${error.javaClass.simpleName}")
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                android.util.Log.e("PaymentResult", "❌ Excepción en savePaymentToFirebase")
                android.util.Log.e("PaymentResult", "Mensaje: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Restaura el stock de todos los productos desde el mapa guardado
     */
    private fun restoreStockFromSharedPreferences() {
        val originalStockMap = getOriginalStockMapFromSharedPreferences()
        if (originalStockMap.isEmpty()) {
            android.util.Log.w("PaymentResult", "No hay stock original guardado para restaurar")
            return
        }
        
        val productService = com.example.intento1app.data.services.ProductFirebaseService()
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            originalStockMap.forEach { (productId, originalStock) ->
                productService.updateProductStock(productId, originalStock).onFailure { error ->
                    android.util.Log.e("PaymentResult", "Error al restaurar stock de $productId: ${error.message}")
                }
            }
            android.util.Log.d("PaymentResult", "Stock restaurado para ${originalStockMap.size} productos")
        }
    }
}

@Composable
fun PaymentResultScreen(
    paymentResult: PaymentResult,
    cartItems: List<CartItem> = emptyList(),
    trackingNumber: String? = null,
    onBackToHome: () -> Unit
) {
    val (icon, iconColor, title, message) = when (paymentResult.status) {
        PaymentResultStatus.SUCCESS -> {
            Quadruple(
                Icons.Filled.CheckCircle,
                Color(0xFF4CAF50),
                "¡Pago Exitoso!",
                paymentResult.message ?: "Tu pago ha sido procesado correctamente."
            )
        }
        PaymentResultStatus.PENDING -> {
            Quadruple(
                Icons.Filled.Info,
                Color(0xFFFF9800),
                "Pago Pendiente",
                paymentResult.message ?: "Tu pago está pendiente de confirmación. Te notificaremos cuando se complete."
            )
        }
        PaymentResultStatus.FAILURE -> {
            Quadruple(
                Icons.Filled.Warning,
                Color(0xFFF44336),
                "Pago Rechazado",
                paymentResult.message ?: "No se pudo procesar tu pago. Por favor, intenta nuevamente."
            )
        }
        PaymentResultStatus.CANCELLED -> {
            Quadruple(
                Icons.Filled.Close,
                Color(0xFF757575),
                "Pago Cancelado",
                paymentResult.message ?: "El pago fue cancelado."
            )
        }
    }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Contenido scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = iconColor
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = iconColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        paymentResult.paymentId?.let { paymentId ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = FutronoBlanco
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Información del pago",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID de pago: $paymentId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    trackingNumber?.let { tracking ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Número de seguimiento:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tracking,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        // Mostrar contenido del carrito solo si el pago fue exitoso
        if (paymentResult.status == PaymentResultStatus.SUCCESS && cartItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            CartItemsTable(cartItems = cartItems)
        }
        
        // Mensaje adicional para pagos pendientes
        if (paymentResult.status == PaymentResultStatus.PENDING) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "ℹ️ Información importante",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tu pago está siendo procesado. Recibirás una notificación cuando se confirme. Puedes verificar el estado en tu cuenta de MercadoPago.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
        
        // Mensaje adicional para pagos fallidos
        if (paymentResult.status == PaymentResultStatus.FAILURE) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "💡 Sugerencias",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Verifica que los datos de tu tarjeta sean correctos\n• Asegúrate de tener fondos suficientes\n• Intenta con otro método de pago\n• Contacta a tu banco si el problema persiste",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
        }
        
        // Botón de regreso siempre visible al final (fuera del scroll)
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B4513)
            )
        ) {
            Text(
                text = "Volver al Inicio",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

/**
 * Componente que muestra una tabla con los items del carrito
 */
@Composable
private fun CartItemsTable(cartItems: List<CartItem>) {
    // Calcular subtotal, IVA y total
    val subtotal = cartItems.sumOf { it.totalPrice }
    val iva = subtotal * 0.19 // 19% IVA
    val total = subtotal + iva
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Detalle de la Compra",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FutronoCafe
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Encabezado de la tabla
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.15f)
                )
                Text(
                    text = "Producto",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.3f)
                )
                Text(
                    text = "Cant.",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.15f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Precio",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.2f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.2f),
                    textAlign = TextAlign.End
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Items del carrito
            cartItems.forEach { cartItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = cartItem.product.id.take(8), // Mostrar solo los primeros 8 caracteres del ID
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.15f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = cartItem.product.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.3f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${cartItem.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.15f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$${String.format("%,.0f", cartItem.product.price).replace(",", ".")}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.2f),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "$${String.format("%,.0f", cartItem.totalPrice).replace(",", ".")}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.2f),
                        textAlign = TextAlign.End
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    text = "$${String.format("%,.0f", subtotal).replace(",", ".")}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.4f),
                    textAlign = TextAlign.End
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // IVA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "IVA (19%)",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    text = "$${String.format("%,.0f", iva).replace(",", ".")}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.4f),
                    textAlign = TextAlign.End
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TOTAL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = "$${String.format("%,.0f", total).replace(",", ".")}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(0.4f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

// Helper class
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

