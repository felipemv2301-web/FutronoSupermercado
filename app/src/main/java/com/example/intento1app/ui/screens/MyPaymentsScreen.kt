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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.FirebasePurchase
import com.example.intento1app.data.models.User
import com.example.intento1app.data.services.FirebaseService
import com.example.intento1app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPaymentsScreen(
    currentUser: User,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firebaseService = remember { FirebaseService() }
    val scope = rememberCoroutineScope()
    
    var purchases by remember { mutableStateOf<List<FirebasePurchase>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Cargar compras cuando se monta el componente
    LaunchedEffect(currentUser.id) {
        if (currentUser.id != "guest") {
            isLoading = true
            errorMessage = null
            
            scope.launch {
                val result = firebaseService.getUserPurchases(currentUser.id)
                result.onSuccess { purchaseList ->
                    purchases = purchaseList
                    isLoading = false
                }.onFailure { error ->
                    errorMessage = error.message ?: "Error al cargar las compras"
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Mis Compras",
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
                isLoading -> {
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
                                text = "Cargando tus compras...",
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
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch {
                                        val result = firebaseService.getUserPurchases(currentUser.id)
                                        result.onSuccess { purchaseList ->
                                            purchases = purchaseList
                                            isLoading = false
                                        }.onFailure { error ->
                                            errorMessage = error.message ?: "Error al cargar las compras"
                                            isLoading = false
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
                                text = "Como invitado, tus compras se guardan pero no se pueden mostrar aquí. Para ver tu historial de compras, crea una cuenta.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
                purchases.isEmpty() -> {
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
                                text = "No tienes compras aún",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = FutronoCafeOscuro
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cuando realices tu primera compra, aparecerá aquí",
                                style = MaterialTheme.typography.bodyLarge,
                                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
                else -> {
                    // Lista de compras
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(purchases) { purchase ->
                            PaymentCard(purchase = purchase)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(
    purchase: FirebasePurchase,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = purchase.purchaseDate?.let { 
        dateFormat.format(it.toDate()) 
    } ?: "Fecha no disponible"
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = FutronoBlanco
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header con número de compra y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Badge con número de compra
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = FutronoCafe.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = FutronoCafe
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Compra #${purchase.trackingNumber.takeIf { it.isNotEmpty() } ?: purchase.orderNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = FutronoCafeOscuro
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = FutronoCafeOscuro.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = FutronoCafeOscuro.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Total destacado
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = "$${String.format("%,.0f", purchase.totalPrice).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoNaranja
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = FutronoCafeOscuro.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${purchase.totalItems} ${if (purchase.totalItems == 1) "producto" else "productos"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = FutronoCafeOscuro.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Estado del pago con diseño mejorado
            val statusColor = when (purchase.paymentStatus.lowercase()) {
                "approved", "aprobado" -> Color(0xFF4CAF50)
                "pending", "pendiente" -> Color(0xFFFF9800)
                "rejected", "rechazado", "failure" -> Color(0xFFF44336)
                else -> FutronoCafe
            }
            val statusText = when (purchase.paymentStatus.lowercase()) {
                "approved", "aprobado" -> "Pagado"
                "pending", "pendiente" -> "Pendiente"
                "rejected", "rechazado", "failure" -> "Rechazado"
                else -> purchase.paymentStatus
            }
            val statusIcon = when (purchase.paymentStatus.lowercase()) {
                "approved", "aprobado" -> Icons.Default.CheckCircle
                "pending", "pendiente" -> Icons.Default.Schedule
                "rejected", "rechazado", "failure" -> Icons.Default.Cancel
                else -> Icons.Default.Payment
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = statusColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp)
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = statusColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = statusColor
                )
            }
            
            // Información de pago mejorada
            if (purchase.paymentId.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = FutronoCafe.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = FutronoCafe
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = purchase.paymentMethod,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = FutronoCafeOscuro
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ID: ${purchase.paymentId.take(24)}${if (purchase.paymentId.length > 24) "..." else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = FutronoCafeOscuro.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            // Resumen de productos mejorado
            if (purchase.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = FutronoCafe
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Productos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoCafeOscuro
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Lista de productos con mejor diseño
                purchase.items.take(3).forEachIndexed { index, item ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = FutronoCafe.copy(alpha = 0.03f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.productName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FutronoCafeOscuro,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "x${item.quantity}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .background(
                                        color = FutronoCafe.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Text(
                                text = "$${String.format("%,.0f", item.totalPrice).replace(",", ".")}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = FutronoNaranja
                            )
                        }
                    }
                }
                
                if (purchase.items.size > 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "y ${purchase.items.size - 3} producto${if (purchase.items.size - 3 > 1) "s" else ""} más",
                        style = MaterialTheme.typography.bodySmall,
                        color = FutronoCafeOscuro.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
            
            // Desglose de precios mejorado
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(
                color = FutronoCafe.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = FutronoCafe.copy(alpha = 0.03f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Subtotal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "$${String.format("%,.0f", purchase.subtotal).replace(",", ".")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro
                    )
                }
                if (purchase.iva > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "IVA (19%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FutronoCafeOscuro.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$${String.format("%,.0f", purchase.iva).replace(",", ".")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FutronoCafeOscuro
                        )
                    }
                }
                if (purchase.shipping > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Envío",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FutronoCafeOscuro.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$${String.format("%,.0f", purchase.shipping).replace(",", ".")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FutronoCafeOscuro
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = FutronoCafe.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoCafeOscuro
                    )
                    Text(
                        text = "$${String.format("%,.0f", purchase.totalPrice).replace(",", ".")}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoNaranja
                    )
                }
            }
            
            // Notas del pedido mejoradas
            if (purchase.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = FutronoCafe.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Note,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = FutronoCafe.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Notas",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = FutronoCafeOscuro.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = purchase.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = FutronoCafeOscuro.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

