package com.futrono.simplificado.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.futrono.simplificado.ui.theme.FutronoFondo
import com.futrono.simplificado.ui.theme.FutronoCafeClaro

data class User(
    val id: String,
    val nombre: String,
    val apellido: String,
    val rut: String,
    val telefono: String,
    val email: String
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: ProductCategory,
    val imageUrl: String = "",
    val unit: String,
    val stock: Int = 100,
    val isAvailable: Boolean = true
)

enum class ProductCategory(
    val displayName: String,
    val containerColor: Color,
    val contentColor: Color,
    val textColor: Color,
    val icon: ImageVector
) {
    DESPENSA(
        displayName = "Despensa",
        containerColor = FutronoCafeClaro,
        contentColor = FutronoFondo,
        textColor = FutronoFondo,
        icon = Icons.Default.Inventory2
    )
}

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.price * quantity
}

// Modelos para Firebase
data class FirebaseUser(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val phoneNumber: String = "",
    val isEmailVerified: Boolean = false,
    val isActive: Boolean = true,
    val roles: List<String> = listOf("cliente")
)

