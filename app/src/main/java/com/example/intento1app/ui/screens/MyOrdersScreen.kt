package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.R
import com.example.intento1app.data.models.FirebasePurchase
import com.example.intento1app.ui.theme.*
import com.example.intento1app.viewmodel.PaymentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    currentUser: com.example.intento1app.data.models.User,
    paymentViewModel: PaymentViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoadingPurchases by remember { mutableStateOf(true) }
    
    // Escuchar cambios en tiempo real en los pedidos del usuario
    LaunchedEffect(currentUser.id) {
        if (currentUser.id == "guest") {
            // Para usuarios invitados, no podemos usar el listener de Firebase
            // porque no tienen un ID fijo. Mostrar mensaje informativo.
            println("MyOrdersScreen: Usuario invitado - no se pueden mostrar pedidos históricos")
            isLoadingPurchases = false
        } else {
            // Para usuarios autenticados, usar el listener normal
            paymentViewModel.startUserOrdersListener(currentUser.id)
            isLoadingPurchases = false
        }
    }
    
    // Observar cambios en el historial de pedidos desde Firebase (sin cache local)
    val purchases by paymentViewModel.userOrders.collectAsState()
    
    // Debug: Mostrar información de los pedidos recibidos
    LaunchedEffect(purchases) {
        println("MyOrdersScreen: Pedidos recibidos: ${purchases.size}")
        purchases.forEachIndexed { index, purchase ->
            println("MyOrdersScreen: Pedido $index - ID: ${purchase.id}, UserID: '${purchase.userId}', OrderNumber: ${purchase.orderNumber}")
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Mis Pedidos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoFondo
                    ) 
                },
                actions = {
                    // Botón temporal de debug
                    IconButton(
                        onClick = {
                            paymentViewModel.debugAllOrders()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh),
                            contentDescription = "Debug",
                            tint = FutronoFondo,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
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
            if (isLoadingPurchases) {
                // Estado de carga
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = FutronoCafe
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando tus pedidos...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FutronoCafeOscuro
                        )
                    }
                }
            } else if (currentUser.id == "guest") {
                // Mensaje para usuarios invitados
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = FutronoCafe.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Modo Invitado",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = FutronoCafeOscuro
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Como invitado, tus pedidos se guardan pero no se pueden mostrar aquí. Para ver tu historial de pedidos, crea una cuenta.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FutronoCafeOscuro.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else if (purchases.isEmpty()) {
                // Estado vacío para usuarios autenticados
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shopping_bag),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = FutronoCafe.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "No tienes pedidos aún",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = FutronoCafeOscuro
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cuando realices tu primera compra, tus pedidos aparecerán aquí",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FutronoCafeOscuro.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                // Lista de pedidos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(purchases) { purchase ->
                        OrderCard(purchase = purchase)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    purchase: FirebasePurchase,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = purchase.purchaseDate?.let { 
        dateFormat.format(it.toDate()) 
    } ?: "Fecha no disponible"
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = " #${purchase.orderNumber}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoCafeOscuro
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.7f)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.0f", purchase.totalPrice)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoNaranja
                    )
                    Text(
                        text = "${purchase.totalItems} productos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Estado del pago
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = when (purchase.paymentStatus) {
                            "approved" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            "pending" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                            "rejected" -> Color(0xFFF44336).copy(alpha = 0.1f)
                            else -> FutronoCafe.copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = when (purchase.paymentStatus) {
                        "approved" -> R.drawable.ic_credit_card
                        "pending" -> R.drawable.ic_credit_card
                        "rejected" -> R.drawable.ic_delete
                        else -> R.drawable.ic_credit_card
                    }),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = when (purchase.paymentStatus) {
                        "approved" -> Color(0xFF4CAF50)
                        "pending" -> Color(0xFFFF9800)
                        "rejected" -> Color(0xFFF44336)
                        else -> FutronoCafe
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (purchase.paymentStatus) {
                        "approved" -> "Pagado"
                        "pending" -> "Pendiente"
                        "rejected" -> "Rechazado"
                        else -> purchase.paymentStatus
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = when (purchase.paymentStatus) {
                        "approved" -> Color(0xFF4CAF50)
                        "pending" -> Color(0xFFFF9800)
                        "rejected" -> Color(0xFFF44336)
                        else -> FutronoCafeOscuro
                    }
                )
            }
            
            // Resumen de productos
            if (purchase.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Productos:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoCafeOscuro
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                purchase.items.take(3).forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "• ${item.productName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FutronoCafeOscuro,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "x${item.quantity}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = FutronoCafeOscuro
                        )
                    }
                }
                
                if (purchase.items.size > 3) {
                    Text(
                        text = "• ... y ${purchase.items.size - 3} productos más",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Notas del pedido
            if (purchase.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Notas: ${purchase.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FutronoCafeOscuro.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = FutronoCafe.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )
            }
        }
    }
}
