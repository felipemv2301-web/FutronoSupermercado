package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intento1app.data.models.FirebaseUser
import com.example.intento1app.ui.theme.*
import com.example.intento1app.viewmodel.CustomersViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerCustomersScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    customersViewModel: CustomersViewModel = viewModel()
) {
    // Obtener datos del ViewModel
    val clients by customersViewModel.clients.collectAsStateWithLifecycle()
    val stats by customersViewModel.stats.collectAsStateWithLifecycle()
    val isLoading by customersViewModel.isLoading.collectAsStateWithLifecycle()
    
    // Formateador de números
    val numberFormatter = remember {
        NumberFormat.getNumberInstance(Locale("es", "CL"))
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Gestión de Clientes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoFondo
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = FutronoFondo
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )
        
        // Contenido
        if (isLoading && clients.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Estadísticas de clientes
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Resumen de Clientes",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                CustomerStatItem(
                                    title = "Total Clientes",
                                    value = if (isLoading) "..." else numberFormatter.format(stats.totalClients),
                                    color = Color.White
                                )
                                CustomerStatItem(
                                    title = "Nuevos Hoy",
                                    value = if (isLoading) "..." else numberFormatter.format(stats.newClientsToday),
                                    color = Color.White
                                )
                                CustomerStatItem(
                                    title = "Activos",
                                    value = if (isLoading) "..." else numberFormatter.format(stats.activeClients),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                
                // Lista de clientes
                if (clients.isNotEmpty()) {
                    item {
                        Text(
                            text = "Clientes Registrados",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                    
                    items(clients) { client ->
                        CustomerCard(
                            client = client,
                            customersViewModel = customersViewModel,
                            onViewDetails = { /* TODO: Implementar vista de detalles */ },
                            onEditCustomer = { /* TODO: Implementar edición */ }
                        )
                    }
                } else if (!isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay clientes registrados",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerStatItem(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun CustomerCard(
    client: FirebaseUser,
    customersViewModel: CustomersViewModel,
    onViewDetails: () -> Unit,
    onEditCustomer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Obtener información adicional del cliente
    val lastPurchase = customersViewModel.formatLastPurchase(client.id)
    val totalOrders = customersViewModel.getTotalOrders(client.id)
    val totalSpent = customersViewModel.getTotalSpent(client.id)
    
    // Formateador de moneda
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            currency = java.util.Currency.getInstance("CLP")
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = FutronoCafe.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (client.displayName.ifEmpty { client.email }).take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = FutronoCafe
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del cliente
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = client.displayName.ifEmpty { client.email },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = client.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (lastPurchase != "Sin compras") {
                    Text(
                        text = "Última compra: $lastPurchase",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Sin compras registradas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (totalOrders > 0) {
                    Text(
                        text = "$totalOrders pedido${if (totalOrders > 1) "s" else ""} • ${currencyFormatter.format(totalSpent)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Botones de acción
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onViewDetails,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = FutronoCafe.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Ver detalles",
                        tint = FutronoCafe,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = onEditCustomer,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = FutronoNaranja.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = FutronoNaranja,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
