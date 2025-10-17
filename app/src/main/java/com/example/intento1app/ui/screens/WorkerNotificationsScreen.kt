package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import com.example.intento1app.R
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
fun WorkerNotificationsScreen(
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
                    text = "Notificaciones",
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
            actions = {
                IconButton(onClick = { /* TODO: Implementar marcar todas como leídas */ }) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Marcar todas como leídas"
                    )
                }
                IconButton(onClick = { /* TODO: Implementar configuración de notificaciones */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración"
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Resumen de notificaciones
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tienes 5 notificaciones nuevas",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "3 urgentes, 2 informativas",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }
            
            // Filtros
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { /* TODO: Implementar filtro */ },
                        label = { Text("Todas") },
                        selected = true
                    )
                    FilterChip(
                        onClick = { /* TODO: Implementar filtro */ },
                        label = { Text("Urgentes") },
                        selected = false
                    )
                    FilterChip(
                        onClick = { /* TODO: Implementar filtro */ },
                        label = { Text("Pedidos") },
                        selected = false
                    )
                    FilterChip(
                        onClick = { /* TODO: Implementar filtro */ },
                        label = { Text("Stock") },
                        selected = false
                    )
                }
            }
            
            // Lista de notificaciones
            items(getSampleNotifications()) { notification ->
                NotificationCard(
                    notification = notification,
                    onMarkAsRead = { /* TODO: Implementar marcar como leída */ },
                    onAction = { /* TODO: Implementar acción */ }
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: SampleNotification,
    onMarkAsRead: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF3E5F5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 2.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono de tipo de notificación
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = notification.type.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = notification.type.iconRes),
                    contentDescription = null,
                    tint = notification.type.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenido de la notificación
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Indicador de urgencia
                    if (notification.isUrgent) {
                        Surface(
                            color = Color(0xFFF44336),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "URGENTE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.timeAgo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    if (!notification.isRead) {
                        Surface(
                            color = FutronoCafe,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "NUEVO",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
                
                // Botones de acción
                if (notification.hasAction) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAction,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(notification.actionText)
                        }
                        
                        if (!notification.isRead) {
                            OutlinedButton(
                                onClick = onMarkAsRead
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Leída")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Data classes para notificaciones
data class SampleNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timeAgo: String,
    val isRead: Boolean,
    val isUrgent: Boolean,
    val hasAction: Boolean,
    val actionText: String
)

enum class NotificationType(
    val iconRes: Int,
    val color: Color
) {
    ORDER(R.drawable.ic_shopping_bag, Color(0xFF2196F3)),
    STOCK(R.drawable.ic_inventory, Color(0xFFFF9800)),
    SYSTEM(R.drawable.ic_settings, Color(0xFF9C27B0)),
    CUSTOMER(R.drawable.ic_customers, Color(0xFF00BCD4)),
    PAYMENT(R.drawable.ic_credit_card, Color(0xFF4CAF50))
}

// Función para obtener notificaciones de ejemplo
private fun getSampleNotifications(): List<SampleNotification> {
    return listOf(
        SampleNotification(
            id = "1",
            title = "Nuevo Pedido Urgente",
            message = "Pedido #1234 requiere atención inmediata. Cliente solicita entrega en 30 minutos.",
            type = NotificationType.ORDER,
            timeAgo = "Hace 5 minutos",
            isRead = false,
            isUrgent = true,
            hasAction = true,
            actionText = "Ver Pedido"
        ),
        SampleNotification(
            id = "2",
            title = "Stock Bajo",
            message = "Leche descremada está agotada. Revisar inventario y reabastecer.",
            type = NotificationType.STOCK,
            timeAgo = "Hace 15 minutos",
            isRead = false,
            isUrgent = true,
            hasAction = true,
            actionText = "Revisar Stock"
        ),
        SampleNotification(
            id = "3",
            title = "Pago Procesado",
            message = "Pago del pedido #1230 ha sido procesado exitosamente.",
            type = NotificationType.PAYMENT,
            timeAgo = "Hace 1 hora",
            isRead = false,
            isUrgent = false,
            hasAction = true,
            actionText = "Ver Detalles"
        ),
        SampleNotification(
            id = "4",
            title = "Cliente Nuevo",
            message = "Nuevo cliente registrado: María González. Revisar perfil.",
            type = NotificationType.CUSTOMER,
            timeAgo = "Hace 2 horas",
            isRead = true,
            isUrgent = false,
            hasAction = true,
            actionText = "Ver Cliente"
        ),
        SampleNotification(
            id = "5",
            title = "Sistema Actualizado",
            message = "El sistema ha sido actualizado con nuevas funcionalidades.",
            type = NotificationType.SYSTEM,
            timeAgo = "Hace 3 horas",
            isRead = true,
            isUrgent = false,
            hasAction = false,
            actionText = ""
        ),
        SampleNotification(
            id = "6",
            title = "Pedido Completado",
            message = "Pedido #1229 ha sido completado y está listo para entrega.",
            type = NotificationType.ORDER,
            timeAgo = "Hace 4 horas",
            isRead = true,
            isUrgent = false,
            hasAction = true,
            actionText = "Ver Pedido"
        )
    )
}
