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
import androidx.compose.material.icons.Icons.Default
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
import androidx.compose.ui.text.style.TextOverflow
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
import android.app.DatePickerDialog
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp


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

/**
 * Componente para seleccionar rango de fechas
 */
@Composable
fun DateRangeSelector(
    startDate: Date?,
    endDate: Date?,
    onStartDateSelected: (Date?) -> Unit,
    onEndDateSelected: (Date?) -> Unit,
    onClearDates: () -> Unit,
    showStartPicker: Boolean,
    showEndPicker: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    // DatePicker para fecha de inicio
    LaunchedEffect(showStartPicker) {
        if (showStartPicker) {
            val calendar = Calendar.getInstance()
            val startYear = startDate?.let { 
                val cal = Calendar.getInstance()
                cal.time = it
                cal.get(Calendar.YEAR)
            } ?: calendar.get(Calendar.YEAR)
            val startMonth = startDate?.let {
                val cal = Calendar.getInstance()
                cal.time = it
                cal.get(Calendar.MONTH)
            } ?: calendar.get(Calendar.MONTH)
            val startDay = startDate?.let {
                val cal = Calendar.getInstance()
                cal.time = it
                cal.get(Calendar.DAY_OF_MONTH)
            } ?: calendar.get(Calendar.DAY_OF_MONTH)
            
            val dialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                    onStartDateSelected(selectedDate)
                },
                startYear,
                startMonth,
                startDay
            )
            
            // Limitar fecha máxima a la fecha de fin si existe
            endDate?.let {
                dialog.datePicker.maxDate = it.time
            }
            
            // Manejar cancelación
            dialog.setOnCancelListener {
                onDismiss()
            }
            
            dialog.show()
        }
    }
    
    // DatePicker para fecha de fin
    LaunchedEffect(showEndPicker) {
        if (showEndPicker) {
            val calendar = Calendar.getInstance()
            val endYear = endDate?.let {
                val cal = Calendar.getInstance()
                cal.time = it
                cal.get(Calendar.YEAR)
            } ?: calendar.get(Calendar.YEAR)
            val endMonth = endDate?.let {
                val cal = Calendar.getInstance()
                cal.time = it
                cal.get(Calendar.MONTH)
            } ?: calendar.get(Calendar.MONTH)
            val endDay = endDate?.let {
                val cal = Calendar.getInstance()
                cal.time = it
                cal.get(Calendar.DAY_OF_MONTH)
            } ?: calendar.get(Calendar.DAY_OF_MONTH)
            
            val dialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }.time
                    // Validar que la fecha de fin sea mayor o igual a la de inicio
                    if (startDate == null || selectedDate >= startDate) {
                        onEndDateSelected(selectedDate)
                    } else {
                        // Si la fecha es inválida, mostrar error y no aplicar
                        onEndDateSelected(null)
                    }
                },
                endYear,
                endMonth,
                endDay
            )
            
            // Limitar fecha mínima a la fecha de inicio si existe
            startDate?.let {
                dialog.datePicker.minDate = it.time
            }
            
            // Manejar cancelación
            dialog.setOnCancelListener {
                onDismiss()
            }
            
            dialog.show()
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
            .height(160.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp),
                tint = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2, // Máximo 2 líneas
                overflow = TextOverflow.Ellipsis,
                minLines = 2, // Fuerza a usar 2 líneas
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight
            )
        }
    }
}

