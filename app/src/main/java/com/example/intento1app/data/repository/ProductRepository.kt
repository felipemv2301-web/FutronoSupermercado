package com.example.intento1app.data.repository

import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.data.services.ProductFirebaseService
import kotlinx.coroutines.delay

class ProductRepository {
    
    private val firebaseService = ProductFirebaseService()
    
    suspend fun getProducts(): List<Product> {
        return try {
            firebaseService.getAllProducts()
        } catch (e: Exception) {
            // En caso de error, devolver lista vacía
            emptyList()
        }
    }
    
    suspend fun getProductsByCategory(category: ProductCategory): List<Product> {
        return try {
            firebaseService.getProductsByCategory(category)
        } catch (e: Exception) {
            // En caso de error, devolver lista vacía
            emptyList()
        }
    }
}
