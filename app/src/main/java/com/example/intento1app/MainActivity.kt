package com.example.intento1app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import com.example.intento1app.utils.Validators
import com.example.intento1app.utils.TipoError
import com.example.intento1app.viewmodel.PaymentViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.intento1app.ui.screens.PaymentScreen
import com.example.intento1app.ui.screens.AccessibilityScreen
import com.example.intento1app.ui.screens.MercadoPagoCheckoutScreen
import com.example.intento1app.ui.screens.UserProfileScreen
import com.example.intento1app.ui.screens.MyOrdersScreen
import com.example.intento1app.ui.screens.MyDataScreen
import com.example.intento1app.ui.screens.MyBankDetailsScreen
import com.example.intento1app.ui.screens.HelpAndContactScreen
import com.example.intento1app.ui.screens.WorkerOrdersScreen
import com.example.intento1app.ui.screens.WorkerHomeScreen
import com.example.intento1app.ui.screens.InventoryScreen
import com.example.intento1app.ui.screens.WorkerCustomersScreen
import com.example.intento1app.ui.screens.WorkerNotificationsScreen
import com.example.intento1app.data.models.User
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import coil.request.ImageRequest
import com.example.intento1app.ui.components.ScalableHeadlineMedium
import com.example.intento1app.ui.components.ScalableHeadlineSmall
import com.example.intento1app.ui.components.ScalableTitleMedium
import com.example.intento1app.viewmodel.AccessibilityViewModel
import com.example.intento1app.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.intento1app.data.models.CartItem
import com.example.intento1app.ui.screens.AddProductScreen
import com.example.intento1app.ui.screens.WorkerProductsScreen
import com.example.intento1app.ui.theme.StockHigh
import com.example.intento1app.ui.theme.StockLow
import com.example.intento1app.ui.theme.StockMedium

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    val paymentViewModel: PaymentViewModel = remember { PaymentViewModel() }

    var currentScreen by remember { mutableStateOf("loading") } // Cambiar a loading inicialmente
    var isCheckingAuth by remember { mutableStateOf(true) } // Estado para verificar autenticación

    // Observar el estado de autenticación
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentFirebaseUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var showCheckout by remember { mutableStateOf(false) }
    var checkoutUrl by remember { mutableStateOf("") }

    // Variables de estado para las pantallas del perfil
    var showUserProfile by remember { mutableStateOf(false) }
    var showMyOrders by remember { mutableStateOf(false) }
    var showMyData by remember { mutableStateOf(false) }
    var showMyBankDetails by remember { mutableStateOf(false) }
    var showHelpAndContact by remember { mutableStateOf(false) }
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

        // Mostrar la pantalla anterior basándose en la pila
        when (previousScreen) {
            "userProfile" -> showUserProfile = true
            "myData" -> showMyData = true
            "myOrders" -> showMyOrders = true
            "myBankDetails" -> showMyBankDetails = true
            "helpAndContact" -> showHelpAndContact = true
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
            else -> {
                // Si no hay pantalla anterior, volver a home
                currentScreen = "home"
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
                    rut = "12345678-9", // Se puede obtener de Firestore
                    telefono = firebaseUser.phoneNumber,
                    email = firebaseUser.email
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
                    rut = "12345678-9", // Se puede obtener de Firestore
                    telefono = firebaseUser.phoneNumber,
                    email = firebaseUser.email
                )
                currentUser = localUser
                println("MainActivity: Usuario sincronizado con roles válidos: ${userRoles}")
            } else {
                println("MainActivity: Usuario sin roles válidos, no se sincroniza: ${userRoles}")
                currentUser = null
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
                }
            )
        }
        showWorkerOrders -> {
            // Pantalla de gestión de pedidos para trabajadores
            WorkerOrdersScreen(
                paymentViewModel = paymentViewModel,
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
                paymentViewModel = paymentViewModel,
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
                paymentViewModel = paymentViewModel,
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
                onDeleteAccountClick = {
                    // TODO: Implementar eliminación de cuenta
                    println("Eliminar cuenta - No implementado aún")
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
                }
            )
        }
        showCheckout -> {
            // Pantalla de checkout de Mercado Pago
            MercadoPagoCheckoutScreen(
                checkoutUrl = checkoutUrl,
                onBack = {
                    showCheckout = false
                    currentScreen = "payment"
                },
                onPaymentSuccess = {
                    showCheckout = false
                    // Guardar compra en historial antes de limpiar carrito
                    currentUser?.let { user ->
                        val paymentId = "mp_${System.currentTimeMillis()}"
                        val orderNumber = "ORD-${System.currentTimeMillis().toString().takeLast(6)}"
                        paymentViewModel.savePurchaseToHistory(
                            userId = user.id,
                            userEmail = user.email,
                            userName = "${user.nombre} ${user.apellido}",
                            userPhone = user.telefono,
                            cartItems = cartItems,
                            paymentId = paymentId,
                            orderNumber = orderNumber
                        )
                    }
                    cartItems = emptyList() // Limpiar carrito
                    currentScreen = "home"
                },
                onPaymentFailure = {
                    showCheckout = false
                    currentScreen = "payment"
                },
                onPaymentPending = {
                    showCheckout = false
                    currentScreen = "payment"
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
                currentUser = User("guest", "Invitado", "Invitado", "", "", "")
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
                // Pantalla específica para trabajadores
                WorkerHomeScreen(
                    currentUser = currentUser,
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
                val existingItem = cartItems.find { it.product.id == product.id }
                if (existingItem != null) {
                    cartItems = cartItems.map { item ->
                        if (item.product.id == product.id) {
                            item.copy(quantity = item.quantity + 1)
                        } else {
                            item
                        }
                    }
                } else {
                    cartItems = cartItems + CartItem(product, 1)
                }
            },
            onCartClick = {
                currentScreen = "cart"
            },
            cartItemCount = cartItems.sumOf { it.quantity }
        )
        currentScreen == "cart" -> CartScreen(
            cartItems = cartItems,
            onBackClick = {
                currentScreen = "home"
            },
            onUpdateQuantity = { productId, quantity ->
                if (quantity <= 0) {
                    cartItems = cartItems.filter { it.product.id != productId }
                } else {
                    cartItems = cartItems.map { item ->
                        if (item.product.id == productId) {
                            item.copy(quantity = quantity)
                        } else {
                            item
                        }
                    }
                }
            },
            onRemoveItem = { productId ->
                cartItems = cartItems.filter { it.product.id != productId }
            },
            onClearCart = {
                cartItems = emptyList()
            },
            onCheckout = {
                // Navegar a la pantalla de pago
                currentScreen = "payment"
            }
        )
        currentScreen == "payment" -> PaymentScreen(
            cartItems = cartItems,
            currentUser = currentUser, // Pasar usuario actual
            onPaymentComplete = {
                // Pago exitoso, limpiar carrito y volver a home
                println("MainActivity: onPaymentComplete llamado - limpiando carrito y navegando a home")
                cartItems = emptyList()
                currentScreen = "home"
                println("MainActivity: Navegación completada - currentScreen = $currentScreen")
            },
            onBackToCart = {
                // Volver al carrito
                currentScreen = "cart"
            },
            onNavigateToCheckout = { url ->
                checkoutUrl = url
                showCheckout = true
            }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo de Futrono
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo), // 1. Carga tu imagen
                contentDescription = "Logo de Futrono Supermercado",      // 2. Texto para accesibilidad
                modifier = Modifier
                    .fillMaxWidth(0.9f) // 3. Ajusta el tamaño del logo (90% del ancho)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FutronoCafe,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FutronoCafeOscuro,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Credenciales incorrectas. Intenta de nuevo.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

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
                                rut = "12345678-9", // Se puede obtener de Firestore
                                telefono = firebaseUser.phoneNumber,
                                email = firebaseUser.email
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

        // Botón de entrar sin cuenta
        TextButton(
            onClick = onGuestLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Entrar sin iniciar sesión",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
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
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            item {
                // Campo Apellido
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
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
                                onSuccess = { firebaseUser ->
                                    // Crear usuario local para compatibilidad
                                    val localUser = User(
                                        id = firebaseUser.id,
                                        nombre = nombre,
                                        apellido = apellido,
                                        rut = rut,
                                        telefono = telefono,
                                        email = email
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
                            errorMessage = Validators.obtenerMensajeError(errorType)
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


//Pantalla de Home, que llama los componentes separados de header y body
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
    accessibilityViewModel: AccessibilityViewModel,
    isWorker: Boolean = false,
    isAdmin: Boolean = false,
    onWorkerOrdersClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        FutronoHeader(
            cartItemCount = cartItemCount,
            onAccessibilityClick = onAccessibilityClick,
            onUserProfileClick = onUserProfileClick,
            onCartClick = onCartClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isWorker || isAdmin) {
            WorkerOrdersButton(onClick = onWorkerOrdersClick)
            Spacer(modifier = Modifier.height(16.dp))
        }

        ScalableHeadlineMedium(
            text = "Categorías Disponibles",
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        CategoriesGrid(
            categories = categories,
            onCategoryClick = onCategoryClick
        )

        ViewAllProductsButton(onClick = { onCategoryClick("TODOS") })
    }
}

//Componente que engloba todo el header
@Composable
private fun FutronoHeader(
    cartItemCount: Int,
    onAccessibilityClick: () -> Unit,
    onUserProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(FutronoFondo)
        // No padding horizontal to let logo align naturally
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo alineado a la izquierda con margen natural
        FutronoLogo()

        HeaderActions(
            cartItemCount = cartItemCount,
            onAccessibilityClick = onAccessibilityClick,
            onUserProfileClick = onUserProfileClick,
            onCartClick = onCartClick
        )
    }
}

//Componente que se encarga específicamente del logo
@Composable
private fun FutronoLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "Logo Futrono",
        contentScale = ContentScale.FillHeight,
        modifier = modifier
            .height(80.dp)
            .aspectRatio(2.2f)
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
        IconActionButton(
            onClick = onAccessibilityClick,
            icon = Icons.Default.Settings,
            description = "Accesibilidad",
            backgroundColor = FutronoCafe
        )

        IconActionButton(
            onClick = onUserProfileClick,
            painter = painterResource(id = R.drawable.ic_person),
            description = "Mi cuenta",
            backgroundColor = FutronoCafe
        )

        CartButton(
            cartItemCount = cartItemCount,
            onClick = onCartClick
        )
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
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = FutronoNaranja,
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
                onClick = { onCategoryClick(category.name) }
            )
        }
    }
}

