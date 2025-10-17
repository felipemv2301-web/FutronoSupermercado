package com.example.intento1app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.ui.screens.CartScreen
import com.example.intento1app.ui.screens.HomeScreen
import com.example.intento1app.ui.screens.ProductsScreen
import com.example.intento1app.viewmodel.FutronoViewModel
import com.example.intento1app.viewmodel.AccessibilityViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Products : Screen("products/{category}")
    object Cart : Screen("cart")
}

@Composable
fun FutronoNavigation(
    viewModel: FutronoViewModel,
    navController: NavHostController = rememberNavController()
) {
    val accessibilityViewModel: AccessibilityViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { category ->
                    viewModel.getProductsByCategory(category)
                    navController.navigate("products/${category.name}")
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                cartItemCount = viewModel.getCartItemCount(),
                onAccessibilityClick = {
                    // Aquí se podría navegar a una pantalla de accesibilidad
                    // Por ahora no hacemos nada específico
                },
                accessibilityViewModel = accessibilityViewModel
            )
        }
        
        composable(Screen.Products.route) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category")
            val category = ProductCategory.values().find { it.name == categoryName }
            
            if (category != null) {
                ProductsScreen(
                    category = category,
                    products = uiState.filteredProducts,
                    onBackClick = { navController.popBackStack() },
                    onAddToCart = { product ->
                        viewModel.addToCart(product)
                    },
                    onCartClick = {
                        navController.navigate(Screen.Cart.route)
                    },
                    cartItemCount = viewModel.getCartItemCount(),
                    isLoading = uiState.isLoading
                )
            }
        }
        
        composable(Screen.Cart.route) {
            CartScreen(
                cartItems = uiState.cart,
                onBackClick = { navController.popBackStack() },
                onUpdateQuantity = { productId, quantity ->
                    viewModel.updateCartItemQuantity(productId, quantity)
                },
                onRemoveItem = { productId ->
                    viewModel.removeFromCart(productId)
                },
                onClearCart = {
                    viewModel.clearCart()
                },
                onCheckout = {
                    // Aquí se implementaría la lógica de checkout
                    // Por ahora solo mostramos un mensaje y volvemos al home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
