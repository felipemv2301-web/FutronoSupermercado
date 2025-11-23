package com.example.intento1app.data.services

import com.example.intento1app.data.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import java.util.Calendar

/**
 * Servicio principal para interactuar con Firebase
 */
class FirebaseService {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    // ==================== AUTENTICACIÓN ====================
    
    /**
     * Registra un nuevo usuario
     */
    suspend fun registerUser(email: String, password: String, displayName: String, phoneNumber: String = ""): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user!!
            
            // Actualizar perfil
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdates).await()
            
            // Crear documento de usuario en Firestore
            val firebaseUser = FirebaseUser(
                id = user.uid,
                email = user.email ?: "",
                displayName = user.displayName ?: displayName,
                photoUrl = user.photoUrl?.toString() ?: "",
                phoneNumber = phoneNumber, // Usar el phoneNumber pasado como parámetro
                isEmailVerified = user.isEmailVerified,
                isActive = true,
                roles = listOf("cliente") // Rol por defecto para nuevos usuarios
                // createdAt y updatedAt se llenarán automáticamente con @ServerTimestamp
            )
            
            firestore.collection("users").document(user.uid).set(firebaseUser).await()
            
            println(" FirebaseService: Usuario registrado en Firebase: ${firebaseUser.email}")
            println(" FirebaseService: Teléfono guardado: ${firebaseUser.phoneNumber}")
            Result.success(firebaseUser)
        } catch (e: Exception) {
            println(" FirebaseService: Error al registrar usuario: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun signInUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user!!
            
            // Obtener datos del usuario desde Firestore
            val userDoc = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()
            
            val firebaseUser = if (userDoc.exists()) {
                userDoc.toObject<FirebaseUser>() ?: FirebaseUser(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    photoUrl = user.photoUrl?.toString() ?: "",
                    phoneNumber = user.phoneNumber ?: "",
                    isEmailVerified = user.isEmailVerified,
                    isActive = true,
                    roles = listOf("cliente") // Rol por defecto si no existe
                )
            } else {
                // Crear usuario si no existe en Firestore
                val newUser = FirebaseUser(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    photoUrl = user.photoUrl?.toString() ?: "",
                    phoneNumber = user.phoneNumber ?: "",
                    isEmailVerified = user.isEmailVerified,
                    isActive = true,
                    roles = listOf("cliente") // Rol por defecto para usuarios nuevos
                )
                firestore.collection("users").document(user.uid).set(newUser).await()
                newUser
            }
            
            println(" FirebaseService: Usuario logueado en Firebase: ${firebaseUser.email}")
            Result.success(firebaseUser)
        } catch (e: Exception) {
            println(" FirebaseService: Error al iniciar sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Envía un email de recuperación de contraseña
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            println(" FirebaseService: Email de recuperación enviado a: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            println(" FirebaseService: Error al enviar email de recuperación: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Cierra la sesión del usuario
     */
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            println(" FirebaseService: Sesión cerrada en Firebase")
            Result.success(Unit)
        } catch (e: Exception) {
            println(" FirebaseService: Error al cerrar sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene el usuario actual
     */
    fun getCurrentUser(): FirebaseUser? {
        val user = auth.currentUser
        return if (user != null) {
            FirebaseUser(
                id = user.uid,
                email = user.email ?: "",
                displayName = user.displayName ?: "",
                photoUrl = user.photoUrl?.toString() ?: "",
                phoneNumber = user.phoneNumber ?: "",
                isEmailVerified = user.isEmailVerified,
                isActive = true,
                roles = listOf("cliente") // Rol por defecto para compatibilidad
            )
        } else {
            null
        }
    }
    
    /**
     * Obtiene el usuario actual con roles completos desde Firestore
     */
    suspend fun getCurrentUserWithRoles(): Result<FirebaseUser?> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                println(" FirebaseService: Usuario autenticado encontrado: ${user.email}")
                println(" FirebaseService: UID: ${user.uid}")
                println(" FirebaseService: Email verificado: ${user.isEmailVerified}")
                
                val userDoc = firestore.collection("users")
                    .document(user.uid)
                    .get()
                    .await()
                
                val firebaseUser = if (userDoc.exists()) {
                    val existingUser = userDoc.toObject<FirebaseUser>()?.copy(id = user.uid)
                    println(" FirebaseService: Usuario encontrado en Firestore: ${existingUser?.email}")
                    existingUser
                } else {
                    println(" FirebaseService: Usuario no existe en Firestore, creando...")
                    // Crear usuario si no existe en Firestore
                    val newUser = FirebaseUser(
                        id = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName ?: "",
                        photoUrl = user.photoUrl?.toString() ?: "",
                        phoneNumber = user.phoneNumber ?: "",
                        isEmailVerified = user.isEmailVerified,
                        isActive = true,
                        roles = listOf("cliente")
                    )
                    firestore.collection("users").document(user.uid).set(newUser).await()
                    println(" FirebaseService: Usuario creado en Firestore: ${newUser.email}")
                    newUser
                }
                
                println(" FirebaseService: Usuario obtenido con roles: ${firebaseUser?.email}, Roles: ${firebaseUser?.roles}")
                Result.success(firebaseUser)
            } else {
                println("FirebaseService: No hay usuario autenticado")
                Result.success(null)
            }
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener usuario con roles: ${e.message}")
            Result.failure(e)
        }
    }
    
    // ==================== FIRESTORE ====================
    
    /**
     * Obtiene todos los productos (modo simulación)
     */
    suspend fun getProducts(): Result<List<FirebaseProduct>> {
        return try {
            // Simular productos
            val products = listOf(
                FirebaseProduct(
                    id = "prod_1",
                    name = "Producto Ejemplo 1",
                    description = "Descripción del producto 1",
                    price = 10000.0,
                    imageUrl = "",
                    category = "Categoría 1",
                    stock = 10,
                    isActive = true,
                    tags = listOf("tag1", "tag2")
                ),
                FirebaseProduct(
                    id = "prod_2",
                    name = "Producto Ejemplo 2",
                    description = "Descripción del producto 2",
                    price = 15000.0,
                    imageUrl = "",
                    category = "Categoría 2",
                    stock = 5,
                    isActive = true,
                    tags = listOf("tag3", "tag4")
                )
            )
            
            println(" FirebaseService: Productos obtenidos (simulado): ${products.size} productos")
            Result.success(products)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener productos: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene un producto por ID (modo simulación)
     */
    suspend fun getProduct(productId: String): Result<FirebaseProduct?> {
        return try {
            // Simular producto
            val product = FirebaseProduct(
                id = productId,
                name = "Producto $productId",
                description = "Descripción del producto $productId",
                price = 10000.0,
                imageUrl = "",
                category = "Categoría",
                stock = 10,
                isActive = true,
                tags = listOf("tag1")
            )
            
            println(" FirebaseService: Producto obtenido (simulado): ${product.name}")
            Result.success(product)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener producto: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Busca productos (modo simulación)
     */
    suspend fun searchProducts(query: String): Result<List<FirebaseProduct>> {
        return try {
            // Simular búsqueda
            val products = listOf(
                FirebaseProduct(
                    id = "search_1",
                    name = "Producto encontrado: $query",
                    description = "Resultado de búsqueda para: $query",
                    price = 10000.0,
                    imageUrl = "",
                    category = "Búsqueda",
                    stock = 10,
                    isActive = true,
                    tags = listOf("search")
                )
            )
            
            println(" FirebaseService: Búsqueda realizada (simulado): '$query' - ${products.size} resultados")
            Result.success(products)
        } catch (e: Exception) {
            println(" FirebaseService: Error en búsqueda: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene las órdenes del usuario (modo simulación)
     */
    suspend fun getUserOrders(userId: String): Result<List<FirebaseOrder>> {
        return try {
            // Simular órdenes
            val orders = listOf(
                FirebaseOrder(
                    id = "order_1",
                    userId = userId,
                    userEmail = "usuario@ejemplo.com",
                    items = listOf(
                        FirebaseCartItem(
                            productId = "prod_1",
                            productName = "Producto 1",
                            productImageUrl = "",
                            quantity = 2,
                            unitPrice = 10000.0,
                            totalPrice = 20000.0
                        )
                    ),
                    totalPrice = 20000.0,
                    totalItems = 2,
                    status = OrderStatus.PENDING,
                    paymentMethod = "directo",
                    paymentId = "payment_123",
                    shippingAddress = null,
                    notes = "Orden de prueba"
                )
            )
            
            println(" FirebaseService: Órdenes obtenidas (simulado): ${orders.size} órdenes")
            Result.success(orders)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener órdenes: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene las categorías (modo simulación)
     */
    suspend fun getCategories(): Result<List<FirebaseCategory>> {
        return try {
            // Simular categorías
            val categories = listOf(
                FirebaseCategory(
                    id = "cat_1",
                    name = "Categoría 1",
                    description = "Descripción de categoría 1",
                    imageUrl = "",
                    isActive = true
                ),
                FirebaseCategory(
                    id = "cat_2",
                    name = "Categoría 2",
                    description = "Descripción de categoría 2",
                    imageUrl = "",
                    isActive = true
                )
            )
            
            println(" FirebaseService: Categorías obtenidas (simulado): ${categories.size} categorías")
            Result.success(categories)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener categorías: ${e.message}")
            Result.failure(e)
        }
    }
    
    // ==================== COMPRAS Y HISTORIAL ====================
    
    /**
     * Guarda una compra en el historial del usuario
     */
    suspend fun savePurchase(purchase: FirebasePurchase): Result<String> {
        return try {
            val docRef = firestore.collection("purchases").add(purchase).await()
            println(" FirebaseService: Compra guardada: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            println(" FirebaseService: Error al guardar compra: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene el historial de compras de un usuario
     */
    suspend fun getUserPurchases(userId: String): Result<List<FirebasePurchase>> {
        return try {
            val snapshot = firestore.collection("purchases")
                .whereEqualTo("userId", userId)
                .orderBy("purchaseDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val purchases = snapshot.documents.mapNotNull { doc ->
                doc.toObject<FirebasePurchase>()?.copy(id = doc.id)
            }
            
            println(" FirebaseService: Historial obtenido: ${purchases.size} compras para usuario $userId")
            Result.success(purchases)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener historial: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene una compra específica por ID
     */
    suspend fun getPurchase(purchaseId: String): Result<FirebasePurchase?> {
        return try {
            val doc = firestore.collection("purchases").document(purchaseId).get().await()
            val purchase = if (doc.exists()) {
                doc.toObject<FirebasePurchase>()?.copy(id = doc.id)
            } else {
                null
            }
            
            println(" FirebaseService: Compra obtenida: ${purchase?.id ?: "No encontrada"}")
            Result.success(purchase)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener compra: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza el estado de una compra
     */
    suspend fun updatePurchaseStatus(purchaseId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("purchases").document(purchaseId)
                .update("paymentStatus", status)
                .await()
            
            println(" FirebaseService: Estado de compra actualizado: $purchaseId -> $status")
            Result.success(Unit)
        } catch (e: Exception) {
            println(" FirebaseService: Error al actualizar estado: ${e.message}")
            Result.failure(e)
        }
    }
    
    // ==================== NUEVO SISTEMA DE PEDIDOS POR USUARIO ====================
    
    /**
     * Función de prueba para verificar la conexión con Firebase (DESHABILITADA)
     */
    /*
    suspend fun testFirebaseConnection(): Result<String> {
        return try {
            println("🧪 FirebaseService: Probando conexión con Firebase...")
            
            // Intentar crear un documento de prueba
            val testDoc = firestore.collection("test")
                .document("connection_test")
                .set(mapOf(
                    "timestamp" to com.google.firebase.Timestamp.now(),
                    "message" to "Conexión exitosa"
                ))
                .await()
            
            println(" FirebaseService: Conexión con Firebase exitosa")
            Result.success("Conexión exitosa")
        } catch (e: Exception) {
            println(" FirebaseService: Error de conexión: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    */
    
    /**
     * Función de prueba para verificar permisos de escritura en users (DESHABILITADA)
     */
    /*
    suspend fun testUserWritePermission(userId: String): Result<String> {
        return try {
            println("🧪 FirebaseService: Probando permisos de escritura para usuario: $userId")
            
            // Intentar escribir un campo de prueba en el documento del usuario
            val testData = mapOf(
                "testField" to "test_value_${System.currentTimeMillis()}",
                "testTimestamp" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection("users")
                .document(userId)
                .update(testData)
                .await()
            
            println(" FirebaseService: Permisos de escritura en users exitosos")
            Result.success("Permisos de escritura OK")
        } catch (e: Exception) {
            println(" FirebaseService: Error de permisos de escritura: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    */
    
    /**
     * Función de prueba para verificar permisos de escritura en orders (DESHABILITADA)
     */
    /*
    suspend fun testOrdersWritePermission(): Result<String> {
        return try {
            println("🧪 FirebaseService: Probando permisos de escritura en orders...")
            
            // Intentar escribir un documento de prueba en orders
            val testData = mapOf(
                "testField" to "test_orders_${System.currentTimeMillis()}",
                "testTimestamp" to com.google.firebase.Timestamp.now(),
                "userId" to "test_user"
            )
            
            val docRef = firestore.collection("orders")
                .add(testData)
                .await()
            
            println(" FirebaseService: Permisos de escritura en orders exitosos - ID: ${docRef.id}")
            Result.success("Permisos de escritura en orders OK")
        } catch (e: Exception) {
            println(" FirebaseService: Error de permisos de escritura en orders: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Función de prueba para guardar un pedido simple (DESHABILITADA)
     */
    suspend fun testSaveSimpleOrder(userId: String): Result<String> {
        return try {
            println("🧪 FirebaseService: Probando guardado de pedido simple para usuario: $userId")
            
            val simpleOrder = mapOf(
                "userId" to userId,
                "orderNumber" to "TEST-${System.currentTimeMillis()}",
                "totalPrice" to 10000.0,
                "userEmail" to "test@test.com",
                "userName" to "Usuario Test",
                "paymentStatus" to "approved",
                "purchaseDate" to com.google.firebase.Timestamp.now(),
                "items" to listOf(
                    mapOf(
                        "productName" to "Producto Test",
                        "quantity" to 1,
                        "unitPrice" to 10000.0,
                        "totalPrice" to 10000.0
                    )
                )
            )
            
            val docRef = firestore.collection("orders")
                .add(simpleOrder)
                .await()
            
            println(" FirebaseService: Pedido simple guardado exitosamente - ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            println(" FirebaseService: Error al guardar pedido simple: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    */
    
    /**
     * Guarda un pedido en la colección orders global
     */
    suspend fun saveUserOrder(userId: String, order: FirebasePurchase): Result<String> {
        return try {
            println(" FirebaseService: Intentando guardar pedido para usuario: $userId")
            println("FirebaseService: Datos del pedido: ${order.orderNumber}, Total: ${order.totalPrice}")
            println(" FirebaseService: Nombre: ${order.userName}, Teléfono: ${order.userPhone}")
            
            // Crear un mapa simple para evitar problemas con el modelo
            val orderData = mapOf(
                "userId" to order.userId,
                "userEmail" to order.userEmail,
                "userName" to order.userName,
                "userPhone" to order.userPhone, // Agregar teléfono del usuario
                "orderNumber" to order.orderNumber,
                "totalPrice" to order.totalPrice,
                "subtotal" to order.subtotal,
                "iva" to order.iva,
                "shipping" to order.shipping,
                "totalItems" to order.totalItems,
                "paymentMethod" to order.paymentMethod,
                "paymentId" to order.paymentId,
                "paymentStatus" to order.paymentStatus,
                "notes" to order.notes,
                "purchaseDate" to com.google.firebase.Timestamp.now(),
                "items" to order.items.map { item ->
                    mapOf(
                        "productId" to item.productId,
                        "productName" to item.productName,
                        "productImageUrl" to item.productImageUrl,
                        "quantity" to item.quantity,
                        "unitPrice" to item.unitPrice,
                        "totalPrice" to item.totalPrice
                    )
                }
            )
            
            println("FirebaseService: Datos a guardar en Firebase: $orderData")
            
            // Guardar en la colección orders global
            val docRef = firestore.collection("orders")
                .add(orderData)
                .await()
            
            println(" FirebaseService: Pedido guardado exitosamente en colección orders: ${docRef.id}")
            println("FirebaseService: Ruta completa: orders/${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            println(" FirebaseService: Error al guardar pedido: ${e.message}")
            println("FirebaseService: Tipo de error: ${e.javaClass.simpleName}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene el historial de pedidos de un usuario desde la colección orders global
     */
    suspend fun getUserOrdersFromProfile(userId: String): Result<List<FirebasePurchase>> {
        return try {
            println(" FirebaseService: Obteniendo pedidos para usuario: $userId")
            
            val snapshot = firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            println("FirebaseService: Snapshot obtenido con ${snapshot.documents.size} documentos")
            
            val orders = snapshot.documents.mapNotNull { doc ->
                println("FirebaseService: Procesando documento: ${doc.id}")
                println("FirebaseService: Datos del documento: ${doc.data}")
                
                val purchase = doc.toObject<FirebasePurchase>()?.copy(id = doc.id)
                if (purchase != null) {
                    println(" FirebaseService: Pedido procesado: ${purchase.orderNumber} para usuario ${purchase.userId}")
                } else {
                    println(" FirebaseService: Error al convertir documento ${doc.id} a FirebasePurchase")
                }
                purchase
            }
            
            println(" FirebaseService: Pedidos obtenidos: ${orders.size} pedidos para usuario $userId")
            Result.success(orders)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener pedidos: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Método de debug para verificar todos los pedidos en la colección
     */
    suspend fun debugAllOrders(): Result<List<Map<String, Any?>>> {
        return try {
            println("FirebaseService: Obteniendo TODOS los pedidos para debug")
            
            val snapshot = firestore.collection("orders")
                .get()
                .await()
            
            println("FirebaseService: Total de documentos en colección 'orders': ${snapshot.documents.size}")
            
            val allOrders = snapshot.documents.mapNotNull { doc ->
                val data = doc.data
                println("FirebaseService: Documento ${doc.id}: $data")
                data
            }
            
            Result.success(allOrders)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener todos los pedidos: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene un pedido específico de la subcolección del usuario
     */
    suspend fun getUserOrder(userId: String, orderId: String): Result<FirebasePurchase?> {
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("orders")
                .document(orderId)
                .get()
                .await()
            
            val order = if (doc.exists()) {
                doc.toObject<FirebasePurchase>()?.copy(id = doc.id)
            } else {
                null
            }
            
            println(" FirebaseService: Pedido obtenido del perfil: ${order?.id ?: "No encontrado"}")
            Result.success(order)
        } catch (e: Exception) {
            println(" FirebaseService: Error al obtener pedido del perfil: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza el estado de un pedido en la subcolección del usuario
     */
    suspend fun updateUserOrderStatus(userId: String, orderId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("orders")
                .document(orderId)
                .update("paymentStatus", status)
                .await()
            
            println(" FirebaseService: Estado de pedido actualizado en perfil: $orderId -> $status")
            Result.success(Unit)
        } catch (e: Exception) {
            println(" FirebaseService: Error al actualizar estado del pedido: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Elimina un pedido de la subcolección del usuario
     */
    suspend fun deleteUserOrder(userId: String, orderId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("orders")
                .document(orderId)
                .delete()
                .await()
            
            println(" FirebaseService: Pedido eliminado del perfil: $orderId")
            Result.success(Unit)
        } catch (e: Exception) {
            println(" FirebaseService: Error al eliminar pedido del perfil: ${e.message}")
            Result.failure(e)
        }
    }
    
    // ==================== LISTENERS EN TIEMPO REAL ====================
    
    /**
     * Escucha cambios en tiempo real en los pedidos del usuario desde la colección orders global
     * Retorna un Flow que se actualiza automáticamente cuando hay cambios
     */
    fun listenToUserOrders(userId: String): Flow<List<FirebasePurchase>> = callbackFlow {
        println(" FirebaseService: Iniciando listener para usuario: $userId")
        
        val listener = firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println(" FirebaseService: Error en listener de pedidos: ${error.message}")
                    println("FirebaseService: Código de error: ${error.code}")
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    println("FirebaseService: Snapshot recibido con ${snapshot.documents.size} documentos")
                    
                    val orders = snapshot.documents.mapNotNull { doc ->
                        println("FirebaseService: Procesando documento: ${doc.id}")
                        println("FirebaseService: Datos del documento: ${doc.data}")
                        
                        val purchase = doc.toObject<FirebasePurchase>()?.copy(id = doc.id)
                        if (purchase != null) {
                            println(" FirebaseService: Pedido procesado: ${purchase.orderNumber} para usuario ${purchase.userId}")
                        } else {
                            println(" FirebaseService: Error al convertir documento ${doc.id} a FirebasePurchase")
                        }
                        purchase
                    }
                    
                    println(" FirebaseService: Listener actualizado: ${orders.size} pedidos para usuario $userId")
                    trySend(orders)
                } else {
                    println(" FirebaseService: Snapshot es null")
                    trySend(emptyList())
                }
            }
        
        awaitClose { 
            println(" FirebaseService: Cerrando listener para usuario: $userId")
            listener.remove() 
        }
    }
    
    /**
     * Escucha cambios en tiempo real en un pedido específico del usuario
     */
    fun listenToUserOrder(userId: String, orderId: String): Flow<FirebasePurchase?> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("orders")
            .document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println(" FirebaseService: Error en listener de pedido específico: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val order = snapshot.toObject<FirebasePurchase>()?.copy(id = snapshot.id)
                    println(" FirebaseService: Listener de pedido específico actualizado: ${order?.id}")
                    trySend(order)
                } else {
                    println(" FirebaseService: Pedido específico no encontrado")
                    trySend(null)
                }
            }
        
        awaitClose { listener.remove() }
    }
    
    // ==================== STORAGE ====================
    
    /**
     * Sube una imagen (modo simulación)
     */
    suspend fun uploadImage(imagePath: String, fileName: String): Result<String> {
        return try {
            // Simular URL de imagen
            val imageUrl = "https://ejemplo.com/images/$fileName"
            println(" FirebaseService: Imagen subida (simulado): $imageUrl")
            Result.success(imageUrl)
        } catch (e: Exception) {
            println(" FirebaseService: Error al subir imagen: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Elimina una imagen (modo simulación)
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            println(" FirebaseService: Imagen eliminada (simulado): $imageUrl")
            Result.success(Unit)
        } catch (e: Exception) {
            println(" FirebaseService: Error al eliminar imagen: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza el estado de una orden (para trabajadores)
     */
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        return try {
            println("FirebaseService: Actualizando estado de orden $orderId a $newStatus")
            
            firestore.collection("orders")
                .document(orderId)
                .update("paymentStatus", newStatus)
                .await()
            
            println("✅ FirebaseService: Estado de orden actualizado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ FirebaseService: Error al actualizar estado de orden: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Listener en tiempo real para todas las órdenes (para trabajadores)
     */
    fun listenToAllOrders(): kotlinx.coroutines.flow.Flow<List<FirebasePurchase>> = kotlinx.coroutines.flow.callbackFlow {
        val listener = firestore.collection("orders")
            .orderBy("purchaseDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ FirebaseService: Error en listener de todas las órdenes: ${error.message}")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject<FirebasePurchase>()?.copy(id = doc.id)
                        } catch (e: Exception) {
                            println("FirebaseService: Error al convertir documento ${doc.id}: ${e.message}")
                            null
                        }
                    }
                    println("✅ FirebaseService: Listener de todas las órdenes actualizado: ${orders.size} órdenes")
                    trySend(orders)
                }
            }
        
        awaitClose { listener.remove() }
    }
    
    // ==================== GESTIÓN DE CLIENTES ====================
    
    /**
     * Obtiene todos los clientes desde Firestore
     * Filtra solo usuarios con rol "cliente"
     */
    suspend fun getAllClients(): Result<List<FirebaseUser>> {
        return try {
            val snapshot = firestore.collection("users")
                .get()
                .await()
            
            val allUsers = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject<FirebaseUser>()?.copy(id = doc.id)
                } catch (e: Exception) {
                    println("FirebaseService: Error al convertir usuario ${doc.id}: ${e.message}")
                    null
                }
            }
            
            // Filtrar solo clientes (excluir admin y trabajador)
            val clients = allUsers.filter { user ->
                user.roles.contains("cliente") && !user.roles.contains("admin") && !user.roles.contains("trabajador")
            }
            
            println("✅ FirebaseService: Clientes obtenidos: ${clients.size} de ${allUsers.size} usuarios totales")
            Result.success(clients)
        } catch (e: Exception) {
            println("❌ FirebaseService: Error al obtener clientes: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Listener en tiempo real para todos los clientes
     * Filtra solo usuarios con rol "cliente"
     */
    fun listenToAllClients(): Flow<List<FirebaseUser>> = callbackFlow {
        println("FirebaseService: Iniciando listener de clientes")
        
        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ FirebaseService: Error en listener de clientes: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val allUsers = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject<FirebaseUser>()?.copy(id = doc.id)
                        } catch (e: Exception) {
                            println("FirebaseService: Error al convertir usuario ${doc.id}: ${e.message}")
                            null
                        }
                    }
                    
                    // Filtrar solo clientes (excluir admin y trabajador)
                    val clients = allUsers.filter { user ->
                        user.roles.contains("cliente") && !user.roles.contains("admin") && !user.roles.contains("trabajador")
                    }
                    
                    println("✅ FirebaseService: Listener de clientes actualizado: ${clients.size} clientes de ${allUsers.size} usuarios")
                    trySend(clients)
                } else {
                    println("FirebaseService: Snapshot de clientes es null")
                    trySend(emptyList())
                }
            }
        
        awaitClose {
            println("FirebaseService: Cerrando listener de clientes")
            listener.remove()
        }
    }
    
    /**
     * Obtiene las órdenes de un cliente específico para calcular actividad
     */
    suspend fun getClientOrders(userId: String, daysBack: Int = 30): Result<List<FirebasePurchase>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -daysBack)
            val startDate = Timestamp(calendar.time)
            
            val snapshot = firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("purchaseDate", startDate)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject<FirebasePurchase>()?.copy(id = doc.id)
                } catch (e: Exception) {
                    println("FirebaseService: Error al convertir orden ${doc.id}: ${e.message}")
                    null
                }
            }
            
            println("✅ FirebaseService: Órdenes del cliente obtenidas: ${orders.size} órdenes en últimos $daysBack días")
            Result.success(orders)
        } catch (e: Exception) {
            println("❌ FirebaseService: Error al obtener órdenes del cliente: ${e.message}")
            Result.failure(e)
        }
    }
}