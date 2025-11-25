package com.example.intento1app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.FirebasePurchase
import com.example.intento1app.data.services.FirebaseService
import com.example.intento1app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerOrdersScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()
    
    var selectedTabIndex by remember { mutableStateOf(0) } // 0 = En Preparación, 1 = Completados
    var isLoading by remember { mutableStateOf(true) }
    var ordersInPreparation by remember { mutableStateOf<List<FirebasePurchase>>(emptyList()) }
    var completedOrders by remember { mutableStateOf<List<FirebasePurchase>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCompletingOrder by remember { mutableStateOf<String?>(null) } // ID del pedido que se está completando
    
    // Función para cargar todos los pedidos
    fun loadOrders() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            
            // Cargar pedidos en preparación
            val preparationResult = firebaseService.getOrdersInPreparation()
            preparationResult.fold(
                onSuccess = { orders ->
                    ordersInPreparation = orders
                },
                onFailure = { error ->
                    errorMessage = error.message ?: "Error al cargar pedidos en preparación"
                }
            )
            
            // Cargar pedidos completados
            val completedResult = firebaseService.getCompletedOrders()
            completedResult.fold(
                onSuccess = { orders ->
                    completedOrders = orders
                },
                onFailure = { error ->
                    if (errorMessage == null) {
                        errorMessage = error.message ?: "Error al cargar pedidos completados"
                    }
                }
            )
            
            isLoading = false
        }
    }
    
    // Cargar pedidos al iniciar
    LaunchedEffect(Unit) {
        loadOrders()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Gestión de Pedidos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoBlanco
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = FutronoBlanco,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { loadOrders() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = FutronoBlanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FutronoCafe,
                    titleContentColor = FutronoBlanco
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
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FutronoError,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { loadOrders() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FutronoCafe
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            } else {
                // Pestañas para cambiar entre En Preparación y Completados
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = FutronoCafe
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { 
                            Text("En Preparación (${ordersInPreparation.size})")
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { 
                            Text("Completados (${completedOrders.size})")
                        }
                    )
                }
                
                // Lista de pedidos según la pestaña seleccionada
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> {
                            // Pestaña: En Preparación
                            if (ordersInPreparation.isEmpty()) {
                                item {
                                    EmptyStateCard(
                                        icon = Icons.Default.ShoppingCart,
                                        message = "No hay pedidos en preparación"
                                    )
                                }
                            } else {
                                items(ordersInPreparation) { order ->
                                    ExpandableOrderCard(
                                        order = order,
                                        onCompleteOrder = { orderId ->
                                            coroutineScope.launch {
                                                isCompletingOrder = orderId
                                                val result = firebaseService.completeOrder(orderId)
                                                result.fold(
                                                    onSuccess = {
                                                        // Recargar pedidos después de completar
                                                        loadOrders()
                                                        isCompletingOrder = null
                                                    },
                                                    onFailure = { error ->
                                                        errorMessage = "Error al completar pedido: ${error.message}"
                                                        isCompletingOrder = null
                                                    }
                                                )
                                            }
                                        },
                                        isCompleting = isCompletingOrder == order.id,
                                        showCompleteButton = true // Mostrar botón solo en preparación
                                    )
                                }
                            }
                        }
                        1 -> {
                            // Pestaña: Completados
                            if (completedOrders.isEmpty()) {
                                item {
                                    EmptyStateCard(
                                        icon = Icons.Default.CheckCircle,
                                        message = "No hay pedidos completados"
                                    )
                                }
                            } else {
                                items(completedOrders) { order ->
                                    ExpandableOrderCard(
                                        order = order,
                                        onCompleteOrder = { },
                                        isCompleting = false,
                                        showCompleteButton = false // No mostrar botón en completados
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = FutronoCafeOscuro.copy(alpha = 0.6f)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = FutronoCafeOscuro,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ExpandableOrderCard(
    order: FirebasePurchase,
    onCompleteOrder: (String) -> Unit,
    isCompleting: Boolean = false,
    showCompleteButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
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
            // Header expandible - siempre visible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Pedido #${order.orderNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoCafeOscuro
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cliente: ${order.userName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.8f)
                    )
                    Text(
                        text = formatDate(order.purchaseDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = FutronoCafeOscuro.copy(alpha = 0.7f)
                    )
                }
                
                // Icono de expandir/colapsar
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                    tint = FutronoCafe,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Contenido expandible
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Divider(
                        color = FutronoCafeOscuro.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // Información del cliente
                    Text(
                        text = "Información del Cliente",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoCafeOscuro,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Teléfono: ${order.userPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FutronoCafeOscuro.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "Dirección: ${order.userAddress.ifEmpty { "No especificada" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FutronoCafeOscuro.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Lista de productos
                    Text(
                        text = "Productos del Pedido (${order.totalItems}):",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoCafeOscuro,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${item.productName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = FutronoCafeOscuro.copy(alpha = 0.8f),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "x${item.quantity}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = FutronoCafeOscuro,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Divider(
                        color = FutronoCafeOscuro.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // Total y botón (solo si showCompleteButton es true)
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
                        
                        // Mostrar botón solo si showCompleteButton es true
                        if (showCompleteButton) {
                            Button(
                                onClick = { onCompleteOrder(order.id) },
                                enabled = !isCompleting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FutronoNaranja
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isCompleting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Terminar Pedido")
                                }
                            }
                        } else {
                            // Mostrar badge de completado si no hay botón
                            Surface(
                                color = FutronoVerde.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = FutronoVerde
                                    )
                                    Text(
                                        text = "Completado",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = FutronoVerde
                                    )
                                }
                            }
                        }
                    }
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
