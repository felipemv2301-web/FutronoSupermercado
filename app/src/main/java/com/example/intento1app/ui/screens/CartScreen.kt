package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intento1app.data.models.CartItem
import com.example.intento1app.ui.components.ScalableHeadlineSmall
import com.example.intento1app.ui.components.ScalableTitleLarge
import com.example.intento1app.ui.components.ScalableTitleMedium
import com.example.intento1app.ui.theme.FutronoBlanco
import com.example.intento1app.ui.theme.LocalTypography
import com.example.intento1app.ui.theme.FutronoCafe
import com.example.intento1app.ui.theme.FutronoNaranja

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                ScalableTitleMedium(
                    text = "Carrito de Compras",
                    color = FutronoBlanco,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = FutronoBlanco
                    )
                }
            },
            actions = {
                if (cartItems.isNotEmpty()) {
                    IconButton(onClick = onClearCart) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Vaciar carrito",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )
        
        // Contenido principal
        if (cartItems.isEmpty()) {
            // Carrito vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.headlineSmall,
                        color = FutronoBlanco,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Agrega productos para comenzar a comprar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoBlanco,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Lista de productos en el carrito
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onUpdateQuantity = onUpdateQuantity,
                        onRemoveItem = onRemoveItem
                    )
                }
            }
            
            // Resumen y botón de checkout
            CartSummary(
                cartItems = cartItems,
                onCheckout = onCheckout
            )
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%,.0f", cartItem.product.price).replace(",", ".")} por ${cartItem.product.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FutronoBlanco
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total: $${String.format("%,.0f", cartItem.totalPrice).replace(",", ".")}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
            }
            
            // Controles de cantidad
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onUpdateQuantity(cartItem.product.id, cartItem.quantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Reducir cantidad",
                            tint = FutronoCafe
                        )
                    }
                    
                    Text(
                        text = cartItem.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoBlanco,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    IconButton(
                        onClick = { onUpdateQuantity(cartItem.product.id, cartItem.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar cantidad",
                            tint = FutronoCafe
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Botón eliminar
                TextButton(
                    onClick = { onRemoveItem(cartItem.product.id) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Eliminar",
                        color = FutronoBlanco
                    )
                }
            }
        }
    }
}

@Composable
private fun CartSummary(
    cartItems: List<CartItem>,
    onCheckout: () -> Unit
) {
    val totalItems = cartItems.sumOf { it.quantity }
    val totalPrice = cartItems.sumOf { it.totalPrice }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total de productos:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FutronoBlanco
                )
                Text(
                    text = "$totalItems",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScalableTitleLarge(
                    text = "Total a pagar:",
                    fontWeight = FontWeight.Bold,
                    color = FutronoBlanco
                )
                Text(
                    text = "$${String.format("%,.0f", totalPrice).replace(",", ".")}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Botón de checkout
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FutronoNaranja
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Finalizar Compra",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
            }
        }
    }
}
