package com.example.intento1app.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Modelos de datos para Firebase Firestore
 */

// Usuario
data class FirebaseUser(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val phoneNumber: String = "",
    val rut: String = "", // RUT del usuario
    val address: String = "", // Dirección del usuario
    val isEmailVerified: Boolean = false,
    val isActive: Boolean = true,
    val roles: List<String> = listOf("cliente"), // Roles del usuario, "cliente" por defecto
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)

// Producto en Firestore (modo simulación)
data class FirebaseProduct(
    // @DocumentId // Comentado temporalmente
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val stock: Int = 0,
    val isActive: Boolean = true,
    val tags: List<String> = emptyList(),
    // @ServerTimestamp // Comentado temporalmente
    val createdAt: String? = null, // Cambiado a String para simulación
    // @ServerTimestamp // Comentado temporalmente
    val updatedAt: String? = null // Cambiado a String para simulación
)

// Carrito de compras (modo simulación)
data class FirebaseCart(
    // @DocumentId // Comentado temporalmente
    val id: String = "",
    val userId: String = "",
    val items: List<FirebaseCartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val totalItems: Int = 0,
    // @ServerTimestamp // Comentado temporalmente
    val createdAt: String? = null, // Cambiado a String para simulación
    // @ServerTimestamp // Comentado temporalmente
    val updatedAt: String? = null // Cambiado a String para simulación
)

// Item del carrito
data class FirebaseCartItem(
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0
)

// Orden de compra (modo simulación)
data class FirebaseOrder(
    // @DocumentId // Comentado temporalmente
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val items: List<FirebaseCartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val totalItems: Int = 0,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: String = "",
    val paymentId: String = "",
    val shippingAddress: FirebaseAddress? = null,
    val notes: String = "",
    // @ServerTimestamp // Comentado temporalmente
    val createdAt: String? = null, // Cambiado a String para simulación
    // @ServerTimestamp // Comentado temporalmente
    val updatedAt: String? = null // Cambiado a String para simulación
)

// Historial de compras del usuario
data class FirebasePurchase(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userName: String = "",
    val userPhone: String = "", // Teléfono del usuario
    val userAddress: String = "", // Dirección del usuario (visual)
    val items: List<FirebaseCartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val iva: Double = 0.0,
    val shipping: Double = 0.0,
    val totalPrice: Double = 0.0,
    val totalItems: Int = 0,
    val paymentMethod: String = "Directo",
    val paymentId: String = "",
    val paymentStatus: String = "en_preparacion", // Estado por defecto: en preparación
    val orderNumber: String = "",
    val trackingNumber: String = "", // Número de seguimiento único
    @ServerTimestamp
    val purchaseDate: Timestamp? = null,
    val shippingAddress: FirebaseAddress? = null,
    val notes: String = ""
)

// Dirección de envío
data class FirebaseAddress(
    val street: String = "",
    val number: String = "",
    val city: String = "",
    val region: String = "",
    val postalCode: String = "",
    val country: String = "Chile",
    val instructions: String = ""
)

// Estado de la orden
enum class OrderStatus {
    PENDING,        // Pendiente
    CONFIRMED,      // Confirmada
    PROCESSING,     // Procesando
    SHIPPED,        // Enviada
    DELIVERED,      // Entregada
    CANCELLED,      // Cancelada
    REFUNDED        // Reembolsada
}

// Roles de usuario
enum class UserRole(val value: String) {
    CLIENTE("cliente"),
    TRABAJADOR("trabajador"),
    ADMIN("admin")
}

// Funciones de utilidad para roles
object RoleUtils {
    /**
     * Verifica si un usuario tiene un rol específico
     */
    fun hasRole(user: FirebaseUser, role: UserRole): Boolean {
        return user.roles.contains(role.value)
    }
    
    /**
     * Verifica si un usuario es cliente
     */
    fun isClient(user: FirebaseUser): Boolean {
        return hasRole(user, UserRole.CLIENTE)
    }
    
    /**
     * Verifica si un usuario es trabajador
     */
    fun isWorker(user: FirebaseUser): Boolean {
        return hasRole(user, UserRole.TRABAJADOR)
    }
    
    /**
     * Verifica si un usuario es admin
     */
    fun isAdmin(user: FirebaseUser): Boolean {
        return hasRole(user, UserRole.ADMIN)
    }
    
    /**
     * Obtiene el rol principal del usuario (el primero en la lista)
     */
    fun getPrimaryRole(user: FirebaseUser): UserRole? {
        return user.roles.firstOrNull()?.let { roleValue ->
            UserRole.values().find { it.value == roleValue }
        }
    }
}

// Categoría de producto (modo simulación)
data class FirebaseCategory(
    // @DocumentId // Comentado temporalmente
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true,
    // @ServerTimestamp // Comentado temporalmente
    val createdAt: String? = null // Cambiado a String para simulación
)

// Reseña de producto (modo simulación)
data class FirebaseReview(
    // @DocumentId // Comentado temporalmente
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val rating: Int = 0, // 1-5
    val comment: String = "",
    val isVerified: Boolean = false,
    // @ServerTimestamp // Comentado temporalmente
    val createdAt: String? = null // Cambiado a String para simulación
)

// Configuración de la app (modo simulación)
data class FirebaseAppConfig(
    // @DocumentId // Comentado temporalmente
    val id: String = "app_config",
    val appName: String = "Futrono Supermercado",
    val version: String = "1.0.0",
    val isMaintenanceMode: Boolean = false,
    val maintenanceMessage: String = "",
    val supportedPaymentMethods: List<String> = listOf("directo"),
    val shippingCost: Double = 0.0,
    val freeShippingThreshold: Double = 0.0,
    // @ServerTimestamp // Comentado temporalmente
    val updatedAt: String? = null // Cambiado a String para simulación
)
