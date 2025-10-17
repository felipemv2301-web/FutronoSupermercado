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
import com.example.intento1app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerCustomersScreen(
    onNavigateBack: () -> Unit,
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
                Text(
                    text = "Gestión de Clientes",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )
        
        // Contenido
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
                                value = "1,247",
                                color = Color.White
                            )
                            CustomerStatItem(
                                title = "Nuevos Hoy",
                                value = "23",
                                color = Color.White
                            )
                            CustomerStatItem(
                                title = "Activos",
                                value = "1,156",
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            // Lista de clientes recientes
            item {
                Text(
                    text = "Clientes Recientes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
            
            // Clientes de ejemplo
            items(getSampleCustomers()) { customer ->
                CustomerCard(
                    customer = customer,
                    onViewDetails = { /* TODO: Implementar vista de detalles */ },
                    onEditCustomer = { /* TODO: Implementar edición */ }
                )
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
    customer: SampleCustomer,
    onViewDetails: () -> Unit,
    onEditCustomer: () -> Unit,
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
                    text = customer.name.take(1).uppercase(),
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
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = customer.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Última compra: ${customer.lastPurchase}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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

// Data class para clientes de ejemplo
data class SampleCustomer(
    val id: String,
    val name: String,
    val email: String,
    val lastPurchase: String,
    val totalOrders: Int,
    val totalSpent: Double
)

// Función para obtener clientes de ejemplo
private fun getSampleCustomers(): List<SampleCustomer> {
    return listOf(
        SampleCustomer("1", "María González", "maria.gonzalez@email.com", "Hace 2 horas", 15, 125000.0),
        SampleCustomer("2", "Carlos Rodríguez", "carlos.rodriguez@email.com", "Ayer", 8, 89000.0),
        SampleCustomer("3", "Ana Martínez", "ana.martinez@email.com", "Hace 3 días", 22, 156000.0),
        SampleCustomer("4", "Luis Pérez", "luis.perez@email.com", "Hace 1 semana", 5, 45000.0),
        SampleCustomer("5", "Carmen López", "carmen.lopez@email.com", "Hace 2 días", 12, 98000.0),
        SampleCustomer("6", "Roberto Silva", "roberto.silva@email.com", "Hace 4 horas", 18, 134000.0),
        SampleCustomer("7", "Isabel Torres", "isabel.torres@email.com", "Ayer", 9, 67000.0),
        SampleCustomer("8", "Fernando Ruiz", "fernando.ruiz@email.com", "Hace 5 días", 14, 112000.0)
    )
}
