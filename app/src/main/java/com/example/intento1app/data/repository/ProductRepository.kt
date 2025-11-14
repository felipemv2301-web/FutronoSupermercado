package com.example.intento1app.data.repository


import android.util.Log
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
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
            val result = productsCollection.get().await()
            result.documents.mapNotNull { doc ->
                try {
                    Product(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        category = getProductCategoryFromString(doc.getString("category") ?: "DESPENSA"),
                        imageUrl = doc.getString("imageUrl") ?: "",
                        unit = doc.getString("unit") ?: "unidad",
                        stock = doc.getLong("stock")?.toInt() ?: 100,
                        isAvailable = doc.getBoolean("isAvailable") ?: true
                    )
                } catch (e: Exception) {
                    Log.e("ProductRepository", "Error al convertir documento a Product: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error al obtener productos: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Función helper para convertir un string de categoría a ProductCategory enum
     */
    private fun getProductCategoryFromString(categoryString: String): ProductCategory {
        return try {
            ProductCategory.valueOf(categoryString.uppercase())
        } catch (e: IllegalArgumentException) {
            Log.w("ProductRepository", "Categoría no válida: $categoryString, usando DESPENSA por defecto")
            ProductCategory.DESPENSA
        }
    }
}
