package com.example.intento1app.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.intento1app.CategorySelectionScreen
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.ui.screens.CartScreen
import com.example.intento1app.ui.screens.ProductsScreen
import com.example.intento1app.viewmodel.AccessibilityViewModel
import com.example.intento1app.viewmodel.FutronoViewModel

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
            MainScreen(
                viewModel = viewModel,
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                accessibilityViewModel = accessibilityViewModel
            )
        }

        composable(Screen.Products.route) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category")
            val category = ProductCategory.values().find {
                it.name == categoryName
            }

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
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(
    viewModel: FutronoViewModel,
    onCartClick: () -> Unit,
    accessibilityViewModel: AccessibilityViewModel
) {
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }

    val currentCategoryName = selectedCategoryName

    if (currentCategoryName == null) {
        CategorySelectionScreen(
            onCategoryClick = { categoryName ->
                selectedCategoryName = categoryName
            },
            onCartClick = onCartClick,
            cartItemCount = viewModel.getCartItemCount(),
            accessibilityViewModel = accessibilityViewModel
        )
    } else {
        val categoryObject = remember(currentCategoryName) {
            ProductCategory.values().find { it.name == currentCategoryName }
        }

        if (categoryObject != null) {
            ProductListScreen(
                category = categoryObject,
                onBackClick = {
                    selectedCategoryName = null
                },
                onAddToCart = { product ->
                    viewModel.addToCart(product)
                }
            )
        } else {
            selectedCategoryName = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    category: ProductCategory,
    onBackClick: () -> Unit,
    onAddToCart: (com.example.intento1app.data.models.Product) -> Unit
) {
    // 1. Estado para el texto que el usuario escribe en el buscador
    var searchQuery by remember { mutableStateOf("") }

    // (Más adelante conectaremos esto a Firebase, por ahora es solo para que funcione)

    // UI de la pantalla de productos
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text(text = category.displayName) },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.padding(paddingValues).padding(16.dp)) {

            // 2. Esta es la BARRA DE BÚSQUEDA que quieres
            androidx.compose.material3.OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                label = { androidx.compose.material3.Text("Buscar en ${category.displayName}...") },
                leadingIcon = { androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.Search, contentDescription = "Buscar") }
            )

            // 3. Espacio para mostrar los productos
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.material3.Text("Mostrando productos para: ${category.displayName}")
            androidx.compose.material3.Text("Búsqueda: $searchQuery")
        }
    }
}
