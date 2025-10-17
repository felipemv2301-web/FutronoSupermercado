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
import com.example.intento1app.navigation.PaymentNavigation

/**
 * Ejemplo de cómo integrar el sistema de pagos en tu aplicación
 * 
 * Este archivo muestra cómo usar el sistema de pagos de Mercado Pago
 * en tu aplicación existente.
 */
@Composable
fun PaymentIntegrationExample() {
    var showPayment by remember { mutableStateOf(false) }
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
                        imageUrl = ""
                    ),
                    quantity = 2
                )
            )
        ) 
    }
    
    if (showPayment) {
        PaymentNavigation(
            cartItems = cartItems,
            onBackToCart = {
                showPayment = false
            },
            onPaymentComplete = {
                showPayment = false
                // Aquí puedes limpiar el carrito o hacer otras acciones
                cartItems = emptyList()
            }
        )
    } else {
        // Tu pantalla principal aquí
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { showPayment = true },
                enabled = cartItems.isNotEmpty()
            ) {
                Text("Ir a Pagar")
            }
        }
    }
}

/**
 * Cómo integrar en tu MainActivity existente:
 * 
 * 1. Agrega esta función a tu MainActivity:
 * 
 * @Composable
 * fun YourMainScreen() {
 *     var showPayment by remember { mutableStateOf(false) }
 *     var cartItems by remember { mutableStateOf(getCartItems()) } // Tu función para obtener items
 *     
 *     if (showPayment) {
 *         PaymentNavigation(
 *             cartItems = cartItems,
 *             onBackToCart = { showPayment = false },
 *             onPaymentComplete = { 
 *                 showPayment = false
 *                 // Limpiar carrito después del pago exitoso
 *                 clearCart()
 *             }
 *         )
 *     } else {
 *         // Tu UI principal aquí
 *         YourMainContent(
 *             onCheckoutClick = { showPayment = true }
 *         )
 *     }
 * }
 * 
 * 2. En tu botón de checkout, llama a onCheckoutClick()
 * 
 * 3. Asegúrate de que tu función getCartItems() retorne List<CartItem>
 *    con la estructura correcta (product: Product, quantity: Int)
 */
