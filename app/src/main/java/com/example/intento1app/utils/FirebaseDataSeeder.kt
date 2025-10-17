package com.example.intento1app.utils

import android.util.Log
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Utilidad para poblar Firebase Firestore con datos de productos
 * Este archivo se puede usar para migrar los productos estáticos a Firebase
 */
object FirebaseDataSeeder {
    
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_NAME = "products"
    
    /**
     * Pobla Firebase con los productos de ejemplo
     */
    suspend fun seedProducts(): Boolean {
        return try {
            val products = getSampleProducts()
            
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
     * Obtiene los productos de ejemplo (los mismos que estaban en el código estático)
     */
    private fun getSampleProducts(): List<Product> {
        return listOf(
            // Carnes y Pescados
            Product("1", "Carne de Vacuno Premium", "Carne de Vacuno de primera calidad, perfecta para asados", 15990.0, ProductCategory.CARNES_PESCADOS, "", null, "kg"),
            Product("2", "Salmón Fresco", "Salmón fresco del Pacífico, rico en omega-3", 28990.0, ProductCategory.CARNES_PESCADOS, "", null, "kg"),
            Product("3", "Pollo Entero", "Pollo fresco de granja, sin hormonas", 12990.0, ProductCategory.CARNES_PESCADOS, "", null, "kg"),
            Product("4", "Cerdo Ahumado", "Cerdo ahumado con especias tradicionales", 15990.0, ProductCategory.CARNES_PESCADOS, "", null, "kg"),
            Product("5", "Atún Fresco", "Atún fresco de pesca sostenible", 24990.0, ProductCategory.CARNES_PESCADOS, "", null, "kg"),
            
            // Despensa
            Product("6", "Arroz Basmati", "Arroz aromático de grano largo, perfecto para acompañamientos", 3990.0, ProductCategory.DESPENSA, "", null, "kg"),
            Product("7", "Aceite de Oliva Extra Virgen", "Aceite de oliva de primera prensada en frío", 8990.0, ProductCategory.DESPENSA, "", null, "litro"),
            Product("8", "Pasta Integral", "Pasta de trigo integral, rica en fibra", 2990.0, ProductCategory.DESPENSA, "", null, "kg"),
            Product("9", "Lentejas Rojas", "Lentejas rojas orgánicas, ricas en proteínas", 2990.0, ProductCategory.DESPENSA, "", null, "kg"),
            Product("10", "Harina de Trigo", "Harina de trigo para todo uso, ideal para panadería", 1990.0, ProductCategory.DESPENSA, "", null, "kg"),
            
            // Frutas y Verduras
            Product("11", "Manzanas Gala", "Manzanas dulces y crujientes, perfectas para comer frescas", 2990.0, ProductCategory.FRUTAS_VERDURAS, "", null, "kg"),
            Product("12", "Brócoli Orgánico", "Brócoli orgánico fresco, rico en vitaminas", 3990.0, ProductCategory.FRUTAS_VERDURAS, "", null, "kg"),
            Product("13", "Plátanos", "Plátanos maduros y dulces", 1990.0, ProductCategory.FRUTAS_VERDURAS, "", null, "kg"),
            Product("14", "Tomates Cherry", "Tomates cherry dulces y jugosos", 3990.0, ProductCategory.FRUTAS_VERDURAS, "", null, "kg"),
            Product("15", "Zanahorias Orgánicas", "Zanahorias orgánicas frescas y crujientes", 1990.0, ProductCategory.FRUTAS_VERDURAS, "", null, "kg"),
            
            // Bebidas y Snacks
            Product("16", "Jugo de Naranja Natural", "Jugo de naranja 100% natural, sin conservantes", 3990.0, ProductCategory.BEBIDAS_SNACKS, "", null, "litro"),
            Product("17", "Papas Fritas Artesanales", "Papas fritas artesanales con sal marina", 2990.0, ProductCategory.BEBIDAS_SNACKS, "", null, "paquete"),
            Product("18", "Refresco Cola", "Refresco de cola clásico, 2 litros", 1990.0, ProductCategory.BEBIDAS_SNACKS, "", null, "botella"),
            Product("19", "Nueces Mixtas", "Mezcla de nueces premium tostadas", 8990.0, ProductCategory.BEBIDAS_SNACKS, "", null, "kg"),
            Product("20", "Agua Mineral", "Agua mineral natural de manantial", 990.0, ProductCategory.BEBIDAS_SNACKS, "", null, "botella"),
            
            // Frescos y Lácteos
            Product("21", "Leche Entera", "Leche fresca de vaca, rica en calcio", 1990.0, ProductCategory.FRESCOS_LACTEOS, "", null, "litro"),
            Product("22", "Queso Manchego", "Queso manchego curado, sabor intenso", 15990.0, ProductCategory.FRESCOS_LACTEOS, "", null, "kg"),
            Product("23", "Yogur Griego Natural", "Yogur griego natural, sin azúcar añadido", 1990.0, ProductCategory.FRESCOS_LACTEOS, "", null, "unidad"),
            Product("24", "Mantequilla Sin Sal", "Mantequilla sin sal, ideal para cocinar", 3990.0, ProductCategory.FRESCOS_LACTEOS, "", null, "paquete"),
            Product("25", "Crema Agria", "Crema agria natural, perfecta para salsas", 2990.0, ProductCategory.FRESCOS_LACTEOS, "", null, "unidad"),
            
            // Panadería y Pastelería
            Product("26", "Pan Integral", "Pan integral fresco, rico en fibra", 1990.0, ProductCategory.PANADERIA_PASTELERIA, "", null, "unidad"),
            Product("27", "Croissants Clásicos", "Croissants clásicos, hojaldrados y dorados", 990.0, ProductCategory.PANADERIA_PASTELERIA, "", null, "unidad"),
            Product("28", "Pastel de Chocolate", "Pastel de chocolate casero, decorado artesanalmente", 8990.0, ProductCategory.PANADERIA_PASTELERIA, "", null, "unidad"),
            Product("29", "Galletas de Avena", "Galletas de avena con pasas, horneadas diariamente", 2990.0, ProductCategory.PANADERIA_PASTELERIA, "", null, "paquete"),
            Product("30", "Baguette Francés", "Baguette francés tradicional, corteza crujiente", 1990.0, ProductCategory.PANADERIA_PASTELERIA, "", null, "unidad")
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
