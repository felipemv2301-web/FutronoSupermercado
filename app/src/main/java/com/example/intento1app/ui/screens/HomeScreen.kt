package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.ui.components.CategoryCard
import com.example.intento1app.ui.components.ScalableHeadlineLarge
import com.example.intento1app.ui.components.ScalableHeadlineMedium
import com.example.intento1app.ui.components.ScalableBodyLarge
import com.example.intento1app.ui.theme.FutronoCafe
import com.example.intento1app.ui.theme.FutronoNaranja

@Composable
fun HomeScreen(
    onCategoryClick: (ProductCategory) -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int,
    onAccessibilityClick: () -> Unit,
    accessibilityViewModel: com.example.intento1app.viewmodel.AccessibilityViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header con logo y carrito
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo de Futrono
            Column {
                ScalableHeadlineLarge(
                    text = "FUTRONO",
                    color = FutronoCafe,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
                ScalableBodyLarge(
                    text = "Supermercado",
                    color = FutronoNaranja
                )
            }
            
            // Botón del carrito con badge
            Box {
                FloatingActionButton(
                    onClick = onCartClick,
                    containerColor = FutronoNaranja,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrito de compras",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                // Badge con cantidad de items
                if (cartItemCount > 0) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp),
                        containerColor = FutronoCafe
                    ) {
                        Text(
                            text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Título de categorías
        ScalableHeadlineMedium(
            text = "Nuestras Categorías",
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )
        
        // Grid de categorías
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Espaciado normal
            verticalArrangement = Arrangement.spacedBy(16.dp), // Espaciado normal
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ProductCategory.values()) { category ->
                CategoryCard(
                    category = category,
                    onClick = onCategoryClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // (Eliminado) Botón flotante inferior para dejar espacio a las categorías
    }
}
