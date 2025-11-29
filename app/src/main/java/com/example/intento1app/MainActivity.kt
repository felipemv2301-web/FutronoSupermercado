package com.example.intento1app

import android.os.Bundle
import android.util.Patterns
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.example.intento1app.utils.Validators
import com.example.intento1app.utils.TipoError
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intento1app.ui.theme.AccessibleFutronoTheme
import com.example.intento1app.ui.theme.FutronoCafe
import com.example.intento1app.ui.theme.FutronoNaranja
import com.example.intento1app.ui.theme.FutronoFondo
import com.example.intento1app.ui.theme.FutronoCafeOscuro
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextDecoration
import com.example.intento1app.ui.screens.PaymentScreen
import com.example.intento1app.ui.screens.AccessibilityScreen
import com.example.intento1app.ui.screens.UserProfileScreen
import com.example.intento1app.ui.screens.MyOrdersScreen
import com.example.intento1app.ui.screens.SolicitudSoporte
import com.example.intento1app.ui.screens.MyDataScreen
import com.example.intento1app.ui.screens.MyBankDetailsScreen
import com.example.intento1app.ui.screens.SolicitudSoporte
import com.example.intento1app.ui.screens.WorkerOrdersScreen
import com.example.intento1app.ui.screens.WorkerHomeScreen
import com.example.intento1app.ui.screens.InventoryScreen
import com.example.intento1app.ui.screens.WorkerCustomersScreen
import com.example.intento1app.ui.screens.WorkerNotificationsScreen
import com.example.intento1app.data.models.User
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import coil.request.ImageRequest
import com.example.intento1app.ui.components.ScalableHeadlineSmall
import com.example.intento1app.viewmodel.AccessibilityViewModel
import com.example.intento1app.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.intento1app.data.models.CartItem
import com.example.intento1app.ui.components.ScalableHeadlineLarge
import com.example.intento1app.ui.components.ScalableTitleSmall
import com.example.intento1app.ui.screens.AccessibilityScreen
import com.example.intento1app.ui.screens.AddProductScreen
import com.example.intento1app.ui.screens.SolicitudSoporte
import com.example.intento1app.ui.screens.WorkerProductsScreen
import com.example.intento1app.ui.screens.EditProductScreen
import com.example.intento1app.ui.screens.HelpAndContactScreen
import com.example.intento1app.ui.screens.PaymentScreen
import com.example.intento1app.ui.screens.SolicitudSoporte
import com.example.intento1app.ui.screens.TerminoAndCondiciones
import com.example.intento1app.ui.theme.FutronoBlanco
import com.example.intento1app.ui.theme.FutronoError
import com.example.intento1app.ui.theme.FutronoSuccess
import com.example.intento1app.ui.theme.FutronoVerde
import com.example.intento1app.ui.theme.StockHigh
import com.example.intento1app.ui.theme.StockLow
import com.example.intento1app.ui.theme.StockMedium
import com.example.intento1app.utils.CartPersistenceManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar la barra de estado para que sea blanca y el contenido no se dibuje debajo
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true // Iconos oscuros en barra blanca
        
        // Configurar color de fondo de la barra de estado (blanco)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        try {
            FirebaseApp.initializeApp(this)
            println("Firebase inicializado correctamente")
        } catch (e: Exception) {
            println("Error al inicializar Firebase: ${e.message}")
        }

        setContent {
            val accessibilityViewModel: AccessibilityViewModel = viewModel()

            AccessibleFutronoTheme(accessibilityViewModel = accessibilityViewModel) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FutronoApp(accessibilityViewModel = accessibilityViewModel)
                }
            }
        }
    }
    
}

