package com.example.intento1app.data.repository


import com.example.intento1app.data.models.Product

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val firestore = Firebase.firestore
    private val productsCollection = firestore.collection("products")

    suspend fun addProductToFirestore(product: Product): Boolean {
        return try {
            productsCollection.add(product).await()
            true // Éxito
        } catch (e: Exception) {
            // Log del error
            false // Fracaso
        }
    }

    suspend fun getProductsFromFirestore(): List<Product> {
        return try {
            productsCollection.get().await().toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