//Ver todos los productos
@Composable
private fun ViewAllProductsButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FutronoCafe
        )
    ) {
        Text(
            text = "Ver todos los productos",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = FutronoFondo
        )
    }
}


@Composable
fun CategoryCard(
    category: ProductCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.contentColor,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                ScalableTitleMedium(
                    text = category.displayName,
                    color = category.textColor,
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
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var productsFromDb by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Carga los productos desde Firebase (tu código original, sin cambios)
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
    val filteredProducts = remember(searchQuery, productsFromDb) {
        if (searchQuery.isBlank()) {
            productsFromDb // Si no hay búsqueda, muestra todos los productos cargados
        } else {
            productsFromDb.filter { product ->
                // Filtra por nombre O descripción, ignorando mayúsculas/minúsculas
                product.name.contains(searchQuery, ignoreCase = true) ||
                        product.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                ScalableHeadlineSmall(
                    text = if (category == "TODOS") "Todos los Productos" else category,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onPrimary
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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text("Ingresar elemento a buscar...") },
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
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor para la imagen o el ícono
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant), // Fondo para el ícono
                contentAlignment = Alignment.Center
            ) {
                // Si la URL de la imagen no está vacía, intenta cargarla
                if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de ${product.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize() // La imagen llenará el Box
                    )
                } else {
                    // Si no hay URL, muestra el ícono directamente
                    Icon(
                        imageVector = Icons.Default.ImageNotSupported,
                        contentDescription = "Imagen no disponible",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Columna para la información del producto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${String.format("%,.0f", product.price).replace(",", ".")} / ${product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
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

            Spacer(modifier = Modifier.width(12.dp))

            // Botón para agregar al carrito
            IconButton(
                onClick = { onAddToCart(product) },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = FutronoNaranja,
                    contentColor = Color.White
                ),
                enabled = product.stock > 0
            ) {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = "Agregar al carrito"
                )
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
                ScalableHeadlineSmall(
                    text = "Carrito de Compras",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            actions = {
                if (cartItems.isNotEmpty()) {
                    IconButton(onClick = onClearCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Vaciar carrito",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

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
            // Se cambia "ProductCategory.values()" por "filteredCategories"
            items(filteredCategories) { category ->
                // Este es un Composable de tu proyecto, asumo que existe y funciona
                // CategoryCard(
                //     category = category,
                //     onClick = { onCategoryClick(category.name) },
                //     modifier = Modifier.fillMaxWidth()
                // )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "$totalItems",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
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
                    )
                )
                Text(
                    text = "${String.format("%,.0f", totalPrice).replace(",", ".")}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
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
                    )
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