@Composable
fun FutronoApp(accessibilityViewModel: AccessibilityViewModel) {
    val authViewModel: AuthViewModel = remember { AuthViewModel() }
    var currentScreen by remember { mutableStateOf("loading") } // Cambiar a loading inicialmente
    
    // Declarar variables necesarias para el snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val dismissScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    var currentSnackbarDismissJob by remember { mutableStateOf<Job?>(null) }
    
    // Cerrar snackbar inmediatamente cuando se cambia de pantalla (ningún mensaje se mantiene)
    LaunchedEffect(currentScreen) {
        // Cancelar cualquier Job de cierre pendiente
        currentSnackbarDismissJob?.cancel()
        currentSnackbarDismissJob = null
        // Cerrar cualquier snackbar activo inmediatamente al cambiar de pantalla
        snackbarHostState.currentSnackbarData?.dismiss()
        // Pequeño delay y verificar nuevamente para asegurar que se cierre
        delay(50)
        snackbarHostState.currentSnackbarData?.dismiss()
    }
    
    // Función helper para mostrar snackbar con duración personalizada (2 segundos exactos)
    fun showShortSnackbar(message: String, durationMs: Long = 2000) {
        // Cancelar cualquier Job de cierre anterior
        currentSnackbarDismissJob?.cancel()
        currentSnackbarDismissJob = null
        
        // Cerrar cualquier snackbar anterior inmediatamente
        snackbarHostState.currentSnackbarData?.dismiss()
        
        // Mostrar el snackbar en una corrutina separada (no bloqueante)
        scope.launch(Dispatchers.Main) {
            try {
                delay(100) // Delay para asegurar que se cierre completamente el anterior
                
                // Mostrar el snackbar con duración indefinida para controlarlo manualmente
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Indefinite
                )
            } catch (e: Exception) {
                // Ignorar errores al mostrar
            }
        }
        
        // Crear un Job separado que SIEMPRE cierre el snackbar después del tiempo especificado
        // Usar un scope independiente que no se cancela para asegurar que siempre se ejecute
        val dismissJob = dismissScope.launch {
            try {
                // Esperar el tiempo necesario para que se muestre el snackbar + el tiempo de duración
                delay(100 + durationMs)
                
                // Cerrar el snackbar después del tiempo especificado
                snackbarHostState.currentSnackbarData?.dismiss()
                
                // Doble verificación para asegurar el cierre
                delay(50)
                snackbarHostState.currentSnackbarData?.dismiss()
                
                // Limpiar la referencia al Job
                currentSnackbarDismissJob = null
            } catch (e: Exception) {
                // En caso de error, asegurar que se cierre el snackbar
                snackbarHostState.currentSnackbarData?.dismiss()
                currentSnackbarDismissJob = null
            }
        }
        
        // Guardar la referencia al Job para poder cancelarlo si es necesario
        currentSnackbarDismissJob = dismissJob
    }
    var isCheckingAuth by remember { mutableStateOf(true) } // Estado para verificar autenticación

    // Observar el estado de autenticación
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentFirebaseUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    
    // Mapa para guardar el stock original de productos antes de añadirlos al carrito
    // Key: productId, Value: stock original
    var originalStockMap by remember { mutableStateOf(mapOf<String, Int>()) }
    
    // Servicio para actualizar stock en Firebase
    val productService = remember { com.example.intento1app.data.services.ProductFirebaseService() }

    // Variables de estado para las pantallas del perfil
    var showUserProfile by remember { mutableStateOf(false) }
    var showMyOrders by remember { mutableStateOf(false) }
    var showMyData by remember { mutableStateOf(false) }
    var showMyBankDetails by remember { mutableStateOf(false) }
    var showHelpAndContact by remember { mutableStateOf(false) }
    var showTerminosCondiciones by remember { mutableStateOf(false) }
    var showWorkerOrders by remember { mutableStateOf(false) }
    var showInventory by remember { mutableStateOf(false) }
    var showWorkerCustomers by remember { mutableStateOf(false) }
    var showWorkerProducts by remember { mutableStateOf(false) }
    var showAddProduct by remember { mutableStateOf(false) }
    var showEditProduct by remember { mutableStateOf(false) }
    var productToEditId by remember { mutableStateOf<String?>(null) }
    var showWorkerNotifications by remember { mutableStateOf(false) }
    var showWorkerReports by remember { mutableStateOf(false) }
    var showWorkerSchedule by remember { mutableStateOf(false) }
    var showWorkerTeam by remember { mutableStateOf(false) }
    var showWorkerSettings by remember { mutableStateOf(false) }
    var showWorkerHelp by remember { mutableStateOf(false) }
    var showWorkerDevolutionDinero by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    // Sistema de navegación con pila
    var navigationStack by remember { mutableStateOf(listOf<String>()) }

    // Funciones para manejar la navegación con pila
    fun navigateTo(screen: String) {
        navigationStack = navigationStack + screen
    }

    fun navigateBack() {
        if (navigationStack.isNotEmpty()) {
            navigationStack = navigationStack.dropLast(1)
        }
    }

    fun getCurrentScreenFromStack(): String {
        return if (navigationStack.isNotEmpty()) {
            navigationStack.last()
        } else {
            "home"
        }
    }

    // Función para manejar la navegación hacia atrás
    fun handleBackNavigation() {
        navigateBack()
        val previousScreen = getCurrentScreenFromStack()

        // Limpiar todas las pantallas de perfil
        showUserProfile = false
        showMyData = false
        showMyOrders = false
        showMyBankDetails = false
        showHelpAndContact = false
        showTerminosCondiciones = false
        showWorkerOrders = false
        showInventory = false
        showWorkerCustomers = false
        showWorkerProducts = false
        showWorkerNotifications = false
        showWorkerReports = false
        showWorkerSchedule = false
        showWorkerTeam = false
        showWorkerSettings = false
        showWorkerHelp = false
        showWorkerDevolutionDinero = false

        // Mostrar la pantalla anterior basándose en la pila
        when (previousScreen) {
            "userProfile" -> showUserProfile = true
            "myData" -> showMyData = true
            "myOrders" -> showMyOrders = true
            "myBankDetails" -> showMyBankDetails = true
            "helpAndContact" -> showHelpAndContact = true
            "terminosCondiciones" -> showTerminosCondiciones = true
            "workerOrders" -> showWorkerOrders = true
            "inventory" -> showInventory = true
            "workerCustomers" -> showWorkerCustomers = true
            "workerProducts" -> showWorkerProducts = true
            "workerNotifications" -> showWorkerNotifications = true
            "workerReports" -> showWorkerReports = true
            "workerSchedule" -> showWorkerSchedule = true
            "workerTeam" -> showWorkerTeam = true
            "workerSettings" -> showWorkerSettings = true
            "workerHelp" -> showWorkerHelp = true
            "onMyDevolutionDinero" -> showWorkerDevolutionDinero = true
            else -> {
                // Si no hay pantalla anterior, volver a home
                currentScreen = "home"
            }
        }
    }

    // Funciones helper para gestionar el stock
    /**
     * Descuenta stock de un producto cuando se añade al carrito
     */
    fun decreaseProductStock(productId: String, quantity: Int, currentStock: Int) {
        // Guardar stock original si es la primera vez que se añade este producto
        if (!originalStockMap.containsKey(productId)) {
            originalStockMap = originalStockMap + (productId to currentStock)
        }
        
        val newStock = (currentStock - quantity).coerceAtLeast(0)
        scope.launch {
            productService.updateProductStock(productId, newStock).onFailure { error ->
                android.util.Log.e("StockManagement", "Error al descontar stock: ${error.message}")
            }
        }
    }
    
    /**
     * Restaura stock de un producto cuando se quita del carrito
     */
    fun restoreProductStock(productId: String, quantity: Int) {
        val originalStock = originalStockMap[productId] ?: return
        
        // Obtener stock actual y restaurar
        scope.launch {
            val product = productService.getProductById(productId)
            if (product != null) {
                val newStock = (product.stock + quantity).coerceAtMost(originalStock)
                productService.updateProductStock(productId, newStock).onFailure { error ->
                    android.util.Log.e("StockManagement", "Error al restaurar stock: ${error.message}")
                }
            }
        }
    }
    
    /**
     * Restaura todo el stock de todos los productos en el carrito
     */
    fun restoreAllStock() {
        scope.launch {
            originalStockMap.forEach { (productId, originalStock) ->
                productService.updateProductStock(productId, originalStock).onFailure { error ->
                    android.util.Log.e("StockManagement", "Error al restaurar stock de $productId: ${error.message}")
                }
            }
            originalStockMap = emptyMap()
        }
    }
    
    /**
     * Ajusta el stock cuando cambia la cantidad de un producto en el carrito
     */
    fun adjustProductStock(productId: String, oldQuantity: Int, newQuantity: Int, currentStock: Int) {
        val difference = newQuantity - oldQuantity
        if (difference != 0) {
            val newStock = (currentStock - difference).coerceAtLeast(0)
            scope.launch {
                productService.updateProductStock(productId, newStock).onFailure { error ->
                    android.util.Log.e("StockManagement", "Error al ajustar stock: ${error.message}")
                }
            }
        }
    }

    // Estados de autenticación (ya declarados arriba)
    val authErrorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()

    // Estados de roles
    val isClient by authViewModel.isClient.collectAsStateWithLifecycle()
    val isWorker by authViewModel.isWorker.collectAsStateWithLifecycle()
    val isAdmin by authViewModel.isAdmin.collectAsStateWithLifecycle()
    val userRoles by authViewModel.userRoles.collectAsStateWithLifecycle()

    // Obtener el contexto una vez (fuera del LaunchedEffect)
    val context = LocalContext.current
    
    // Cargar el carrito guardado cuando se inicia la app y el usuario está en home
    LaunchedEffect(currentScreen, isLoggedIn) {
        if (currentScreen == "home" && isLoggedIn && cartItems.isEmpty()) {
            val savedCart = CartPersistenceManager.loadCart(context)
            if (savedCart.isNotEmpty()) {
                cartItems = savedCart
                android.util.Log.d("MainActivity", "Carrito cargado desde almacenamiento: ${savedCart.size} items")
            }
        }
    }
    
    // Guardar el carrito automáticamente cada vez que cambia
    LaunchedEffect(cartItems) {
        if (isLoggedIn && currentScreen != "payment") {
            // Solo guardar si no estamos en la pantalla de pago
            CartPersistenceManager.saveCart(context, cartItems)
            android.util.Log.d("MainActivity", "Carrito guardado: ${cartItems.size} items")
        }
    }
    
    // Verificar si se debe limpiar el carrito (cuando el pago se completa o falla)
    LaunchedEffect(currentScreen) {
        if (currentScreen == "home") {
            val prefs = context.getSharedPreferences("payment_prefs", android.content.Context.MODE_PRIVATE)
            val shouldClearCart = prefs.getBoolean("should_clear_cart", false)
            if (shouldClearCart) {
                // Limpiar carrito y bandera
                cartItems = emptyList()
                originalStockMap = emptyMap()
                CartPersistenceManager.clearCart(context) // Limpiar también del almacenamiento persistente
                prefs.edit().remove("should_clear_cart").apply()
                android.util.Log.d("MainActivity", "Carrito limpiado después de procesamiento de pago")
            }
        }
    }

    // Verificar estado de autenticación al inicializar
    LaunchedEffect(isLoggedIn, currentFirebaseUser, userRoles) {
        println("MainActivity: LaunchedEffect ejecutado - isLoggedIn: $isLoggedIn, currentScreen: $currentScreen, isCheckingAuth: $isCheckingAuth")

        val firebaseUser = currentFirebaseUser
        if (isLoggedIn && firebaseUser != null) {
            // Verificar roles del usuario
            val hasValidRole = isClient || isWorker || isAdmin

            println("MainActivity: Verificando roles - hasValidRole: $hasValidRole, isClient: $isClient, isWorker: $isWorker, isAdmin: $isAdmin")

            if (hasValidRole) {
                // Usuario ya logueado con rol válido, crear currentUser y ir a home
                val localUser = User(
                    id = firebaseUser.id,
                    nombre = firebaseUser.displayName.split(" ").getOrNull(0) ?: "Usuario",
                    apellido = firebaseUser.displayName.split(" ").getOrNull(1) ?: "Ejemplo",
                    rut = firebaseUser.rut.ifEmpty { "No registrado" }, // Obtener RUT de Firestore
                    telefono = firebaseUser.phoneNumber,
                    email = firebaseUser.email,
                    direccion = firebaseUser.address // Obtener dirección de Firestore
                )
                currentUser = localUser
                currentScreen = "home"
                isCheckingAuth = false

                // Log de roles para debugging
                println("MainActivity: Usuario logueado con roles válidos: ${userRoles}")
                println("MainActivity: Navegando a home - Cliente: $isClient, Trabajador: $isWorker, Admin: $isAdmin")
            } else {
                // Usuario logueado pero sin roles válidos - redirigir a auth
                println("MainActivity: Usuario logueado pero sin roles válidos: ${userRoles}")
                currentUser = null
                currentScreen = "auth"
                isCheckingAuth = false
            }
        } else if (!isLoggedIn && !isCheckingAuth) {
            // No hay usuario logueado, ir a auth
            println("MainActivity: No hay usuario logueado, navegando a auth")
            currentScreen = "auth"
        }
    }

    // Timeout para la verificación de autenticación
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000) // 3 segundos de timeout
        if (isCheckingAuth) {
            println("MainActivity: ⏰ Timeout de verificación de autenticación")
            isCheckingAuth = false
            if (!isLoggedIn) {
                currentScreen = "auth"
            }
        }
    }

    // Sincronizar currentUser cuando currentFirebaseUser cambie
    LaunchedEffect(currentFirebaseUser, userRoles) {
        val firebaseUser = currentFirebaseUser
        if (firebaseUser != null && currentUser?.id != firebaseUser.id) {
            // Verificar roles antes de sincronizar
            val hasValidRole = isClient || isWorker || isAdmin

            if (hasValidRole) {
                val localUser = User(
                    id = firebaseUser.id,
                    nombre = firebaseUser.displayName.split(" ").getOrNull(0) ?: "Usuario",
                    apellido = firebaseUser.displayName.split(" ").getOrNull(1) ?: "Ejemplo",
                    rut = firebaseUser.rut.ifEmpty { "No registrado" }, // Obtener RUT de Firestore
                    telefono = firebaseUser.phoneNumber,
                    email = firebaseUser.email,
                    direccion = firebaseUser.address // Obtener dirección de Firestore
                )
                currentUser = localUser
                println("MainActivity: Usuario sincronizado con roles válidos: ${userRoles}")
            } else {
                println("MainActivity: Usuario sin roles válidos, no se sincroniza: ${userRoles}")
                currentUser = null
            }
        }
    }

    // Manejar el botón de atrás del dispositivo en todas las pantallas
    // Siempre habilitado excepto cuando estamos cargando
    BackHandler(enabled = currentScreen != "loading" && !isCheckingAuth) {
        // Primero verificar si hay alguna pantalla de perfil activa
        val hasProfileScreenActive = showUserProfile || showMyData || showMyOrders || showMyBankDetails || 
            showHelpAndContact || showTerminosCondiciones || showWorkerOrders || showInventory || 
            showWorkerCustomers || showWorkerProducts || showAddProduct || 
            showEditProduct || showWorkerNotifications || showWorkerReports || 
            showWorkerSchedule || showWorkerTeam || showWorkerSettings || 
            showWorkerHelp || showWorkerDevolutionDinero
        
        android.util.Log.d("BackHandler", "Back button pressed - currentScreen: $currentScreen, hasProfileScreenActive: $hasProfileScreenActive")
        
        // Usar remember para capturar el estado actual
        val currentScreenValue = currentScreen
        
        when {
            // Pantallas de perfil - usar handleBackNavigation
            hasProfileScreenActive -> {
                android.util.Log.d("BackHandler", "Navigating back from profile screen")
                handleBackNavigation()
            }
            // Pantallas principales
            currentScreenValue == "payment" -> {
                android.util.Log.d("BackHandler", "Navigating from payment to cart")
                currentScreen = "cart"
            }
            currentScreenValue == "cart" -> {
                android.util.Log.d("BackHandler", "Navigating from cart to home")
                currentScreen = "home"
            }
            currentScreenValue == "register" -> {
                android.util.Log.d("BackHandler", "Navigating from register to auth")
                currentScreen = "auth"
            }
            currentScreenValue == "accessibility" -> {
                android.util.Log.d("BackHandler", "Navigating from accessibility to home")
                currentScreen = "home"
            }
            currentScreenValue == "home" -> {
                android.util.Log.d("BackHandler", "On home screen - consuming back event")
                // Si estamos en home, no hacer nada (el BackHandler consume el evento)
                // Esto previene que la app se cierre
            }
            currentScreenValue == "auth" -> {
                android.util.Log.d("BackHandler", "On auth screen - consuming back event")
                // Si estamos en auth, no hacer nada (el BackHandler consume el evento)
                // Esto previene que la app se cierre
            }
            else -> {
                android.util.Log.d("BackHandler", "Unknown screen: $currentScreenValue - falling back to home")
                // Si no hay ningún caso específico, intentar volver a home
                // Esto es un fallback para cualquier pantalla no contemplada
                if (currentScreenValue != "home" && currentScreenValue != "auth") {
                    currentScreen = "home"
                }
            }
        }
    }

    when {
        currentScreen == "loading" || isCheckingAuth -> {
            // Pantalla de carga mientras se verifica la autenticación
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FutronoFondo),
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
                        text = "Cargando...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = FutronoCafeOscuro
                    )
                }
            }
        }
        showHelpAndContact -> {
            // Pantalla de ayuda y contacto
            HelpAndContactScreen(
                onBackClick = {
                    handleBackNavigation()
                },
                onTermsAndConditionsClick = {
                    navigateTo("terminosCondiciones")
                    showHelpAndContact = false
                    showTerminosCondiciones = true
                },
                onClaimClick = {
                    navigateTo("myDevolution")
                    showHelpAndContact = false
                    showWorkerDevolutionDinero = true
                }
            )
        }
        showTerminosCondiciones -> {
            // Pantalla de términos y condiciones
            TerminoAndCondiciones(
                onBackClick = {
                    handleBackNavigation()
                },
                Onclick = {
                    // Callback opcional si se necesita alguna acción
                }
            )
        }
        showWorkerOrders -> {
            // Pantalla de gestión de pedidos para trabajadores
            WorkerOrdersScreen(
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showInventory -> {
            // Pantalla de inventario para trabajadores
            InventoryScreen(
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showWorkerCustomers -> {
            // Pantalla de gestión de clientes para trabajadores
            WorkerCustomersScreen(
                onNavigateBack = {
                    handleBackNavigation()
                }
            )
        }
        showWorkerProducts -> {
            // Pantalla de gestión de productos para trabajadores
            WorkerProductsScreen(
                onNavigateBack = {
                    handleBackNavigation()
                },
                onAddProductClick = {
                    navigateTo("addProduct") // Usando tu función de navegación
                    showWorkerProducts = false
                    showAddProduct = true
                },
                onEditProductClick = { productId ->
                    navigateTo("editProduct")
                    productToEditId = productId // Guarda el ID del producto a editar
                    showWorkerProducts = false
                    showEditProduct = true
                }
            )
        }
        //Añadir producto
        showAddProduct -> {
            AddProductScreen(
                onBackClick = {
                    // Al volver, apagas la pantalla de añadir producto
                    showAddProduct = false
                    // y vuelves a encender la pantalla de gestión
                    showWorkerProducts = true
                },
                onProductAdded = {
                    // Después de agregar, haces lo mismo: volver a la gestión
                    showAddProduct = false
                    showWorkerProducts = true
                }
            )
        }
        //Editar producto
        showEditProduct && productToEditId != null -> {
            EditProductScreen(
                productId = productToEditId!!,
                onNavigateBack = {
                    // Al volver, apagas la pantalla de editar producto
                    showEditProduct = false
                    productToEditId = null
                    // y vuelves a encender la pantalla de gestión
                    showWorkerProducts = true
                }
            )
        }
        showWorkerNotifications -> {
            // Pantalla de notificaciones para trabajadores
            WorkerNotificationsScreen(
                onNavigateBack = {
                    handleBackNavigation()
                }
            )
        }
        showWorkerReports -> {
            // Pantalla de reportes para trabajadores (placeholder)
            InventoryScreen(
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showWorkerSchedule -> {
            // Pantalla de horarios para trabajadores (placeholder)
            InventoryScreen(
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showWorkerTeam -> {
            // Pantalla de equipo para trabajadores (placeholder)
            InventoryScreen(
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showWorkerSettings -> {
            // Pantalla de configuración para trabajadores (placeholder)
            InventoryScreen(
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showWorkerHelp -> {
            // Pantalla de ayuda para trabajadores (placeholder)
            InventoryScreen(
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showMyBankDetails -> {
            // Pantalla de mis datos bancarios
            MyBankDetailsScreen(
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showWorkerDevolutionDinero -> {
            SolicitudSoporte(
                currentUser = currentUser,
                onBackClick = {
                    handleBackNavigation()
                },
                onSuccess = {
                    // Opcional: mostrar mensaje de éxito o actualizar UI
                }
            )
        }
        showMyData && currentUser != null -> {
            // Pantalla de mis datos
            val user = currentUser!!
            // Crear FirebaseUser para compatibilidad con MyDataScreen
            val firebaseUser = if (user.id != "guest") {
                // Usuario autenticado
                com.example.intento1app.data.models.FirebaseUser(
                    id = user.id,
                    email = user.email,
                    displayName = "${user.nombre} ${user.apellido}",
                    photoUrl = "",
                    phoneNumber = user.telefono,
                    rut = user.rut,
                    address = user.direccion,
                    isEmailVerified = true,
                    isActive = true
                )
            } else {
                // Usuario invitado
                com.example.intento1app.data.models.FirebaseUser(
                    id = "guest",
                    email = "invitado@futrono.com",
                    displayName = "Usuario Invitado",
                    photoUrl = "",
                    phoneNumber = "",
                    rut = "",
                    address = "",
                    isEmailVerified = false,
                    isActive = true
                )
            }
            MyDataScreen(
                currentUser = user,
                firebaseUser = firebaseUser,
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showMyOrders && currentUser != null -> {
            // Pantalla de mis pedidos
            val user = currentUser!!
            MyOrdersScreen(
                currentUser = user,
                onBackClick = {
                    handleBackNavigation()
                }
            )
        }
        showUserProfile && currentUser != null -> {
            // Pantalla de perfil de usuario (funciona para usuarios autenticados e invitados)
            val user = currentUser!!
            UserProfileScreen(
                currentUser = user,
                accessibilityViewModel = accessibilityViewModel,
                onBackClick = {
                    handleBackNavigation()
                },
                onLogout = {
                    showUserProfile = false
                    currentUser = null
                    authViewModel.signOut()
                    currentScreen = "auth"
                },
                onAccessibilityClick = {
                    showUserProfile = false
                    currentScreen = "accessibility"
                },
                onMyDataClick = {
                    navigateTo("myData")
                    showUserProfile = false
                    showMyData = true
                },
                onPaymentMethodsClick = {
                    // TODO: Implementar pantalla de medios de pago
                    println("Medios de pago - No implementado aún")
                },
                onMyBankDetailsClick = {
                    navigateTo("myBankDetails")
                    showUserProfile = false
                    showMyBankDetails = true
                },
                onDeleteAccountClick = { password ->
                    scope.launch {
                        try {
                            val firebaseService = com.example.intento1app.data.services.FirebaseService()
                            val result = firebaseService.deleteUserAccount(password)
                            
                            result.fold(
                                onSuccess = {
                                    // Cuenta eliminada exitosamente
                                    showUserProfile = false
                                    currentUser = null
                                    cartItems = emptyList()
                                    authViewModel.signOut()
                                    currentScreen = "auth"
                                    
                                    // Mostrar mensaje de éxito
                                    showShortSnackbar(
                                        message = "Cuenta eliminada exitosamente",
                                        durationMs = 3000
                                    )
                                },
                                onFailure = { error ->
                                    // Error al eliminar cuenta
                                    val errorMessage = when {
                                        error.message?.contains("Contraseña incorrecta") == true -> 
                                            "Contraseña incorrecta. Por favor, verifica tu contraseña."
                                        error.message?.contains("recent authentication") == true -> 
                                            "Se requiere autenticación reciente. Por favor, intenta nuevamente."
                                        else -> 
                                            "Error al eliminar cuenta: ${error.message ?: "Error desconocido"}"
                                    }
                                    showShortSnackbar(
                                        message = errorMessage,
                                        durationMs = 4000
                                    )
                                }
                            )
                        } catch (e: Exception) {
                            showShortSnackbar(
                                message = "Error inesperado: ${e.message ?: "Error desconocido"}",
                                durationMs = 4000
                            )
                        }
                    }
                },
                onMyOrdersClick = {
                    navigateTo("myOrders")
                    showUserProfile = false
                    showMyOrders = true
                },
                onHelpContactClick = {
                    navigateTo("helpAndContact")
                    showUserProfile = false
                    showHelpAndContact = true
                },
                onDevolutionClick = {
                    navigateTo("myDevolution")
                    showUserProfile = false
                    showWorkerDevolutionDinero = true
                }
            )
        }
        currentScreen == "auth" -> AuthScreen(
            authViewModel = authViewModel,
            onLoginSuccess = { user ->
                currentUser = user
                currentScreen = "home"
            },
            onRegisterClick = {
                currentScreen = "register"
            },
            onGuestLogin = {
                currentUser = User("guest", "Invitado", "Invitado", "", "", "", "")
                currentScreen = "home"
            }
        )
        currentScreen == "register" -> RegisterScreen(
            authViewModel = authViewModel,
            onBackToLogin = {
                currentScreen = "auth"
            },
            onRegisterSuccess = { user ->
                currentUser = user
                currentScreen = "home"
            }
        )
        currentScreen == "home" -> {
            // Mostrar pantalla diferente según el rol del usuario
            if (isWorker || isAdmin) {
                // Pantalla específica para trabajadores y administradores
                WorkerHomeScreen(
                    currentUser = currentUser,
                    isAdmin = isAdmin, // Pasar el rol de administrador
                    onLogout = {
                        currentUser = null
                        cartItems = emptyList()
                        currentScreen = "auth"
                    },
                    onOrdersClick = {
                        navigateTo("workerOrders")
                        showWorkerOrders = true
                    },
                    onInventoryClick = {
                        navigateTo("inventory")
                        showInventory = true
                    },
                    onCustomersClick = {
                        navigateTo("workerCustomers")
                        showWorkerCustomers = true
                    },
                    onProductsClick = {
                        navigateTo("workerProducts")
                        showWorkerProducts = true
                    },
                    onReportsClick = {
                        navigateTo("workerReports")
                        showWorkerReports = true
                    },
                    onNotificationsClick = {
                        navigateTo("workerNotifications")
                        showWorkerNotifications = true
                    },
                    onScheduleClick = {
                        navigateTo("workerSchedule")
                        showWorkerSchedule = true
                    },
                    onTeamClick = {
                        navigateTo("workerTeam")
                        showWorkerTeam = true
                    },
                    onSettingsClick = {
                        navigateTo("workerSettings")
                        showWorkerSettings = true
                    },
                    onHelpClick = {
                        navigateTo("workerHelp")
                        showWorkerHelp = true
                    },
                    onAccessibilityClick = {
                        currentScreen = "accessibility"
                    },
                    onUserProfileClick = {
                        navigateTo("userProfile")
                        showUserProfile = true
                    },
                    accessibilityViewModel = accessibilityViewModel
                )
            } else {
                // Pantalla normal para clientes
                FutronoHomeScreen(
                    currentUser = currentUser,
                    categories = ProductCategory.values().toList(), // Esto sí devuelve List<ProductCategory>
                    onCategoryClick = { category ->
                        selectedCategory = category
                        currentScreen = "products"
                    },
                    onCartClick = {
                        currentScreen = "cart"
                    },
                    onLogout = {
                        currentUser = null
                        cartItems = emptyList()
                        currentScreen = "auth"
                    },
                    cartItemCount = cartItems.sumOf { it.quantity },
                    onAccessibilityClick = {
                        currentScreen = "accessibility"
                    },
                    onUserProfileClick = {
                        navigateTo("userProfile")
                        showUserProfile = true
                    },
                    onMyOrdersClick = {
                        navigateTo("myOrders")
                        showMyOrders = true
                    },
                    accessibilityViewModel = accessibilityViewModel
                )
            }
        }
        currentScreen == "products" -> ProductsScreen(
            category = selectedCategory ?: "",
            onBackClick = {
                currentScreen = "home"
            },
            onAddToCart = { product ->
                // Obtener stock actualizado desde Firebase antes de añadir al carrito
                // Capturar el contexto antes de entrar a la corrutina
                val contextForToast = context
                scope.launch {
                    val currentProduct = productService.getProductById(product.id)
                    if (currentProduct != null && currentProduct.stock > 0) {
                        val existingItem = cartItems.find { it.product.id == product.id }
                        val quantityToAdd = 1
                        
                        if (existingItem != null) {
                            // Si ya existe, aumentar cantidad y descontar stock
                            cartItems = cartItems.map { item ->
                                if (item.product.id == product.id) {
                                    item.copy(quantity = item.quantity + quantityToAdd)
                                } else {
                                    item
                                }
                            }
                            decreaseProductStock(product.id, quantityToAdd, currentProduct.stock)
                        } else {
                            // Si es nuevo, añadir al carrito y descontar stock
                            cartItems = cartItems + CartItem(currentProduct, quantityToAdd)
                            decreaseProductStock(product.id, quantityToAdd, currentProduct.stock)
                        }
                    } else {
                        // Stock insuficiente o producto no encontrado
                        android.widget.Toast.makeText(
                            contextForToast,
                            "Stock insuficiente para ${product.name}",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // Aquí se mejora lo visual del simón y las funcionalidades
                showShortSnackbar("${product.name} se añadió al carrito correctamente", 2000)
            },
            onCartClick = {
                currentScreen = "cart"
            },
            cartItemCount = cartItems.sumOf { it.quantity },
            snackbarHostState = snackbarHostState
        )
        currentScreen == "cart" -> CartScreen(
            cartItems = cartItems,
            onBackClick = {
                currentScreen = "home"
            },
            onUpdateQuantity = { productId, quantity ->
                // Aquí se mejora lo visual del simón y las funcionalidades
                val existingItem = cartItems.find { it.product.id == productId }
                val productName = existingItem?.product?.name ?: ""
                val oldQuantity = existingItem?.quantity ?: 0
                
                if (quantity <= 0) {
                    // Restaurar stock cuando se elimina (cantidad llega a 0)
                    if (oldQuantity > 0) {
                        restoreProductStock(productId, oldQuantity)
                    }
                    cartItems = cartItems.filter { it.product.id != productId }
                    // Aquí se mejora lo visual del simón y las funcionalidades
                    showShortSnackbar("Se ha eliminado $productName del carrito", 2000)
                } else {
                    // Ajustar stock cuando cambia la cantidad
                    scope.launch {
                        val currentProduct = productService.getProductById(productId)
                        if (currentProduct != null) {
                            adjustProductStock(productId, oldQuantity, quantity, currentProduct.stock)
                            cartItems = cartItems.map { item ->
                                if (item.product.id == productId) {
                                    item.copy(quantity = quantity)
                                } else {
                                    item
                                }
                            }
                        }
                    }
                    // Mostrar mensaje según si aumentó o disminuyó
                    // Aquí se mejora lo visual del simón y las funcionalidades
                    val message = if (quantity > oldQuantity) {
                        "Se añadió $quantity unidad(es) de $productName"
                    } else {
                        "Se quitó $quantity unidad(es) de $productName"
                    }
                    showShortSnackbar(message, 2000)
                }
            },
            onRemoveItem = { productId ->
                // Aquí se mejora lo visual del simón y las funcionalidades
                val existingItem = cartItems.find { it.product.id == productId }
                val productName = existingItem?.product?.name ?: ""
                cartItems = cartItems.filter { it.product.id != productId }
                showShortSnackbar("Se ha eliminado el producto $productName del carrito", 1500)
            },
            onClearCart = {
                // Restaurar todo el stock antes de limpiar el carrito
                restoreAllStock()
                cartItems = emptyList()
            },
            onCheckout = {
                // Navegar a la pantalla de pago
                currentScreen = "payment"
            },
            snackbarHostState = snackbarHostState
        )
        currentScreen == "payment" -> PaymentScreen(
            cartItems = cartItems,
            currentUser = currentUser, // Pasar usuario actual
            originalStockMap = originalStockMap, // Pasar mapa de stock original
            onPaymentComplete = {
                // Pago exitoso: mantener stock descontado, limpiar carrito y mapa de stock original
                println("MainActivity: onPaymentComplete llamado - limpiando carrito y navegando a home")
                cartItems = emptyList()
                originalStockMap = emptyMap() // Limpiar el mapa ya que el stock queda descontado permanentemente
                CartPersistenceManager.clearCart(context) // Limpiar el carrito del almacenamiento persistente
                currentScreen = "home"
                println("MainActivity: Navegación completada - currentScreen = $currentScreen")
            },
            onBackToCart = {
                // Volver al carrito
                currentScreen = "cart"
            },
        )
        currentScreen == "accessibility" -> AccessibilityScreen(
            accessibilityViewModel = accessibilityViewModel,
            onBackClick = {
                currentScreen = "home"
            }
        )
    }
}

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (User) -> Unit,
    onRegisterClick: () -> Unit,
    onGuestLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordMessage by remember { mutableStateOf<String?>(null) }
    var forgotPasswordError by remember { mutableStateOf(false) }
    
    val emailFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val passwordFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding() // Ajusta el contenido cuando aparece el teclado
            .padding(17.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo de Futrono
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo de Futrono Supermercado",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón "Aprende como usar la aplicación"
        OutlinedButton(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/rCFKVNz6RnQ"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.e("AuthScreen", "Error al abrir el enlace de YouTube: ${e.message}", e)
                    // Opcional: mostrar un Toast al usuario
                    android.widget.Toast.makeText(
                        context,
                        "No se pudo abrir el enlace. Por favor, intenta nuevamente.",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FutronoVerde
            ),
            border = BorderStroke(
                width = 1.5.dp,
                color = FutronoVerde
            )
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = FutronoVerde
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "¿Cómo usar la aplicación?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = FutronoVerde
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Correo electrónico",
                    tint = FutronoCafe
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester)
                .bringIntoViewRequester(bringIntoViewRequester),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FutronoCafe,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Contraseña",
                    tint = FutronoCafe
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = FutronoCafe
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester)
                .bringIntoViewRequester(bringIntoViewRequester),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FutronoCafeOscuro,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    // Intentar iniciar sesión cuando se presiona "Done"
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        authViewModel.signInUser(
                            email = email,
                            password = password,
                            onSuccess = { firebaseUser ->
                                val localUser = User(
                                    id = firebaseUser.id,
                                    nombre = firebaseUser.displayName.split(" ").getOrNull(0) ?: "Usuario",
                                    apellido = firebaseUser.displayName.split(" ").getOrNull(1) ?: "Ejemplo",
                                    rut = firebaseUser.rut.ifEmpty { "No registrado" },
                                    telefono = firebaseUser.phoneNumber ?: "",
                                    email = firebaseUser.email ?: "",
                                    direccion = firebaseUser.address.ifEmpty { "" }
                                )
                                onLoginSuccess(localUser)
                            },
                            onError = {
                                showError = true
                            }
                        )
                    } else {
                        showError = true
                    }
                }
            ),
            singleLine = true
        )

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Credenciales incorrectas. Intenta de nuevo.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de "¿Olvidaste tu contraseña?"
        // Primer botón: ¿Olvidaste tu contraseña?

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de inicio de sesión
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    authViewModel.signInUser(
                        email = email,
                        password = password,
                        onSuccess = { firebaseUser ->
                            // Crear usuario local para compatibilidad
                            val localUser = User(
                                id = firebaseUser.id,
                                nombre = firebaseUser.displayName.split(" ").getOrNull(0) ?: "Usuario",
                                apellido = firebaseUser.displayName.split(" ").getOrNull(1) ?: "Ejemplo",
                                rut = firebaseUser.rut.ifEmpty { "No registrado" }, // Obtener RUT de Firestore
                                telefono = firebaseUser.phoneNumber,
                                email = firebaseUser.email,
                                direccion = firebaseUser.address // Obtener dirección de Firestore
                            )
                            onLoginSuccess(localUser)
                        },
                        onError = { error ->
                            showError = true
                        }
                    )
                } else {
                    showError = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FutronoCafeOscuro
            )
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de registro
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FutronoNaranja
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, FutronoNaranja)
        ) {
            Text(
                text = "Crear Cuenta Nueva",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                showForgotPasswordDialog = true
                forgotPasswordEmail = email
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = FutronoCafe,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline // <--- Agregado aquí
                )
            )
        }

// Segundo botón: Entrar sin iniciar sesión
        TextButton(
            onClick = onGuestLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ingresar como invitado",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = FutronoCafe,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline // <--- Agregado aquí
                )
            )
        }
    }
    
    // Diálogo de recuperación de contraseña
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showForgotPasswordDialog = false
                forgotPasswordEmail = ""
                forgotPasswordMessage = null
                forgotPasswordError = false
            },
            title = {
                Text(
                    text = "Recuperar Contraseña",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column {
                    if (forgotPasswordMessage != null) {
                        Text(
                            text = forgotPasswordMessage!!,
                            color = if (forgotPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else {
                        Text(
                            text = "Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    OutlinedTextField(
                        value = forgotPasswordEmail,
                        onValueChange = { forgotPasswordEmail = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Correo electrónico",
                                tint = FutronoCafe
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FutronoCafe,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = forgotPasswordMessage == null
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotPasswordEmail.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(forgotPasswordEmail).matches()) {
                            authViewModel.resetPassword(
                                email = forgotPasswordEmail,
                                onSuccess = {
                                    forgotPasswordMessage = "Se ha enviado un enlace de recuperación a tu correo electrónico. Por favor, revisa tu bandeja de entrada."
                                    forgotPasswordError = false
                                },
                                onError = { error ->
                                    forgotPasswordMessage = when {
                                        error.contains("user-not-found", ignoreCase = true) -> 
                                            "No existe una cuenta con este correo electrónico."
                                        error.contains("invalid-email", ignoreCase = true) -> 
                                            "El correo electrónico no es válido."
                                        else -> 
                                            "Error al enviar el email. Por favor, intenta de nuevo más tarde."
                                    }
                                    forgotPasswordError = true
                                }
                            )
                        } else {
                            forgotPasswordMessage = "Por favor, ingresa un correo electrónico válido."
                            forgotPasswordError = true
                        }
                    },
                    enabled = forgotPasswordMessage == null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoCafeOscuro
                    )
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showForgotPasswordDialog = false
                        forgotPasswordEmail = ""
                        forgotPasswordMessage = null
                        forgotPasswordError = false
                    }
                ) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onBackToLogin: () -> Unit,
    onRegisterSuccess: (User) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Focus requesters para navegar entre campos
    val nombreFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val apellidoFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val rutFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val telefonoFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val direccionFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val emailFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val confirmEmailFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val passwordFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val confirmPasswordFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                ScalableHeadlineSmall(
                    text = "Crear Cuenta",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackToLogin) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

        // Formulario de registro
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // Ajusta el contenido cuando aparece el teclado
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Título
                ScalableHeadlineSmall(
                    text = "Información Personal",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                // Campo Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(nombreFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { apellidoFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }

            item {
                // Campo Apellido
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(apellidoFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { rutFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }

            item {
                // Campo RUT
                OutlinedTextField(
                    value = rut,
                    onValueChange = { rut = it },
                    label = { Text("RUT") },
                    placeholder = { Text("12345678-9") },
                    supportingText = { Text("Formato: 12345678-9 o 12.345.678-9") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(rutFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { telefonoFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }

            item {
                // Campo Teléfono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { newValue ->
                        // Filtrar solo números, + y - para el formato
                        val filtered = newValue.filter { char ->
                            char.isDigit() || char == '+' || char == '-'
                        }
                        // Limitar longitud máxima (13 caracteres para +569XXXXXXXX)
                        if (filtered.length <= 13) {
                            telefono = filtered
                        }
                    },
                    label = { Text("Teléfono Móvil") },
                    placeholder = { Text("+56912345678") },
                    supportingText = { Text("Solo móviles: +569XXXXXXXX") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(telefonoFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { direccionFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }

            item {
                // Campo Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    placeholder = { Text("Calle, número, comuna, región") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.HomeWork,
                            contentDescription = "Dirección",
                            tint = FutronoCafe
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(direccionFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { emailFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }

            item {
                // Campo Correo Electrónico
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Correo electrónico",
                            tint = FutronoCafe
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(emailFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { confirmEmailFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }

            item {
                // Campo Confirmar Correo
                OutlinedTextField(
                    value = confirmEmail,
                    onValueChange = { confirmEmail = it },
                    label = { Text("Confirmar Correo Electrónico") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Confirmar correo",
                            tint = FutronoCafe
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(confirmEmailFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }

            item {
                // Campo Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Contraseña",
                            tint = FutronoCafe
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = FutronoCafe
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { confirmPasswordFocusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }

            item {
                // Campo Confirmar Contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirmar contraseña",
                            tint = FutronoCafe
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = FutronoCafe
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(confirmPasswordFocusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            // Intentar registrar cuando se presiona "Done"
                            val errorType = ValidationHelper.validateFormWithError(nombre, apellido, rut, telefono, email, confirmEmail, password, confirmPassword)
                            if (errorType == null) {
                                val displayName = "$nombre $apellido"
                                authViewModel.registerUser(
                                    email = email,
                                    password = password,
                                    displayName = displayName,
                                    phoneNumber = telefono,
                                    rut = rut,
                                    address = direccion,
                                    onSuccess = { firebaseUser ->
                                        val localUser = User(
                                            id = firebaseUser.id,
                                            nombre = nombre,
                                            apellido = apellido,
                                            rut = firebaseUser.rut.ifEmpty { rut },
                                            telefono = telefono,
                                            email = email,
                                            direccion = firebaseUser.address.ifEmpty { direccion }
                                        )
                                        onRegisterSuccess(localUser)
                                    },
                                    onError = { error ->
                                        showError = true
                                        errorMessage = error
                                    }
                                )
                            } else {
                                showError = true
                                errorMessage = Validators.obtenerMensajeError(errorType ?: TipoError.CAMPO_OBLIGATORIO)
                            }
                        }
                    ),
                    singleLine = true
                )
            }

            if (showError) {
                item {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Botón de registro
                Button(
                    onClick = {
                        val errorType = ValidationHelper.validateFormWithError(nombre, apellido, rut, telefono, email, confirmEmail, password, confirmPassword)
                        if (errorType == null) {
                            val displayName = "$nombre $apellido"
                            authViewModel.registerUser(
                                email = email,
                                password = password,
                                displayName = displayName,
                                phoneNumber = telefono,
                                rut = rut,
                                address = direccion,
                                onSuccess = { firebaseUser ->
                                    // Crear usuario local para compatibilidad
                                    val localUser = User(
                                        id = firebaseUser.id,
                                        nombre = nombre,
                                        apellido = apellido,
                                        rut = firebaseUser.rut.ifEmpty { rut }, // Usar RUT de Firebase, si está vacío usar el del formulario
                                        telefono = telefono,
                                        email = email,
                                        direccion = firebaseUser.address.ifEmpty { direccion } // Usar dirección de Firebase, si está vacía usar la del formulario
                                    )
                                    onRegisterSuccess(localUser)
                                },
                                onError = { error ->
                                    showError = true
                                    errorMessage = error
                                }
                            )
                        } else {
                            showError = true
                            errorMessage = Validators.obtenerMensajeError(errorType ?: TipoError.CAMPO_OBLIGATORIO)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoNaranja
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

//Componente que engloba todo el header
@Composable
private fun FutronoHeader(
    onMenuClick: () -> Unit,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(80.dp)
            .background(FutronoBlanco)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        // Logo alineado a la izquierda con margen natural
        FutronoLogo()

        // Botones a la derecha: Carrito y Menú
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón del carrito con texto
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Carrito",
                    style = MaterialTheme.typography.bodySmall,
                    color = FutronoCafe
                )
                Spacer(modifier = Modifier.height(4.dp))
                CartButton(
                    cartItemCount = cartItemCount,
                    onClick = onCartClick
                )
            }

            // Botón de hamburguesa con texto
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
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(0xFF424242), // Gris oscuro
                            shape = RoundedCornerShape(12.dp)
                        )
                )
                {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

//Pantalla de Home, que llama los componentes separados de header y body
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutronoHomeScreen(
    currentUser: User?,
    categories: List<ProductCategory>,
    onCategoryClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onLogout: () -> Unit,
    cartItemCount: Int,
    onAccessibilityClick: () -> Unit = {},
    onUserProfileClick: () -> Unit = {},
    onMyOrdersClick: () -> Unit = {},
    accessibilityViewModel: AccessibilityViewModel,
    isWorker: Boolean = false,
    isAdmin: Boolean = false,
    onWorkerOrdersClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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

                // Botón de Perfil
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
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

                // Botón de Carrito
                NavigationDrawerItem(
                    icon = {
                        Box {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = FutronoCafe
                            )
                            if (cartItemCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = (-8).dp),
                                    containerColor = FutronoNaranja
                                ) {
                                    Text(
                                        if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    },
                    label = { 
                        Text(
                            "Carrito",
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.Underline
                        ) 
                    },
                    selected = false,
                    onClick = {
                        onCartClick()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = FutronoCafe.copy(alpha = 0.12f),
                        unselectedContainerColor = Color.Transparent
                    )
                )

                // Botón de Mis Pedidos
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = "Mis pedidos",
                            tint = FutronoCafe
                        )
                    },
                    label = { 
                        Text(
                            "Mis Pedidos",
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.Underline
                        ) 
                    },
                    selected = false,
                    onClick = {
                        onMyOrdersClick()
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
                            Icons.Default.Logout,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                FutronoHeader(
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    cartItemCount = cartItemCount,
                    onCartClick = onCartClick
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                Spacer(modifier = Modifier.height(16.dp))

                if (isWorker || isAdmin) {
                    WorkerOrdersButton(onClick = onWorkerOrdersClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp, bottom = 30.dp)
                        .padding(5.dp) // Ajusta el padding interno del recuadro
                ) {
                    ScalableHeadlineLarge(
                        text = "Categorías Disponibles",
                        modifier = Modifier.fillMaxWidth(),
                        color = FutronoCafe,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize // Cambia entre: headlineSmall, headlineMedium, headlineLarge, titleLarge, titleMedium, titleSmall
                    )
                }

                CategoriesGrid(
                    categories = categories,
                    onCategoryClick = onCategoryClick,
                    accessibilityViewModel = accessibilityViewModel
                )

                ViewAllProductsButton(onClick = { onCategoryClick("TODOS") })
                }
            }
        }
    }
}




//Componente que se encarga específicamente del logo
@Composable
private fun FutronoLogo(modifier: Modifier = Modifier) {
    Image(
        // Carga el drawable directamente. Es más eficiente.
        painter = painterResource(id = R.drawable.ic_logo2),
        contentDescription = "Logo Futrono",

        // Mantiene el aspect ratio de la imagen sin distorsionarla
        // Opciones: Fit (ajusta dentro del espacio), Inside (sin recortar), Crop (llena recortando)
        contentScale = ContentScale.Fit, // Cambia a ContentScale.Inside o ContentScale.Crop según necesites

        modifier = modifier
            .height(70.dp) // Altura fija
            .widthIn(max = 165.dp) // Ancho máximo para evitar que sea demasiado grande
            .padding(start = 1.dp)
    )
}


//Componente que se encarga específicamente de las acciones del header
@Composable
private fun HeaderActions(
    cartItemCount: Int,
    onAccessibilityClick: () -> Unit,
    onUserProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Columna para Configuración
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconActionButton(
                onClick = onAccessibilityClick,
                icon = Icons.Default.AccessibilityNew,
                description = "Accesibilidad",
                backgroundColor = FutronoCafe
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Accesibilidad",
                style = MaterialTheme.typography.bodySmall,
                color = FutronoCafe
            )
        }

        // 2. Columna para Perfil
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconActionButton(
                onClick = onUserProfileClick,
                icon = Icons.Default.Person,
                description = "Mi cuenta",
                backgroundColor = FutronoCafe
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.bodySmall,
                color = FutronoCafe
            )
        }

        // 3. Columna para Carrito
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CartButton(
                cartItemCount = cartItemCount,
                onClick = onCartClick
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Carrito",
                style = MaterialTheme.typography.labelSmall,
                color = FutronoCafe // O el color verde si prefieres
            )
        }
    }

}

//Programa las acciones de los botones del header
@Composable
private fun IconActionButton(
    onClick: () -> Unit,
    icon: ImageVector? = null,
    painter: Painter? = null,
    description: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        } else if (painter != null) {
            Icon(
                painter = painter,
                contentDescription = description,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

//Botón del carrito
@Composable
private fun CartButton(
    cartItemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Carrito"
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = FutronoVerde,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Carrito de compras",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }


        if (cartItemCount > 0) {
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp),
                containerColor = FutronoCafe
            ) {
                Text(
                    text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun WorkerOrdersButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50)
        )
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Gestión de Pedidos",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}
//Selección de categorías
@Composable
private fun CategoriesGrid(
    categories: List<ProductCategory>,
    onCategoryClick: (String) -> Unit,
    accessibilityViewModel: AccessibilityViewModel,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.name) },
                accessibilityViewModel = accessibilityViewModel
            )
        }
    }
}

//Ver todos los productos
@Composable
private fun ViewAllProductsButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 8.dp) // Padding externo
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, RoundedCornerShape(10.dp)), // Sombra aplicada al botón
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FutronoNaranja
            )
        ) {
            Text(
                text = "Ver todos los productos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoBlanco
            )
        }
    }
}


@Composable
fun CategoryCard(
    category: ProductCategory,
    onClick: () -> Unit,
    accessibilityViewModel: AccessibilityViewModel,
    modifier: Modifier = Modifier
) {
    // Obtener el factor de escala de accesibilidad de forma reactiva
    // Como textScaleFactor es un mutableStateOf, Compose observará los cambios automáticamente
    // al leer el valor dentro del Composable
    val scaleFactor = accessibilityViewModel.textScaleFactor
    
    // Tamaño base de la imagen que se escalará según accesibilidad
    // Ajusta este valor para cambiar el tamaño de la imagen (ej: 40.dp más pequeño, 56.dp más grande)
    val baseImageSize = 44.dp
    val scaledImageSize = remember(scaleFactor) { baseImageSize * scaleFactor }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp) //estetica
            .clickable { onClick() }
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.containerColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Imagen de la categoría que se escala según accesibilidad
                Image(
                    painter = painterResource(id = category.imageResId),
                    contentDescription = "Icono de ${category.displayName}",
                    modifier = Modifier
                        .size(scaledImageSize)
                        .padding(bottom = 8.dp),
                )

                ScalableTitleSmall(
                    text = category.displayName,
                    color = FutronoBlanco,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    category: String,
    onBackClick: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var productsFromDb by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    // Carga los productos desde Firebase
    LaunchedEffect(category) {
        isLoading = true
        error = null

        val onResult: (List<Product>) -> Unit = { productList ->
            productsFromDb = productList
            isLoading = false
        }
        val onError: (Exception) -> Unit = { exception ->
            error = exception.message
            isLoading = false
        }

        if (category == "TODOS") {
            FirebaseHelper.getAllProductsFromFirebase(onResult, onError)
        } else {
            FirebaseHelper.getProductsByCategoryFromFirebase(category, onResult, onError)
        }
    }

    // LÓGICA DE FILTRADO
    val filteredProducts = remember(searchQuery, productsFromDb, selectedCategories) {
        productsFromDb.filter { product ->
            // Filtro por búsqueda
            val matchesSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)

            // Filtro por categorías
            val matchesCategory = selectedCategories.isEmpty() ||
                selectedCategories.contains("Todos") ||
                selectedCategories.contains(product.category.displayName)

            matchesSearch && matchesCategory
        }
    }

    // Aquí se mejora lo visual del simón y las funcionalidades
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ScalableHeadlineSmall(
                        text = when (category) {
                            "TODOS" -> "Todos los Productos"
                            "CARNES_PESCADOS" -> "Carnes y Pescados"
                            "DESPENSA" -> "Despensa"
                            "FRUTAS_VERDURAS" -> "Frutas y Verduras"
                            "BEBIDAS_SNACKS" -> "Bebidas y Snacks"
                            "FRESCOS_LACTEOS" -> "Frescos y lácteos"
                            "PANADERIA_PASTELERIA" -> "Panadería y pastelería"
                            else -> category
                        },
                        color = FutronoBlanco,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = FutronoBlanco
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = onCartClick) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito de compras",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        if (cartItemCount > 0) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp),
                                containerColor = FutronoCafe
                            ) {
                                Text(
                                    if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(12.dp),
                    containerColor = FutronoSuccess,      // <-- Color de fondo
                    contentColor = FutronoBlanco,        // <-- Color del texto
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = data.visuals.message)
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {

        // Botones de categoría arriba del buscador (solo cuando se ven todos los productos)
        if (category == "TODOS") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(getProductCategoriesForClient()) { categoryName ->
                        CategoryChipClient(
                            category = categoryName,
                            isSelected = selectedCategories.contains(categoryName),
                            onClick = {
                                selectedCategories = if (categoryName == "Todos") {
                                    if (selectedCategories.contains("Todos")) {
                                        emptySet()
                                    } else {
                                        setOf("Todos")
                                    }
                                } else {
                                    val newSet = selectedCategories.toMutableSet()
                                    if (newSet.contains(categoryName)) {
                                        newSet.remove(categoryName)
                                    } else {
                                        newSet.remove("Todos") // Si selecciona una categoría específica, quitar "Todos"
                                        newSet.add(categoryName)
                                    }
                                    newSet
                                }
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text("Ingresar búsqueda...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (error != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(text = "Error al cargar productos: $error", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            } else if (filteredProducts.isEmpty()) { // Mensaje para cuando no hay resultados
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 50.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No se encontraron productos para \"$searchQuery\"",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Aquí usamos la lista `filteredProducts`
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = onAddToCart
                    )
                }
            }
        }
        }
    }
}

@Composable
fun CategoryChipClient(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(text = category)
        },
        selected = isSelected,
        modifier = modifier
    )
}

// Funciones para obtener categorías de productos para el cliente
fun getProductCategoriesForClient(): List<String> {
    return listOf("Todos") + ProductCategory.values().map { it.displayName }
}

// Estado de carga de imagen
private enum class ImageLoadingState {
    Loading,
    Success,
    Error
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FutronoBlanco
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Imagen en la parte superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FutronoBlanco),
                contentAlignment = Alignment.Center
            ) {
                // Si la URL de la imagen no está vacía, intenta cargarla
                if (!product.imageUrl.isNullOrBlank()) {
                    var imageLoadingState by remember(product.imageUrl) { mutableStateOf<ImageLoadingState>(ImageLoadingState.Loading) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    
                    // Convertir URL de Google Drive a formato directo si es necesario
                    val imageUrl = remember(product.imageUrl) {
                        val url = product.imageUrl.trim()
                        when {
                            // Google Drive: convertir de formato compartir a formato directo
                            url.contains("drive.google.com/file/d/", ignoreCase = true) -> {
                                val fileId = url.substringAfter("file/d/").substringBefore("/").substringBefore("?")
                                "https://drive.google.com/uc?export=view&id=$fileId"
                            }
                            // Google Drive: formato de vista previa
                            url.contains("drive.google.com/uc?id=", ignoreCase = true) -> {
                                url // Ya está en formato correcto
                            }
                            // SharePoint: advertir pero intentar cargar
                            url.contains("sharepoint.com", ignoreCase = true) -> {
                                android.util.Log.w("ProductCard", "⚠️ URL de SharePoint detectada - puede requerir autenticación")
                                url
                            }
                            else -> url
                        }
                    }
                    
                    // Cargar la imagen
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .allowHardware(false)
                            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "Imagen de ${product.name}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (imageLoadingState == ImageLoadingState.Success) 1f else 0f),
                        onLoading = {
                            imageLoadingState = ImageLoadingState.Loading
                            errorMessage = null
                        },
                        onSuccess = {
                            imageLoadingState = ImageLoadingState.Success
                            errorMessage = null
                            android.util.Log.d("ProductCard", "✅ Imagen cargada exitosamente: $imageUrl")
                        },
                        onError = { error ->
                            imageLoadingState = ImageLoadingState.Error
                            val throwable = error.result.throwable
                            errorMessage = throwable?.message ?: "Error desconocido"
                            android.util.Log.e("ProductCard", "❌ Error al cargar imagen")
                            android.util.Log.e("ProductCard", "URL original: ${product.imageUrl}")
                            android.util.Log.e("ProductCard", "URL procesada: $imageUrl")
                            android.util.Log.e("ProductCard", "Mensaje: ${throwable?.message}")
                            android.util.Log.e("ProductCard", "Tipo: ${throwable?.javaClass?.simpleName}")
                            if (product.imageUrl.contains("sharepoint.com", ignoreCase = true)) {
                                android.util.Log.e("ProductCard", "⚠️ Las URLs de SharePoint requieren autenticación y no son accesibles directamente")
                            } else if (product.imageUrl.contains("drive.google.com", ignoreCase = true)) {
                                android.util.Log.e("ProductCard", "⚠️ Asegúrate de que el archivo de Google Drive esté compartido como 'Cualquiera con el enlace'")
                            }
                            throwable?.printStackTrace()
                        }
                    )
                    
                    // Mostrar placeholder o error solo cuando la imagen no está lista
                    if (imageLoadingState != ImageLoadingState.Success) {
                        when (imageLoadingState) {
                            ImageLoadingState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp
                                )
                            }
                            ImageLoadingState.Error -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ImageNotSupported,
                                        contentDescription = "Error al cargar imagen",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    if (errorMessage != null && errorMessage!!.length < 50) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = errorMessage!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                            ImageLoadingState.Success -> { /* No hacer nada */ }
                        }
                    }
                } else {
                    // Si no hay URL, muestra el ícono directamente
                    Icon(
                        imageVector = Icons.Default.ImageNotSupported,
                        contentDescription = "Imagen no disponible",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información del producto en la parte inferior
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$${String.format("%,.0f", product.price).replace(",", ".")} / ${product.unit}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stock: ${product.stock}",
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                product.stock > 50 -> StockHigh
                                product.stock > 30 -> StockMedium
                                else -> StockLow
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Botón para agregar al carrito
                Button(
                    onClick = { onAddToCart(product) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoNaranja,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    enabled = product.stock > 0,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Agregar al carrito",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    // Estado para mostrar el diálogo de confirmación
    var showClearCartDialog by remember { mutableStateOf(false) }
    
    // Aquí se mejora lo visual del simón y las funcionalidades
    Scaffold(
        topBar = {
            TopAppBar(
            title = {
                ScalableHeadlineSmall(
                    text = "Carrito de Compras",
                    color = FutronoBlanco,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = FutronoBlanco
                    )
                }
            },
            actions = {
                if (cartItems.isNotEmpty()) {
                    IconButton(onClick = { showClearCartDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Vaciar carrito",
                            tint = FutronoBlanco
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = FutronoCafe
            )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(12.dp),
                    containerColor = FutronoError,
                    contentColor = FutronoBlanco,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = data.visuals.message)
                }
            }
        },
    ) { paddingValues ->
        // Diálogo de confirmación para vaciar el carrito
        if (showClearCartDialog) {
            AlertDialog(
                onDismissRequest = { showClearCartDialog = false },
                title = {
                    Text(
                        text = "Confirmar eliminación",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "¿Desea eliminar los productos que están en su carrito?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onClearCart()
                            showClearCartDialog = false
                        }
                    ) {
                        Text(
                            text = "Sí",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showClearCartDialog = false }
                    ) {
                        Text(
                            text = "No",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
        // Contenido principal
        if (cartItems.isEmpty()) {
            // Carrito vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Agrega productos para comenzar a comprar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Lista de productos en el carrito
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onUpdateQuantity = onUpdateQuantity,
                        onRemoveItem = onRemoveItem
                    )
                }
            }

            // Resumen y botón de checkout
            CartSummary(
                cartItems = cartItems,
                onCheckout = onCheckout
            )
        }
        }
    }
}

@Composable
fun CategorySelectionScreen(
    onCategoryClick: (String) -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int,
    accessibilityViewModel: AccessibilityViewModel,
    modifier: Modifier = Modifier
) {
    // --- 1. ESTADO PARA LA BÚSQUEDA Y LA LISTA FILTRADA ---
    var searchQuery by remember { mutableStateOf("") }
    val allCategories = remember { ProductCategory.values() }

    val filteredCategories = remember(searchQuery, allCategories) {
        if (searchQuery.isBlank()) {
            allCategories.toList() // Muestra todas las categorías si no hay búsqueda
        } else {
            // Filtra las categorías cuyo nombre de visualización contenga el texto de búsqueda
            allCategories.filter {
                it.displayName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // --- Header con logo y carrito (Tu código original, sin cambios) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FUTRONO",
                    color = FutronoCafe,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp // Tamaño de ejemplo, puedes usar tu ScalableHeadlineLarge
                )
                Text(
                    text = "Supermercado",
                    color = FutronoNaranja,
                    fontSize = 16.sp // Tamaño de ejemplo, puedes usar tu ScalableBodyLarge
                )
            }
            Box {
                FloatingActionButton(
                    onClick = onCartClick,
                    containerColor = FutronoNaranja,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrito de compras",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                if (cartItemCount > 0) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp),
                        containerColor = FutronoCafe
                    ) {
                        Text(
                            if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. EL ELEMENTO A AÑADIR: EL BUSCADOR ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            label = { Text("Buscar categoría...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Icono de búsqueda") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Título (Tu código original, sin cambios) ---
        Text(
            text = "Nuestras Categorías",
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp // Tamaño de ejemplo, puedes usar tu ScalableHeadlineMedium
        )

        // --- 3. USA LA LISTA FILTRADA EN EL GRID ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredCategories) { category ->
                Text(text = category.displayName, modifier = Modifier.clickable { onCategoryClick(category.name) })
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = FutronoBlanco // Cambia este color para el fondo de la tarjeta (ej: FutronoFondo, FutronoBlanco, FutronoCafe)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%,.0f", cartItem.product.price).replace(",", ".")} por ${cartItem.product.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total: ${String.format("%,.0f", cartItem.totalPrice).replace(",", ".")}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = FutronoCafe
                    )
                )
            }

            // Controles de cantidad
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            onUpdateQuantity(cartItem.product.id, cartItem.quantity - 1)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Reducir cantidad",
                            tint = FutronoCafe
                        )
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onUpdateQuantity(cartItem.product.id, cartItem.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Aumentar cantidad",
                            tint = FutronoCafe
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón eliminar
                TextButton(
                    onClick = { onRemoveItem(cartItem.product.id) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
fun CartSummary(
    cartItems: List<CartItem>,
    onCheckout: () -> Unit
) {
    val totalItems = cartItems.sumOf { it.quantity }
    val totalPrice = cartItems.sumOf { it.totalPrice }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total de productos:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FutronoBlanco
                )

                Text(
                    text = "$totalItems",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total a pagar:",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
                Text(
                    text = "${String.format("%,.0f", totalPrice).replace(",", ".")}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                    )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de checkout
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FutronoNaranja
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Finalizar Compra",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
            }
        }
    }
}

object FirebaseHelper {
    // Función de conveniencia para usar con callbacks (compatible con código existente)
    fun getAllProductsFromFirebase(
        onResult: (List<Product>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val firebaseService = com.example.intento1app.data.services.ProductFirebaseService()
        firebaseService.getAllProductsFromFirebase(onResult, onError)
    }

    // Función de conveniencia para obtener productos por categoría
    fun getProductsByCategoryFromFirebase(
        category: String,
        onResult: (List<Product>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val firebaseService = com.example.intento1app.data.services.ProductFirebaseService()
        try {
            val productCategory = ProductCategory.valueOf(category)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val products = firebaseService.getProductsByCategory(productCategory)
                    onResult(products)
                } catch (e: Exception) {
                    onError(e)
                }
            }
        } catch (e: IllegalArgumentException) {
            onError(Exception("Categoría no válida: $category"))
        }
    }
}

// Objeto para funciones de utilidad
object ValidationHelper {
fun validateForm(nombre: String, apellido: String, rut: String, telefono: String, email: String, confirmEmail: String, password: String, confirmPassword: String): Boolean {
    // Validar campos obligatorios
    if (!Validators.validarCampoObligatorio(nombre) ||
        !Validators.validarCampoObligatorio(apellido) ||
        !Validators.validarCampoObligatorio(rut) ||
        !Validators.validarCampoObligatorio(telefono) ||
        !Validators.validarCampoObligatorio(email) ||
        !Validators.validarCampoObligatorio(confirmEmail) ||
        !Validators.validarCampoObligatorio(password) ||
        !Validators.validarCampoObligatorio(confirmPassword)) {
        return false
    }

    // Validar RUT chileno
    if (!Validators.validarRUT(rut)) {
        return false
    }

    // Validar teléfono chileno
    if (!Validators.validarTelefono(telefono)) {
        return false
    }

    // Validar email
    if (!Validators.validarEmail(email)) {
        return false
    }

    // Validar que los emails coincidan
    if (!Validators.validarConfirmacionEmail(email, confirmEmail)) {
        return false
    }

    // Validar contraseña
    if (!Validators.validarPassword(password)) {
        return false
    }

    // Validar que las contraseñas coincidan
    if (!Validators.validarConfirmacionPassword(password, confirmPassword)) {
        return false
    }

    return true
}
//validaciones del formulario de registro
fun validateFormWithError(nombre: String, apellido: String, rut: String, telefono: String, email: String, confirmEmail: String, password: String, confirmPassword: String): TipoError? {
    // Validar campos obligatorios
    if (!Validators.validarCampoObligatorio(nombre)) return TipoError.CAMPO_OBLIGATORIO
    if (!Validators.validarCampoObligatorio(apellido)) return TipoError.CAMPO_OBLIGATORIO
    if (!Validators.validarCampoObligatorio(rut)) return TipoError.CAMPO_OBLIGATORIO
    if (!Validators.validarCampoObligatorio(telefono)) return TipoError.CAMPO_OBLIGATORIO
    if (!Validators.validarCampoObligatorio(email)) return TipoError.CAMPO_OBLIGATORIO
    if (!Validators.validarCampoObligatorio(confirmEmail)) return TipoError.CAMPO_OBLIGATORIO
    if (!Validators.validarCampoObligatorio(password)) return TipoError.CAMPO_OBLIGATORIO
    if (!Validators.validarCampoObligatorio(confirmPassword)) return TipoError.CAMPO_OBLIGATORIO

    // Validar RUT chileno
    if (!Validators.validarRUT(rut)) return TipoError.RUT_INVALIDO

    // Validar teléfono chileno
    if (!Validators.validarTelefono(telefono)) return TipoError.TELEFONO_INVALIDO

    // Validar email
    if (!Validators.validarEmail(email)) return TipoError.EMAIL_INVALIDO

    // Validar que los emails coincidan
    if (!Validators.validarConfirmacionEmail(email, confirmEmail)) return TipoError.EMAIL_NO_COINCIDE

    // Validar contraseña
    if (!Validators.validarPassword(password)) return TipoError.PASSWORD_CORTA

    // Validar que las contraseñas coincidan
    if (!Validators.validarConfirmacionPassword(password, confirmPassword)) return TipoError.PASSWORD_NO_COINCIDE
    return null // Sin errores
}
}

