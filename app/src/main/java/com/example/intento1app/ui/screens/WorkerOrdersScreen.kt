package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.intento1app.R
import com.example.intento1app.data.models.FirebasePurchase
import com.example.intento1app.data.models.OrderStatus
import com.example.intento1app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerOrdersScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Implementar alternativa para obtener órdenes sin PaymentViewModel
    var isLoading by remember { mutableStateOf(false) }
    
    // Estados vacíos temporalmente
    val ordersInPreparation = emptyList<FirebasePurchase>()
    val ordersReady = emptyList<FirebasePurchase>()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Gestión de Pedidos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoFondo
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = FutronoFondo,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FutronoCafe,
                    titleContentColor = FutronoFondo
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FutronoFondo)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = FutronoCafe
                    )
                }
            } else {
                // Pestañas para diferentes estados
                TabRow(
                    selectedTabIndex = 0,
                    containerColor = Color.White,
                    contentColor = FutronoCafe
                ) {
                    Tab(
                        selected = true,
                        onClick = { /* TODO: Implementar cambio de pestañas */ },
                        text = { Text("En Preparación (${ordersInPreparation.size})") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Implementar cambio de pestañas */ },
                        text = { Text("Listos (${ordersReady.size})") }
                    )
                }
                
                // Lista de órdenes en preparación
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (ordersInPreparation.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = FutronoCafeOscuro.copy(alpha = 0.6f)
                    )
                                    Text(
                                        text = "No hay pedidos en preparación",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = FutronoCafeOscuro,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(ordersInPreparation) { order ->
                            OrderCard(
                                order = order,
                                onMarkAsReady = {
                                    // TODO: Implementar marcado de orden como lista sin PaymentViewModel
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: FirebasePurchase,
    onMarkAsReady: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con número de orden y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedido #${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoCafeOscuro
                )
                Text(
                    text = formatDate(order.purchaseDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = FutronoCafeOscuro.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Información del cliente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Cliente: ${order.userName}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = FutronoCafeOscuro
                    )
                    Text(
                        text = "Teléfono: ${order.userPhone}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Dirección: ${order.userAddress.ifEmpty { "No especificada" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Resumen de productos
            Text(
                text = "Productos (${order.totalItems}):",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = FutronoCafeOscuro
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Lista de productos
            order.items.take(3).forEach { item ->
                Text(
                    text = "• ${item.productName} x${item.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FutronoCafeOscuro.copy(alpha = 0.7f)
                )
            }
            
            if (order.items.size > 3) {
                Text(
                    text = "... y ${order.items.size - 3} productos más",
                    style = MaterialTheme.typography.bodySmall,
                    color = FutronoCafeOscuro.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Total y botón
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: $${String.format("%,.0f", order.totalPrice).replace(",", ".")}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoCafe
                )
                
                Button(
                    onClick = onMarkAsReady,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoNaranja
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pedido Listo")
                }
            }
        }
    }
}

private fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
    return if (timestamp != null) {
        val date = timestamp.toDate()
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formatter.format(date)
    } else {
        "Fecha no disponible"
    }
}
