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
    val email: String,
    val direccion: String = "" // Dirección del usuario
)

data class ProductFirestore(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
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
    val unit: String,
    val stock: Int = 100,
    val isAvailable: Boolean = true
)
enum class ProductCategory(
    val displayName: String,
    val containerColor: Color,
    val textColor: Color,
    @DrawableRes val imageResId: Int
)

{
    CARNES_PESCADOS(
        displayName = "Carnes y Pescados",
        containerColor = Color(0xFF603D3D),     // Fondo más serio         // Icono blanco
        textColor = FutronoBlanco,              // Texto blanco
        imageResId = R.drawable.ic_carnes
    ),
    DESPENSA(
        displayName = "Despensa",
        containerColor = Color(0xFF565453),      // Café claro
        textColor = FutronoBlanco,
        imageResId = R.drawable.ic_despensa
    ),
    FRUTAS_VERDURAS(
        displayName = "Frutas y Verduras",
        containerColor = Color(0xFF434D3B),          // Verde institucional (para frescura)
        textColor = FutronoBlanco,
        imageResId = R.drawable.ic_fruta
    ),
    BEBIDAS_SNACKS(
        displayName = "Bebidas y Snacks",
        containerColor = Color(0xFF435755),  // Naranja oscuro vibrante
        textColor = FutronoBlanco,
        imageResId = R.drawable.ic_bebidas
    ),
    FRESCOS_LACTEOS(
        displayName = "Frescos y Lácteos",
        containerColor = Color(0xFF45495D), // Fondo crema
        textColor = FutronoBlanco,
        imageResId = R.drawable.ic_lacteos
    ),
    PANADERIA_PASTELERIA(
        displayName = "Panadería y Pastelería",
        containerColor = Color(0xFF6B5A3F),   // Naranja cálido y amable
        textColor = FutronoBlanco,
        imageResId = R.drawable.ic_panaderia
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
