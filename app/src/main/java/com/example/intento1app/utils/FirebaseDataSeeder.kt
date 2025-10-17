package com.example.intento1app.utils

import android.util.Log
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.data.models.ProductFirestore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirebaseDataSeeder {

    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_NAME = "products"

    /**
     * Pobla Firebase con los productos de ejemplo
     */
    suspend fun seedProducts(): Boolean {
        return try {
            val products = getInitialProducts() // <-- ahora devuelve lista

            for (product in products) {
                val productData = mapOf(
                    "id" to product.id,
                    "name" to product.name,
                    "description" to product.description,
                    "price" to product.price,
                    "category" to product.category.name,
                    "imageUrl" to product.imageUrl,
                    "unit" to product.unit,
                    "stock" to product.stock,
                    "isAvailable" to product.isAvailable
                )

                db.collection(COLLECTION_NAME)
                    .document(product.id)
                    .set(productData)
                    .await()

                Log.d("FirebaseDataSeeder", "Producto agregado: ${product.name}")
            }

            Log.d("FirebaseDataSeeder", "Se agregaron ${products.size} productos a Firebase")
            true
        } catch (e: Exception) {
            Log.e("FirebaseDataSeeder", "Error al poblar productos: ${e.message}")
            false
        }
    }

    /**
     * Devuelve los productos de ejemplo
     */

    //Sube los productos de la lista a FireStore de manera arbitraria
    private fun getInitialProducts(): List<Product> {
        return listOf(
            Product(
                id = "31",
                name = "Arroz Grano Largo",
                description = "Arroz grano largo ancho de excelente calidad.",
                price = 1590.0,
                category = ProductCategory.DESPENSA,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "32",
                name = "Aceite Vegetal",
                description = "Aceite vegetal ideal para cocinar y freír.",
                price = 2390.0,
                category = ProductCategory.DESPENSA,
                imageUrl = "h",
                unit = "L"
            ),
            Product(
                id = "33",
                name = "Porotos Negros",
                description = "Porotos negros seleccionados para guisos y ensaladas.",
                price = 1890.0,
                category = ProductCategory.DESPENSA,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "34",
                name = "Carne de Vacuno Premium",
                description = "Carne de vacuno premium para asados o guisos.",
                price = 15990.0,
                category = ProductCategory.CARNES_PESCADOS,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "35",
                name = "Pechuga de Pollo",
                description = "Pechuga de pollo sin piel, ideal para preparaciones saludables.",
                price = 5990.0,
                category = ProductCategory.CARNES_PESCADOS,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "36",
                name = "Salmón Fresco",
                description = "Filete de salmón fresco del sur de Chile.",
                price = 13490.0,
                category = ProductCategory.CARNES_PESCADOS,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "37",
                name = "Manzana Roja",
                description = "Manzanas rojas frescas y crujientes.",
                price = 1990.0,
                category = ProductCategory.FRUTAS_VERDURAS,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "38",
                name = "Tomate",
                description = "Tomates frescos de cultivo local.",
                price = 1790.0,
                category = ProductCategory.FRUTAS_VERDURAS,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "39",
                name = "Zanahoria",
                description = "Zanahorias frescas ideales para ensaladas y guisos.",
                price = 1290.0,
                category = ProductCategory.FRUTAS_VERDURAS,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "40",
                name = "Bebida Cola",
                description = "Gaseosa sabor cola en botella retornable.",
                price = 1590.0,
                category = ProductCategory.BEBIDAS_SNACKS,
                imageUrl = "",
                unit = "L"
            ),
            Product(
                id = "41",
                name = "Papas Fritas",
                description = "Papas fritas clásicas con sal marina.",
                price = 1390.0,
                category = ProductCategory.BEBIDAS_SNACKS,
                imageUrl = "",
                unit = "g"
            ),
            Product(
                id = "42",
                name = "Jugo de Naranja",
                description = "Jugo natural de naranja sin azúcar añadida.",
                price = 2190.0,
                category = ProductCategory.BEBIDAS_SNACKS,
                imageUrl = "",
                unit = "L"
            ),
            Product(
                id = "43",
                name = "Leche Entera",
                description = "Leche entera fresca pasteurizada.",
                price = 1250.0,
                category = ProductCategory.FRESCOS_LACTEOS,
                imageUrl = "",
                unit = "L"
            ),
            Product(
                id = "44",
                name = "Queso Mantecoso",
                description = "Queso mantecoso de textura suave y sabor intenso.",
                price = 4290.0,
                category = ProductCategory.FRESCOS_LACTEOS,
                imageUrl = "",
                unit = "g"
            ),
            Product(
                id = "45",
                name = "Yogur Natural",
                description = "Yogur natural sin azúcar.",
                price = 2890.0,
                category = ProductCategory.FRESCOS_LACTEOS,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "46",
                name = "Marraqueta",
                description = "Pan marraqueta fresco, crujiente por fuera y suave por dentro.",
                price = 1590.0,
                category = ProductCategory.PANADERIA_PASTELERIA,
                imageUrl = "",
                unit = "kg"
            ),
            Product(
                id = "47",
                name = "Queque de Vainilla",
                description = "Queque casero de vainilla con cobertura de azúcar flor.",
                price = 3290.0,
                category = ProductCategory.PANADERIA_PASTELERIA,
                imageUrl = "",
                unit = "g"
            ),
            Product(
                id = "48",
                name = "Croissant",
                description = "Croissants de mantequilla recién horneados.",
                price = 2990.0,
                category = ProductCategory.PANADERIA_PASTELERIA,
                imageUrl = "",
                unit = "g"
            )
        )
    }

    /**
     * Limpia todos los productos de la colección (útil para testing)
     */
    suspend fun clearProducts(): Boolean {
        return try {
            val snapshot = db.collection(COLLECTION_NAME).get().await()
            val batch = db.batch()

            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }

            batch.commit().await()
            Log.d("FirebaseDataSeeder", "Productos eliminados de Firebase")
            true
        } catch (e: Exception) {
            Log.e("FirebaseDataSeeder", "Error al limpiar productos: ${e.message}")
            false
        }
    }
}
