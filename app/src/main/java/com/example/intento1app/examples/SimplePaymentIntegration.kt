package com.example.intento1app.examples

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.CartItem
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.ui.screens.PaymentScreen
import com.example.intento1app.ui.screens.MercadoPagoCheckoutScreen

/**
 * Integración simple del sistema de pagos
 * 
 * Este ejemplo muestra cómo integrar el sistema de pagos
 * directamente en tu MainActivity existente.
 */
@Composable
fun SimplePaymentIntegration() {
    var showPayment by remember { mutableStateOf(false) }
    var showCheckout by remember { mutableStateOf(false) }
    var checkoutUrl by remember { mutableStateOf("") }
    
    // Datos de ejemplo del carrito
    var cartItems by remember { 
        mutableStateOf(
            listOf(
                CartItem(
                    product = Product(
                        id = "1",
                        name = "Producto de Prueba",
                        description = "Descripción del producto",
                        price = 25000.0,
                        category = ProductCategory.DESPENSA,
                        imageUrl = "",
                        unit = "kg"
                    ),
                    quantity = 2
                )
            )
        ) 
    }
    
    when {
        showCheckout -> {
            // Pantalla de checkout de Mercado Pago
            MercadoPagoCheckoutScreen(
                checkoutUrl = checkoutUrl,
                onBack = {
                    showCheckout = false
                    showPayment = false
                },
                onPaymentSuccess = {
                    showCheckout = false
                    showPayment = false
                    cartItems = emptyList() // Limpiar carrito
                },
                onPaymentFailure = {
                    showCheckout = false
                    showPayment = false
                },
                onPaymentPending = {
                    showCheckout = false
                    showPayment = false
                }
            )
        }
        showPayment -> {
            // Pantalla de pago
            PaymentScreen(
                cartItems = cartItems,
                onPaymentComplete = {
                    showPayment = false
                    cartItems = emptyList()
                },
                onBackToCart = {
                    showPayment = false
                },
                onNavigateToCheckout = { url ->
                    checkoutUrl = url
                    showCheckout = true
                }
            )
        }
        else -> {
            // Tu pantalla principal
            MainScreenContent(
                cartItems = cartItems,
                onCheckoutClick = { showPayment = true }
            )
        }
    }
}

@Composable
private fun MainScreenContent(
    cartItems: List<CartItem>,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tu Aplicación",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Carrito: ${cartItems.size} items",
            style = MaterialTheme.typography.bodyLarge
        )
        
        if (cartItems.isNotEmpty()) {
            Text(
                text = "Total: $${cartItems.sumOf { it.totalPrice }.toInt()}",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onCheckoutClick,
            enabled = cartItems.isNotEmpty()
        ) {
            Text("Ir a Pagar con Mercado Pago")
        }
    }
}

/**
 * CÓMO INTEGRAR EN TU MAINACTIVITY:
 * 
 * 1. Reemplaza tu contenido principal con:
 * 
 * @Composable
 * fun YourMainActivity() {
 *     var showPayment by remember { mutableStateOf(false) }
 *     var showCheckout by remember { mutableStateOf(false) }
 *     var checkoutUrl by remember { mutableStateOf("") }
 *     
 *     when {
 *         showCheckout -> {
 *             MercadoPagoCheckoutScreen(
 *                 checkoutUrl = checkoutUrl,
 *                 onBack = { showCheckout = false; showPayment = false },
 *                 onPaymentSuccess = { 
 *                     showCheckout = false
 *                     showPayment = false
 *                     clearCart() // Tu función para limpiar carrito
 *                 },
 *                 onPaymentFailure = { showCheckout = false; showPayment = false },
 *                 onPaymentPending = { showCheckout = false; showPayment = false }
 *             )
 *         }
 *         showPayment -> {
 *             PaymentScreen(
 *                 cartItems = getCartItems(), // Tu función para obtener items
 *                 onPaymentComplete = { showPayment = false; clearCart() },
 *                 onBackToCart = { showPayment = false },
 *                 onNavigateToCheckout = { url ->
 *                     checkoutUrl = url
 *                     showCheckout = true
 *                 }
 *             )
 *         }
 *         else -> {
 *             // Tu UI principal aquí
 *             YourMainContent(
 *                 onCheckoutClick = { showPayment = true }
 *             )
 *         }
 *     }
 * }
 * 
 * 2. En tu botón de checkout, llama a onCheckoutClick()
 * 
 * 3. Asegúrate de que getCartItems() retorne List<CartItem>
 */
