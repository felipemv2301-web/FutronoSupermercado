package com.example.intento1app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import android.graphics.BitmapFactory
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.intento1app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.User
import com.example.intento1app.ui.theme.*
import com.example.intento1app.viewmodel.AccessibilityViewModel
import com.example.intento1app.viewmodel.AnalyticsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.util.*
import androidx.compose.runtime.Composable
import androidx.core.text.color


data class WorkerFunction(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,          // color del icono
    val backgroundColor: Color,
    val titleColor: Color,  // color de fondo de la tarjeta
    val onClick: () -> Unit
)

data class Stat(
    val titleStat: String,
    val valueStat: String,
    val iconStat: ImageVector,
    val colorStat: Color
)

@Composable
fun WorkerHomeScreen(
    currentUser: User?,
    isAdmin: Boolean = false,
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
    analyticsViewModel: AnalyticsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // Hora y fecha actuales
    val currentTime = remember {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        formatter.format(Date())
    }
    val currentDate = remember {
        val formatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
        formatter.format(Date())
    }

    // Funciones principales con colores Futrono - filtradas según el rol
    val allMainFunctions = listOf(
        WorkerFunction(
            title = "Gestión de Pedidos",
            icon = Icons.Filled.ShoppingCart,
            iconColor = FutronoBlanco,             // Naranja principal para icono
            backgroundColor = FutronoNaranja,
            titleColor = FutronoBlanco,
            onClick = onOrdersClick
        ),
        WorkerFunction(
            title = "Gestión de Inventario",
            icon = Icons.Filled.Inventory,
            iconColor = FutronoBlanco,           // Café claro para icono
            backgroundColor = FutronoAmarillo,
            titleColor = FutronoBlanco,   // Naranja oscuro para fondo
            onClick = onProductsClick
        ),
        WorkerFunction(
            title = "Gestión de Clientes",
            icon = Icons.Filled.Groups,
            iconColor = FutronoBlanco,          // Café oscuro para icono
            backgroundColor = FutronoAzul,
            titleColor = FutronoBlanco,   // Superficie crema para fondo
            onClick = onCustomersClick
        )
    )
    // Filtrar funciones según el rol: trabajador solo ve Gestión de Pedidos e Inventario
    val mainFunctions = if (isAdmin) {
        allMainFunctions // Admin ve todas las funciones
    } else {
        allMainFunctions.filter { function ->
            function.title == "Gestión de Pedidos" || function.title == "Gestión de Inventario"
        }
    }

    // Funciones adicionales con colores Futrono y complementarios - filtradas según el rol
    val allAdditionalFunctions = listOf(
        WorkerFunction(
            title = "Ayuda",
            icon = Icons.Filled.HelpOutline,
            iconColor = FutronoFondo,            // Café claro para icono
            backgroundColor = FutronoCeleste,          // Fondo crema suave
            titleColor = FutronoFondo,
            onClick = onHelpClick
        ),
        WorkerFunction(
            title = "Cerrar Sesión",
            icon = Icons.Filled.Logout,
            iconColor = FutronoFondo,                 // Rojo error corporativo para icono
            backgroundColor = FutronoError,           // Fondo crema
            titleColor = FutronoFondo,
            onClick = onLogout
        )
    )
    
    // Filtrar funciones adicionales según el rol: trabajador solo ve Cerrar Sesión
    val additionalFunctions = if (isAdmin) {
        allAdditionalFunctions // Admin ve todas las funciones
    } else {
        allAdditionalFunctions.filter { function ->
            function.title == "Cerrar Sesión"
        }
    }

    // Obtener estadísticas reales del día desde AnalyticsViewModel (solo para admin)
    val dailyStatsData by analyticsViewModel.dailyStats.collectAsStateWithLifecycle()
    val isLoadingStats by analyticsViewModel.isLoading.collectAsStateWithLifecycle()
    
    // Formatear el total de ingresos
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            currency = java.util.Currency.getInstance("CLP")
        }
    }
    
    // Crear las tarjetas de estadísticas con datos reales (solo para admin)
    val dailyStats = remember(dailyStatsData, isLoadingStats) {
        if (isAdmin) {
            listOf(
                Stat(
                    "Pedidos", 
                    if (isLoadingStats) "..." else dailyStatsData.totalOrders.toString(), 
                    Icons.Filled.ShoppingCart, 
                    FutronoNaranja
                ),
                Stat(
                    "Completados", 
                    if (isLoadingStats) "..." else dailyStatsData.completedOrders.toString(), 
                    Icons.Filled.CheckCircle, 
                    FutronoVerde
                ),
                Stat(
                    "Pendientes", 
                    if (isLoadingStats) "..." else dailyStatsData.pendingOrders.toString(), 
                    Icons.Filled.Schedule, 
                    FutronoAmarillo
                ),
                Stat(
                    "Total", 
                    if (isLoadingStats) "..." else currencyFormatter.format(dailyStatsData.totalRevenue), 
                    Icons.Filled.AttachMoney, 
                    FutronoMorado
                )
            )
        } else {
            emptyList() // Trabajadores no ven estadísticas
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                val bitmap = remember {
                    BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo2)
                }
                Image(
                    painter = BitmapPainter(bitmap.asImageBitmap()),
                    contentDescription = "Logo de Futrono Supermercado",
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(FutronoCafe, shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = onAccessibilityClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(FutronoCafe, shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Configuración",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = onUserProfileClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(FutronoCafe, shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color.White)
                }
            }
        }

        // Bienvenida
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "¡Hola, ${currentUser?.nombre ?: if (isAdmin) "Administrador" else "Trabajador"}!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = if (isAdmin) "Bienvenido al panel de administración" else "Bienvenido al panel de trabajo",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.9f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$currentTime - $currentDate",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(
                                alpha = 0.8f
                            )
                        )
                    )
                }
            }
        }
        
        // Resumen del Día - Solo visible para administradores
        if (isAdmin) {
            //Componente de título y tarjeta
            Text(
                "Resumen del Día",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dailyStats) { stat ->
                    StatCard(
                        title = stat.titleStat,
                        value = stat.valueStat,
                        icon = stat.iconStat,
                        color = stat.colorStat
                    )
                }
            }
        }

        // Estructura de las Funciones principales
        Text("Funciones Principales", style = MaterialTheme.typography.titleMedium)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .heightIn(max = 400.dp) // tamaño máximo controlado para evitar conflicto
        ) {
            items(mainFunctions) { WorkerFunctionCardGridStyle(it) }
        }

        // Estructura de las Funciones adicionales
        Text("Herramientas Adicionales", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            additionalFunctions.chunked(2).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEachIndexed { index, function ->
                        Box(modifier = Modifier.weight(1f)) {
                            WorkerFunctionCardGridStyle(function)
                        }
                        if (index == 0 && rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WorkerFunctionCardGridStyle(function: WorkerFunction) {
    Card(
        modifier = Modifier
            .clickable { function.onClick() }
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = function.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = function.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = function.iconColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = function.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = function.titleColor
            )
        }
    }
}

//Tarjetas de estadísticas
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp), // Mantén el tamaño o ajústalo si es necesario
                tint = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
