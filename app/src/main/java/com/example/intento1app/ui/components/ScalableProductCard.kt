package com.example.intento1app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.intento1app.ui.theme.*

/**
 * Tarjeta de producto escalable que respeta las preferencias de accesibilidad
 * Este componente demuestra cómo aplicar texto escalable en listas de productos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScalableProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FutronoSuperficie
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onProductClick() }
                .padding(16.dp)
        ) {
            // Imagen del producto (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(FutronoCafeClaro.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Imagen de ${product.name}",
                    tint = FutronoCafe,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Nombre del producto (texto escalable)
            ScalableTitleSmall(
                text = product.name,
                color = FutronoCafeOscuro,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Descripción del producto (texto escalable)
            ScalableBodySmall(
                text = product.description,
                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Fila inferior con precio y botón
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precio (texto escalable)
                ScalableTitleMedium(
                    text = "$${product.price}",
                    color = FutronoNaranja
                )
                
                // Botón de agregar al carrito
                FloatingActionButton(
                    onClick = onAddToCart,
                    modifier = Modifier.size(40.dp),
                    containerColor = FutronoNaranja,
                    contentColor = FutronoFondo,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar ${product.name} al carrito",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de producto compacta para listas con texto escalable
 */
@Composable
fun ScalableCompactProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = FutronoSuperficie
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onProductClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(FutronoCafeClaro.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Imagen de ${product.name}",
                    tint = FutronoCafe,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre del producto
                ScalableBodyMedium(
                    text = product.name,
                    color = FutronoCafeOscuro
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Descripción
                ScalableBodySmall(
                    text = product.description,
                    color = FutronoCafeOscuro.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Precio
                ScalableLabelLarge(
                    text = "$${product.price}",
                    color = FutronoNaranja
                )
            }
            
            // Botón de agregar
            IconButton(
                onClick = onAddToCart,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar ${product.name} al carrito",
                    tint = FutronoNaranja,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Lista de productos con texto escalable
 */
@Composable
fun ScalableProductList(
    products: List<Product>,
    onAddToCart: (Product) -> Unit,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(products) { product ->
            ScalableProductCard(
                product = product,
                onAddToCart = { onAddToCart(product) },
                onProductClick = { onProductClick(product) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Grid de productos con texto escalable
 */
@Composable
fun ScalableProductGrid(
    products: List<Product>,
    onAddToCart: (Product) -> Unit,
    onProductClick: (Product) -> Unit,
    columns: Int = 2,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(products) { product ->
            ScalableProductCard(
                product = product,
                onAddToCart = { onAddToCart(product) },
                onProductClick = { onProductClick(product) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Modelo de producto simplificado para el ejemplo
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val unit: String
)
