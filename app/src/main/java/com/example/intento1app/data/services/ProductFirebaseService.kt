package com.example.intento1app.data.services

import android.util.Log
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Servicio para manejar productos en Firebase Firestore
 */
class ProductFirebaseService {
    
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "products"
    
    /**
     * Obtiene todos los productos desde Firestore
     */
    suspend fun getAllProducts(): List<Product> {
        return try {
            val result = db.collection(collectionName)
                .get()
                .await()
            
            val products = result.documents.mapNotNull { doc ->
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
                    Log.e("ProductFirebaseService", "Error al convertir documento a Product: ${e.message}")
                    null
                }
            }
            
            Log.d("ProductFirebaseService", "Productos obtenidos: ${products.size}")
            products
        } catch (e: Exception) {
            Log.e("ProductFirebaseService", "Error al obtener productos: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Obtiene productos por categoría desde Firestore
     */
    suspend fun getProductsByCategory(category: ProductCategory): List<Product> {
        return try {
            val result = db.collection(collectionName)
                .whereEqualTo("category", category.name)
                .get()
                .await()
            
            val products = result.documents.mapNotNull { doc ->
                try {
                    Product(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        category = category,
                        imageUrl = doc.getString("imageUrl") ?: "",
                        unit = doc.getString("unit") ?: "unidad",
                        stock = doc.getLong("stock")?.toInt() ?: 100,
                        isAvailable = doc.getBoolean("isAvailable") ?: true
                    )
                } catch (e: Exception) {
                    Log.e("ProductFirebaseService", "Error al convertir documento a Product: ${e.message}")
                    null
                }
            }
            
            Log.d("ProductFirebaseService", "Productos obtenidos para categoría ${category.name}: ${products.size}")
            products
        } catch (e: Exception) {
            Log.e("ProductFirebaseService", "Error al obtener productos por categoría: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Obtiene un producto por ID desde Firestore
     */
    suspend fun getProductById(productId: String): Product? {
        return try {
            val doc = db.collection(collectionName)
                .document(productId)
                .get()
                .await()
            
            if (doc.exists()) {
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
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ProductFirebaseService", "Error al obtener producto por ID: ${e.message}")
            null
        }
    }
    
    /**
     * Convierte un string a ProductCategory
     */
    private fun getProductCategoryFromString(categoryString: String): ProductCategory {
        return try {
            ProductCategory.valueOf(categoryString)
        } catch (e: IllegalArgumentException) {
            Log.w("ProductFirebaseService", "Categoría no válida: $categoryString, usando DESPENSA por defecto")
            ProductCategory.DESPENSA
        }
    }
    
    /**
     * Función de conveniencia para usar con callbacks (compatible con tu código existente)
     */
    fun getAllProductsFromFirebase(
        onResult: (List<Product>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Ejecutar en un scope de corrutinas
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val products = getAllProducts()
                onResult(products)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
