package com.example.intento1app.data.models

import com.example.intento1app.R
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.*
import com.example.intento1app.ui.theme.FutronoBlanco
import com.example.intento1app.ui.theme.FutronoCafe
import com.example.intento1app.ui.theme.FutronoCafeClaro
import com.example.intento1app.ui.theme.FutronoCafeOscuro
import com.example.intento1app.ui.theme.FutronoCremaSuperficie
import com.example.intento1app.ui.theme.FutronoFondo
import com.example.intento1app.ui.theme.FutronoNaranjaClaro
import com.example.intento1app.ui.theme.FutronoNaranjaOscuro
import com.example.intento1app.ui.theme.FutronoSuccess



/**
 * Modelos de datos principales de la aplicación
 */

data class User(
    val id: String,
    val nombre: String,
    val apellido: String,
    val rut: String,
    val telefono: String,
    val email: String
)

data class ProductFirestore(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",    // 🔹 String, no enum
    val imageUrl: String = "",
    val unit: String = "unidad",
    val stock: Int = 100,
    val isAvailable: Boolean = true
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: ProductCategory,
    val imageUrl: String = "",
    val icon: ImageVector? = null,
    val unit: String = "unidad",
    val stock: Int = 100,
    val isAvailable: Boolean = true
)

enum class ProductCategory(
    val displayName: String,
    val containerColor: Color,
    val contentColor: Color,
    val textColor: Color,
    @DrawableRes val imageResId: Int,
    val icon: ImageVector
)

{
    CARNES_PESCADOS(
        displayName = "Carnes y Pescados",
        containerColor = Color(0xFF791F1F),     // Fondo más serio
        contentColor = FutronoBlanco,           // Icono blanco
        textColor = FutronoBlanco,              // Texto blanco
        imageResId = R.drawable.ic_carnes,
        icon = Icons.Default.KebabDining
    ),
    DESPENSA(
        displayName = "Despensa",
        containerColor = FutronoCafeClaro,      // Café claro
        contentColor = FutronoFondo,
        textColor = FutronoFondo,
        imageResId = R.drawable.ic_despensa,
        icon = Icons.Default.Inventory2
    ),
    FRUTAS_VERDURAS(
        displayName = "Frutas y Verduras",
        containerColor = FutronoSuccess,          // Verde institucional (para frescura)
        contentColor = FutronoFondo,
        textColor = FutronoFondo,
        imageResId = R.drawable.ic_fruta,
        icon = Icons.Default.Grass
    ),
    BEBIDAS_SNACKS(
        displayName = "Bebidas y Snacks",
        containerColor = Color(0xFF315677),  // Naranja oscuro vibrante
        contentColor = FutronoFondo,
        textColor = FutronoFondo,
        imageResId = R.drawable.ic_bebidas,
        icon = Icons.Default.LocalBar
    ),
    FRESCOS_LACTEOS(
        displayName = "Frescos y Lácteos",
        containerColor = Color(0xFF4B8C93), // Fondo crema
        contentColor = FutronoFondo,
        textColor = FutronoFondo,
        imageResId = R.drawable.ic_lacteos,
        icon = Icons.Default.Egg
    ),
    PANADERIA_PASTELERIA(
        displayName = "Panadería y Pastelería",
        containerColor = FutronoNaranjaClaro,   // Naranja cálido y amable
        contentColor = FutronoFondo,
        textColor = FutronoFondo,
        imageResId = R.drawable.ic_panaderia,
        icon = Icons.Default.BakeryDining
    ),
}

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.price * quantity
}

data class Category(
    val id: String,
    val name: String,
    val color: Color
)