package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.User
import com.example.intento1app.ui.theme.*
import com.example.intento1app.viewmodel.AccessibilityViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkerHomeScreen(
    currentUser: User?,
    onLogout: () -> Unit,
    onOrdersClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onCustomersClick: () -> Unit = {},
    onProductsClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onTeamClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onAccessibilityClick: () -> Unit = {},
    onUserProfileClick: () -> Unit = {},
    accessibilityViewModel: AccessibilityViewModel,
    modifier: Modifier = Modifier
) {
    val currentTime = remember { 
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        formatter.format(Date())
    }
    
    val currentDate = remember {
        val formatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale.getDefault())
        formatter.format(Date())
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con logo y botones de configuración
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                // Logo de Futrono
                Column {
                    Text(
                        text = "FUTRONO",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = FutronoCafe
                        )
                    )
                    Text(
                        text = "Panel Trabajador",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = FutronoNaranja
                        )
                    )
                }
                
                // Botones de configuración
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onAccessibilityClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = FutronoCafe,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración de Accesibilidad",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onUserProfileClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = FutronoCafe,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Mi cuenta",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        
        // Saludo y información del trabajador
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50) // Verde
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "¡Hola, ${currentUser?.nombre ?: "Trabajador"}!",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Bienvenido al panel de trabajo",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$currentTime - $currentDate",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }
        
        // Estadísticas rápidas
        Text(
            text = "Resumen del Día",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                StatCard(
                    title = "Pedidos Hoy",
                    value = "12",
                    icon = Icons.Default.ShoppingCart,
                    color = Color(0xFF2196F3)
                )
            }
            item {
                StatCard(
                    title = "Completados",
                    value = "8",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50)
                )
            }
            item {
                StatCard(
                    title = "Pendientes",
                    value = "4",
                    icon = painterResource(id = R.drawable.ic_time),
                    color = Color(0xFFFF9800)
                )
            }
            item {
                StatCard(
                    title = "Ventas",
                    value = "$89.990",
                    icon = painterResource(id = R.drawable.ic_money),
                    color = Color(0xFF9C27B0)
                )
            }
        }
        
        // Funciones principales
        Text(
            text = "Funciones Principales",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        WorkerFunctionCard(
            icon = Icons.Default.ShoppingCart,
            title = "Gestión de Pedidos",
            description = "Ver y gestionar pedidos de clientes",
            color = Color(0xFF2196F3),
            onClick = onOrdersClick
        )
        
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_inventory),
            title = "Inventario",
            description = "Gestionar stock de productos",
            color = Color(0xFF9C27B0),
            onClick = onInventoryClick
        )
        
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_customers),
            title = "Clientes",
            description = "Gestionar información de clientes",
            color = Color(0xFF00BCD4),
            onClick = onCustomersClick
        )
        
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_products),
            title = "Productos",
            description = "Gestionar catálogo de productos",
            color = Color(0xFF795548),
            onClick = onProductsClick
        )
        
        // Funciones secundarias
        Text(
            text = "Herramientas Adicionales",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_analytics),
            title = "Estadísticas",
            description = "Ver estadísticas de ventas y rendimiento",
            color = Color(0xFFFF9800),
            onClick = onReportsClick
        )
        
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_notifications),
            title = "Notificaciones",
            description = "Ver notificaciones y alertas",
            color = Color(0xFFE91E63),
            onClick = onNotificationsClick
        )
        
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_schedule),
            title = "Horarios",
            description = "Gestionar turnos y horarios",
            color = Color(0xFF607D8B),
            onClick = onScheduleClick
        )
        
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_team),
            title = "Equipo",
            description = "Gestionar equipo de trabajo",
            color = Color(0xFF3F51B5),
            onClick = onTeamClick
        )
        
        // Configuración y ayuda
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_settings),
            title = "Configuración",
            description = "Ajustes del sistema y preferencias",
            color = Color(0xFF757575),
            onClick = onSettingsClick
        )
        
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_help),
            title = "Ayuda",
            description = "Centro de ayuda y soporte",
            color = Color(0xFF009688),
            onClick = onHelpClick
        )
        
        WorkerFunctionCard(
            icon = painterResource(id = R.drawable.ic_logout),
            title = "Cerrar Sesión",
            description = "Salir del sistema",
            color = Color(0xFFF44336),
            onClick = onLogout
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: Any, // Puede ser ImageVector o Painter
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (icon) {
                is androidx.compose.ui.graphics.vector.ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        tint = color
                    )
                }
                is androidx.compose.ui.graphics.painter.Painter -> {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        tint = color
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WorkerFunctionCard(
    icon: Any, // Puede ser ImageVector o Painter
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono
            when (icon) {
                is androidx.compose.ui.graphics.vector.ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        tint = color
                    )
                }
                is androidx.compose.ui.graphics.painter.Painter -> {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        tint = color
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Texto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Flecha
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}