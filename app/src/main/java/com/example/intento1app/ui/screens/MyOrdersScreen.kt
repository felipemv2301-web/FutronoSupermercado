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
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.R
import com.example.intento1app.data.models.FirebasePurchase
import com.example.intento1app.data.models.User
import com.example.intento1app.data.services.FirebaseService
import com.example.intento1app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    currentUser: User,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firebaseService = remember { FirebaseService() }
    val scope = rememberCoroutineScope()
    
    var purchases by remember { mutableStateOf<List<FirebasePurchase>>(emptyList()) }
    var isLoadingPurchases by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Cargar compras cuando se monta el componente
    LaunchedEffect(currentUser.id) {
        if (currentUser.id != "guest") {
            isLoadingPurchases = true
            errorMessage = null
            
            scope.launch {
                val result = firebaseService.getUserPurchases(currentUser.id)
                result.onSuccess { purchaseList ->
                    purchases = purchaseList
                    isLoadingPurchases = false
                }.onFailure { error ->
                    errorMessage = error.message ?: "Error al cargar los pedidos"
                    isLoadingPurchases = false
                }
            }
        } else {
            isLoadingPurchases = false
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
            when {
                isLoadingPurchases -> {
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
                }
                errorMessage != null -> {
                    // Estado de error
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color(0xFFF44336)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Error al cargar",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = FutronoCafeOscuro
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "Ocurrió un error desconocido",
                                style = MaterialTheme.typography.bodyLarge,
                                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    isLoadingPurchases = true
                                    errorMessage = null
                                    scope.launch {
                                        val result = firebaseService.getUserPurchases(currentUser.id)
                                        result.onSuccess { purchaseList ->
                                            purchases = purchaseList
                                            isLoadingPurchases = false
                                        }.onFailure { error ->
                                            errorMessage = error.message ?: "Error al cargar los pedidos"
                                            isLoadingPurchases = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FutronoCafe
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                currentUser.id == "guest" -> {
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
                                imageVector = Icons.Default.Person,
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
                }
                (currentUser.id != "guest" && purchases.isEmpty()) -> {
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
                                imageVector = Icons.Default.ShoppingBag,
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
                }
                else -> {
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
                        text = "Pedido #${purchase.trackingNumber.takeIf { it.isNotEmpty() } ?: purchase.orderNumber}",
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
                        color = when (purchase.paymentStatus.lowercase()) {
                            in listOf("approved", "aprobado") -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            in listOf("pending", "pendiente") -> Color(0xFFFF9800).copy(alpha = 0.1f)
                            in listOf("rejected", "rechazado", "failure") -> Color(0xFFF44336).copy(alpha = 0.1f)
                            else -> FutronoCafe.copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = when (purchase.paymentStatus.lowercase()) {
                        in listOf("approved", "aprobado") -> Icons.Default.CheckCircle
                        in listOf("pending", "pendiente") -> Icons.Default.Schedule
                        in listOf("rejected", "rechazado", "failure") -> Icons.Default.Cancel
                        else -> Icons.Default.Payment
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = when (purchase.paymentStatus.lowercase()) {
                        in listOf("approved", "aprobado") -> Color(0xFF4CAF50)
                        in listOf("pending", "pendiente") -> Color(0xFFFF9800)
                        in listOf("rejected", "rechazado", "failure") -> Color(0xFFF44336)
                        else -> FutronoCafe
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (purchase.paymentStatus.lowercase()) {
                        in listOf("approved", "aprobado") -> "Pagado"
                        in listOf("pending", "pendiente") -> "Pendiente"
                        in listOf("rejected", "rechazado", "failure") -> "Rechazado"
                        else -> purchase.paymentStatus
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = when (purchase.paymentStatus.lowercase()) {
                        in listOf("approved", "aprobado") -> Color(0xFF4CAF50)
                        in listOf("pending", "pendiente") -> Color(0xFFFF9800)
                        in listOf("rejected", "rechazado", "failure") -> Color(0xFFF44336)
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