@Composable
fun WorkerFunctionCardGridStyle(function: WorkerFunction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { function.onClick() }
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp), // Similar a MainActivity
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = function.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp), // Menos padding, similar a MainActivity
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = function.titleColor,
                maxLines = 2, // Máximo 2 líneas
                overflow = TextOverflow.Ellipsis, // Solo trunca si supera 2 líneas
                minLines = 2, // Fuerza a usar 2 líneas
                lineHeight = MaterialTheme.typography.titleSmall.lineHeight
            )
        }
    }
}

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

    // Funciones principales
    val allMainFunctions = listOf(
        WorkerFunction(
            title = "Gestión de Pedidos",
            icon = Icons.Filled.ShoppingCart,
            iconColor = FutronoBlanco,
            backgroundColor = Color(0xFFA16948),
            titleColor = FutronoBlanco,
            onClick = onOrdersClick
        ),
        WorkerFunction(
            title = "Gestión de Inventario",
            icon = Icons.Filled.Inventory,
            iconColor = FutronoBlanco,
            backgroundColor = Color(0xFF494B49),
            titleColor = FutronoBlanco,
            onClick = onProductsClick
        ),
        WorkerFunction(
            title = "Gestión de Clientes",
            icon = Icons.Filled.Groups,
            iconColor = FutronoBlanco,
            backgroundColor = Color(0xFF3B4260),
            titleColor = FutronoBlanco,
            onClick = onCustomersClick
        ),
        WorkerFunction(
            title = "Ayuda",
            icon = Icons.Filled.HelpOutline,
            iconColor = FutronoBlanco,
            backgroundColor = Color(0xFF787E5A),
            titleColor = FutronoBlanco,
            onClick = onHelpClick
        ),
    )
    // Filtrar funciones según el rol: trabajador solo ve Gestión de Pedidos e Inventario
    val mainFunctions = if (isAdmin) {
        allMainFunctions // Admin ve todas las funciones
    } else {
        allMainFunctions.filter { function ->
            function.title == "Gestión de Pedidos" || function.title == "Gestión de Inventario"
        }
    }

    // Funciones adicionales con colores menos saturados y accesibles - filtradas según el rol
    val allAdditionalFunctions = listOf(
        WorkerFunction(
            title = "Cerrar Sesión",
            icon = Icons.Filled.Logout,
            iconColor = FutronoBlanco,
            backgroundColor = Color(0xFF5D2F2F), // Marrón oscuro suave (similar a Carnes, menos saturado que el error)
            titleColor = FutronoBlanco,
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
    val dateRange by analyticsViewModel.dateRange.collectAsStateWithLifecycle()
    
    // Estados para el selector de fechas
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")) }
    
    // Sincronizar fechas con el ViewModel
    val currentStartDate = dateRange?.startDate
    val currentEndDate = dateRange?.endDate
    
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
                    "Solicitudes de compra",
                    if (isLoadingStats) "..." else dailyStatsData.totalOrders.toString(), 
                    Icons.Filled.ShoppingCart, 
                    FutronoNaranja
                ),
                Stat(
                    "Solicitudes Completadas",
                    if (isLoadingStats) "..." else dailyStatsData.completedOrders.toString(), 
                    Icons.Filled.CheckCircle, 
                    FutronoVerde
                ),
                Stat(
                    "Solicitudes Pendientes",
                    if (isLoadingStats) "..." else dailyStatsData.pendingOrders.toString(), 
                    Icons.Filled.Schedule, 
                    FutronoAmarillo
                ),
                Stat(
                    "Ventas en total",
                    if (isLoadingStats) "..." else currencyFormatter.format(dailyStatsData.totalRevenue), 
                    Icons.Filled.AttachMoney, 
                    FutronoMorado
                )
            )
        } else {
            emptyList() // Trabajadores no ven estadísticas
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                // Encabezado del drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(FutronoBlanco)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo2),
                        contentDescription = "Logo Futrono",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(80.dp)
                            .widthIn(max = 200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón de Perfil
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Perfil",
                            tint = FutronoCafe
                        )
                    },
                    label = {
                        Text(
                            "Perfil",
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.Underline
                        )
                    },
                    selected = false,
                    onClick = {
                        onUserProfileClick()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = FutronoCafe.copy(alpha = 0.12f),
                        unselectedContainerColor = Color.Transparent
                    )
                )

                // Botón de Accesibilidad
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.AccessibilityNew,
                            contentDescription = "Accesibilidad",
                            tint = FutronoCafe
                        )
                    },
                    label = {
                        Text(
                            "Accesibilidad",
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.Underline
                        )
                    },
                    selected = false,
                    onClick = {
                        onAccessibilityClick()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = FutronoCafe.copy(alpha = 0.12f),
                        unselectedContainerColor = Color.Transparent
                    )
                )

                // Botón de Notificaciones
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Notificaciones",
                            tint = FutronoCafe
                        )
                    },
                    label = { 
                        Text(
                            "Notificaciones",
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.Underline
                        ) 
                    },
                    selected = false,
                    onClick = {
                        onNotificationsClick()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = FutronoCafe.copy(alpha = 0.12f),
                        unselectedContainerColor = Color.Transparent
                    )
                )

                // Botón de Cerrar Sesión
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = FutronoError
                        )
                    },
                    label = { 
                        Text(
                            "Cerrar Sesión",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FutronoError,
                            textDecoration = TextDecoration.Underline
                        ) 
                    },
                    selected = false,
                    onClick = {
                        onLogout()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = FutronoCafe.copy(alpha = 0.12f),
                        unselectedContainerColor = Color.Transparent
                    )
                )
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header blanco con logo y botón hamburguesa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(80.dp)
                    .background(FutronoBlanco)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_logo2),
                    contentDescription = "Logo Futrono",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(70.dp)
                        .widthIn(max = 165.dp)
                        .padding(start = 1.dp)
                )

                // Botón de hamburguesa
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Opciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = FutronoCafe
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0xFF424242), // Gris oscuro
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menú",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

        // Bienvenida
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(FutronoVerde),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "¡Hola, ${currentUser?.nombre ?: if (isAdmin) "Administrador" else "Trabajador"}!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = if (isAdmin) "Bienvenido al panel de administración" else "Bienvenido al panel de trabajo",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f))
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
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(
                                alpha = 0.8f
                            )
                        )
                    )
                }
            }
        }
        
        // Resumen de Estadísticas - Solo visible para administradores
        if (isAdmin) {
            //Componente de título y tarjeta
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Resumen de Rendimiento por Fecha",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Botón para filtrar por fechas
                Row(
                    modifier = Modifier
                        .clickable { showStartDatePicker = true }
                        .background(
                            if (currentStartDate != null || currentEndDate != null) FutronoCafe.copy(alpha = 0.1f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Filtrar por fechas",
                        tint = if (currentStartDate != null || currentEndDate != null) FutronoCafe else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Seleccionar fecha",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (currentStartDate != null || currentEndDate != null) FutronoCafe else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Selector de rango de fechas
            DateRangeSelector(
                startDate = currentStartDate,
                endDate = currentEndDate,
                onStartDateSelected = { date ->
                    showStartDatePicker = false
                    if (date != null) {
                        // Si hay fecha de fin y la nueva fecha de inicio es mayor, limpiar fecha de fin
                        val finalEndDate = if (currentEndDate != null && date > currentEndDate) null else currentEndDate
                        analyticsViewModel.setDateRange(date, finalEndDate)
                        if (finalEndDate == null) {
                            showEndDatePicker = true
                        }
                    }
                },
                onEndDateSelected = { date ->
                    showEndDatePicker = false
                    analyticsViewModel.setDateRange(currentStartDate, date)
                },
                onClearDates = {
                    analyticsViewModel.setDateRange(null, null)
                },
                showStartPicker = showStartDatePicker,
                showEndPicker = showEndDatePicker,
                onDismiss = {
                    showStartDatePicker = false
                    showEndDatePicker = false
                }
            )
            
            // Mostrar fechas seleccionadas si hay filtro activo
            if (currentStartDate != null || currentEndDate != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtro: ${currentStartDate?.let { dateFormatter.format(it) } ?: "Inicio"} - ${currentEndDate?.let { dateFormatter.format(it) } ?: "Fin"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FutronoCafe
                    )
                    TextButton(onClick = {
                        analyticsViewModel.setDateRange(null, null)
                    }) {
                        Text("Limpiar", color = FutronoError)
                    }
                }
            }

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
        Text(
            "Funciones Principales", 
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
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
        Text(
            "Herramientas Adicionales", 
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
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
}
}
