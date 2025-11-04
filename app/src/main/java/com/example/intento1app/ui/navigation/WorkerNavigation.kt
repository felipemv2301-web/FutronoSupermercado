package com.example.intento1app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.intento1app.ui.screens.EditProductScreen
import com.example.intento1app.ui.screens.WorkerProductsScreen

sealed class WorkerScreen(val route: String) {
    object ProductList : WorkerScreen("productList")
    object EditProduct : WorkerScreen("editProduct/{productId}") {
        fun createRoute(productId: String) = "editProduct/$productId"
    }
    object AddProduct : WorkerScreen("addProduct")
}

@Composable
fun WorkerNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = WorkerScreen.ProductList.route) {
        composable(WorkerScreen.ProductList.route) {
            WorkerProductsScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddProductClick = { navController.navigate(WorkerScreen.AddProduct.route) },
                onEditProductClick = { productId ->
                    navController.navigate(WorkerScreen.EditProduct.createRoute(productId))
                }
            )
        }
        composable(
            route = WorkerScreen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                EditProductScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        composable(WorkerScreen.AddProduct.route) {
            // TODO: Crear la pantalla para agregar productos
        }
    }
}
