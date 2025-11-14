
package com.example.intento1app.data.services

import android.util.Log
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

/**
 * Servicio para manejar productos en Firebase Firestore
 */
class ProductFirebaseService {
    
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "products"
    
    /**
     * Obtiene todos los productos desde Firestore como un Flow
     */
    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val listener = db.collection(collectionName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ProductFirebaseService", "Error al obtener productos", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { doc ->
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
                    trySend(products)
                }
            }
        awaitClose { listener.remove() }
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
     * Actualiza un producto en Firestore
     */
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            // Convertir Product a mapa para guardar en Firestore (categoría como string)
            val productMap = hashMapOf(
                "id" to product.id,
                "name" to product.name,
                "description" to product.description,
                "price" to product.price,
                "category" to product.category.name, // Convertir enum a string
                "imageUrl" to product.imageUrl,
                "unit" to product.unit,
                "stock" to product.stock,
                "isAvailable" to product.isAvailable
            )
            db.collection(collectionName).document(product.id).set(productMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProductFirebaseService", "Error al actualizar el producto: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza solo el stock de un producto en Firestore
     * @param productId ID del producto
     * @param newStock Nuevo valor de stock
     */
    suspend fun updateProductStock(productId: String, newStock: Int): Result<Unit> {
        return try {
            db.collection(collectionName).document(productId)
                .update("stock", newStock)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProductFirebaseService", "Error al actualizar stock del producto: ${e.message}")
            Result.failure(e)
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
                // Ya no se usa getAllProducts suspendida, así que simplemente llamamos al flow y colectamos el primer resultado
                 db.collection(collectionName)
                .get()
                .addOnSuccessListener { result ->
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
                    onResult(products)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
