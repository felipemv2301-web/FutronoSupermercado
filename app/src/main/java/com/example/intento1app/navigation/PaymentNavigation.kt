package com.example.intento1app.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.intento1app.data.models.CartItem
import com.example.intento1app.ui.screens.MercadoPagoCheckoutScreen
import com.example.intento1app.ui.screens.PaymentScreen

// Estados de navegación para el flujo de pagos
sealed class PaymentRoute(val route: String) {
    object Payment : PaymentRoute("payment")
    object Checkout : PaymentRoute("checkout/{checkoutUrl}") {
        fun createRoute(checkoutUrl: String) = "checkout/${checkoutUrl}"
    }
    object Success : PaymentRoute("payment_success")
    object Failure : PaymentRoute("payment_failure")
    object Pending : PaymentRoute("payment_pending")
}

@Composable
fun PaymentNavigation(
    cartItems: List<CartItem>,
    onBackToCart: () -> Unit,
    onPaymentComplete: () -> Unit
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = PaymentRoute.Payment.route
    ) {
        
        // Pantalla de pago principal
        composable(PaymentRoute.Payment.route) {
            PaymentScreen(
                cartItems = cartItems,
                onPaymentComplete = {
                    navController.navigate(PaymentRoute.Success.route)
                },
                onBackToCart = onBackToCart,
                onNavigateToCheckout = { checkoutUrl ->
                    navController.navigate(PaymentRoute.Checkout.createRoute(checkoutUrl))
                }
            )
        }
        
        // Pantalla de checkout de Mercado Pago
        composable(PaymentRoute.Checkout.route) { backStackEntry ->
            val checkoutUrl = backStackEntry.arguments?.getString("checkoutUrl") ?: ""
            
            MercadoPagoCheckoutScreen(
                checkoutUrl = checkoutUrl,
                onBack = {
                    navController.popBackStack()
                },
                onPaymentSuccess = {
                    navController.navigate(PaymentRoute.Success.route) {
                        popUpTo(PaymentRoute.Payment.route) { inclusive = true }
                    }
                },
                onPaymentFailure = {
                    navController.navigate(PaymentRoute.Failure.route) {
                        popUpTo(PaymentRoute.Payment.route) { inclusive = true }
                    }
                },
                onPaymentPending = {
                    navController.navigate(PaymentRoute.Pending.route) {
                        popUpTo(PaymentRoute.Payment.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de éxito
        composable(PaymentRoute.Success.route) {
            PaymentSuccessScreen(
                onContinue = onPaymentComplete
            )
        }
        
        // Pantalla de error
        composable(PaymentRoute.Failure.route) {
            PaymentFailureScreen(
                onRetry = {
                    navController.popBackStack()
                },
                onBackToCart = onBackToCart
            )
        }
        
        // Pantalla de pendiente
        composable(PaymentRoute.Pending.route) {
            PaymentPendingScreen(
                onContinue = onPaymentComplete,
                onBackToCart = onBackToCart
            )
        }
    }
}

// Pantallas de resultado (simplificadas)
@Composable
private fun PaymentSuccessScreen(
    onContinue: () -> Unit
) {
    // Implementar pantalla de éxito
    // Por ahora, solo llamar onContinue
    LaunchedEffect(Unit) {
        onContinue()
    }
}

@Composable
private fun PaymentFailureScreen(
    onRetry: () -> Unit,
    onBackToCart: () -> Unit
) {
    // Implementar pantalla de error
    // Por ahora, solo llamar onBackToCart
    LaunchedEffect(Unit) {
        onBackToCart()
    }
}

@Composable
private fun PaymentPendingScreen(
    onContinue: () -> Unit,
    onBackToCart: () -> Unit
) {
    // Implementar pantalla de pendiente
    // Por ahora, solo llamar onContinue
    LaunchedEffect(Unit) {
        onContinue()
    }
}
