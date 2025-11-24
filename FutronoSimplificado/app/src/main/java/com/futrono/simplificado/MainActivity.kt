package com.futrono.simplificado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.fontScale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.futrono.simplificado.data.models.*
import com.futrono.simplificado.ui.screens.MercadoPagoCheckoutScreen
import com.futrono.simplificado.ui.screens.PaymentScreen
import com.futrono.simplificado.ui.theme.FutronoAppTheme
import com.futrono.simplificado.ui.theme.FutronoCafe
import com.futrono.simplificado.ui.theme.FutronoFondo
import com.futrono.simplificado.ui.theme.FutronoNaranja
import com.futrono.simplificado.viewmodel.AuthViewModel
import com.futrono.simplificado.viewmodel.PaymentViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.tasks.await

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
            FutronoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SimpleFutronoApp()
                }
            }
        }
    }
}

@Composable
fun SimpleFutronoApp() {
    val authViewModel: AuthViewModel = viewModel()
    val paymentViewModel: PaymentViewModel = remember { PaymentViewModel() }

    var currentScreen by remember { mutableStateOf("loading") }
    var isCheckingAuth by remember { mutableStateOf(true) }
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var showCheckout by remember { mutableStateOf(false) }
    var checkoutUrl by remember { mutableStateOf("") }

    // Estados de autenticación
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentFirebaseUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    // Verificar estado de autenticación al inicializar
    LaunchedEffect(isLoggedIn, currentFirebaseUser) {
        val firebaseUser = currentFirebaseUser
        if (isLoggedIn && firebaseUser != null) {
            val localUser = User(
                id = firebaseUser.id,
                nombre = firebaseUser.displayName.split(" ").getOrNull(0) ?: "Usuario",
                apellido = firebaseUser.displayName.split(" ").getOrNull(1) ?: "Ejemplo",
                rut = "12345678-9",
                telefono = firebaseUser.phoneNumber ?: "",
                email = firebaseUser.email ?: ""
            )
            currentUser = localUser
            currentScreen = "home"
            isCheckingAuth = false
        } else if (!isLoggedIn && !isCheckingAuth) {
            currentScreen = "auth"
        }
    }

    // Timeout para la verificación de autenticación
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        if (isCheckingAuth) {
            isCheckingAuth = false
            if (!isLoggedIn) {
                currentScreen = "auth"
            }
        }
    }

    // Manejar el botón de atrás del dispositivo en todas las pantallas
    // Siempre habilitado excepto cuando estamos cargando
    BackHandler(enabled = currentScreen != "loading" && !isCheckingAuth) {
        when {
            showCheckout -> {
                // Si estamos en el checkout de MercadoPago, volver a payment
                showCheckout = false
                currentScreen = "payment"
            }
            currentScreen == "payment" -> {
                // Si estamos en payment, volver a cart
                currentScreen = "cart"
            }
            currentScreen == "cart" -> {
                // Si estamos en cart, volver a home
                currentScreen = "home"
            }
            currentScreen == "home" -> {
                // Si estamos en home, no hacer nada (el BackHandler consume el evento)
                // Esto previene que la app se cierre
            }
            currentScreen == "auth" -> {
                // Si estamos en auth, no hacer nada (el BackHandler consume el evento)
                // Esto previene que la app se cierre
            }
        }
    }

    when {
        currentScreen == "loading" || isCheckingAuth -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FutronoFondo),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = FutronoCafe)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando...", color = FutronoCafe)
                }
            }
        }
        showCheckout -> {
            MercadoPagoCheckoutScreen(
                checkoutUrl = checkoutUrl,
                onBack = {
                    showCheckout = false
                    currentScreen = "payment"
                },
                onPaymentSuccess = {
                    showCheckout = false
                    cartItems = emptyList()
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
        currentScreen == "auth" -> SimpleAuthScreen(
            authViewModel = authViewModel,
            onLoginSuccess = { user ->
                currentUser = user
                currentScreen = "home"
            }
        )
        currentScreen == "home" -> SimpleHomeScreen(
            currentUser = currentUser,
            cartItems = cartItems,
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
            onLogout = {
                currentUser = null
                cartItems = emptyList()
                authViewModel.signOut()
                currentScreen = "auth"
            }
        )
        currentScreen == "cart" -> SimpleCartScreen(
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
            onCheckout = {
                currentScreen = "payment"
            }
        )
        currentScreen == "payment" -> PaymentScreen(
            cartItems = cartItems,
            currentUser = currentUser,
            onPaymentComplete = {
                cartItems = emptyList()
                currentScreen = "home"
            },
            onBackToCart = {
                currentScreen = "cart"
            },
            onNavigateToCheckout = { url ->
                checkoutUrl = url
                showCheckout = true
            },
            viewModel = paymentViewModel
        )
    }
}

@Composable
fun SimpleAuthScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (User) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding() // Ajusta el contenido cuando aparece el teclado
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Futrono Supermercado",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = FutronoCafe
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = FutronoCafe)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = FutronoCafe)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                        tint = FutronoCafe
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
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
                                    rut = "12345678-9",
                                    telefono = firebaseUser.phoneNumber ?: "",
                                    email = firebaseUser.email ?: ""
                                )
                                onLoginSuccess(localUser)
                            },
                            onError = {
                                showError = true
                            }
                        )
                    }
                }
            ),
            singleLine = true
        )

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Credenciales incorrectas",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                keyboardController?.hide()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    authViewModel.signInUser(
                        email = email,
                        password = password,
                        onSuccess = { firebaseUser ->
                            val localUser = User(
                                id = firebaseUser.id,
                                nombre = firebaseUser.displayName.split(" ").getOrNull(0) ?: "Usuario",
                                apellido = firebaseUser.displayName.split(" ").getOrNull(1) ?: "Ejemplo",
                                rut = "12345678-9",
                                telefono = firebaseUser.phoneNumber ?: "",
                                email = firebaseUser.email ?: ""
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
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FutronoCafe)
        ) {
            Text("Iniciar Sesión", fontWeight = FontWeight.Bold)
        }
        
        // Spacer adicional para asegurar que el botón sea visible cuando aparece el teclado
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleHomeScreen(
    currentUser: User?,
    cartItems: List<CartItem>,
    onAddToCart: (Product) -> Unit,
    onCartClick: () -> Unit,
    onLogout: () -> Unit
) {
    // Una sola categoría: DESPENSA
    val category = ProductCategory.DESPENSA
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Estado del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val topAppBarState = rememberTopAppBarState()

    // Cargar el primer producto de la categoría DESPENSA desde Firebase
    LaunchedEffect(Unit) {
        try {
            val result = Firebase.firestore.collection("products")
                .whereEqualTo("category", category.name)
                .limit(1)
                .get()
                .await()

            if (!result.isEmpty) {
                val doc = result.documents.first()
                product = Product(
                    id = doc.getString("id") ?: doc.id,
                    name = doc.getString("name") ?: "Producto",
                    description = doc.getString("description") ?: "Descripción",
                    price = doc.getDouble("price") ?: 0.0,
                    category = category,
                    imageUrl = doc.getString("imageUrl") ?: "",
                    unit = doc.getString("unit") ?: "unidad",
                    stock = doc.getLong("stock")?.toInt() ?: 100,
                    isAvailable = doc.getBoolean("isAvailable") ?: true
                )
            } else {
                // Si no hay productos en Firebase, crear uno de ejemplo
                product = Product(
                    id = "demo-product",
                    name = "Producto Demo",
                    description = "Este es un producto de demostración",
                    price = 1000.0,
                    category = category,
                    imageUrl = "",
                    unit = "unidad",
                    stock = 10,
                    isAvailable = true
                )
            }
            isLoading = false
        } catch (e: Exception) {
            println("Error al cargar producto: ${e.message}")
            // Producto de ejemplo en caso de error
            product = Product(
                id = "demo-product",
                name = "Producto Demo",
                description = "Este es un producto de demostración",
                price = 1000.0,
                category = category,
                imageUrl = "",
                unit = "unidad",
                stock = 10,
                isAvailable = true
            )
            isLoading = false
        }
    }

    // Detectar el ancho de la pantalla y el tamaño del texto
    BoxWithConstraints {
        val screenWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        val configuration = LocalConfiguration.current
        val fontScale = configuration.fontScale
        
        // Usar menú hamburguesa si la pantalla es pequeña O el texto es muy grande
        // Para testing, puedes cambiar el umbral (por ejemplo, usar siempre true para ver el menú)
        val useCompactLayout = screenWidth < 600.dp || fontScale > 1.2f
        
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerContentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Futrono",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FutronoCafe,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    NavigationDrawerItem(
                        icon = {
                            Box {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Carrito",
                                    tint = FutronoNaranja
                                )
                                if (cartItems.isNotEmpty()) {
                                    Badge(
                                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-6).dp),
                                        containerColor = FutronoCafe
                                    ) {
                                        Text(
                                            text = cartItems.sumOf { it.quantity }.toString(),
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        },
                        label = { Text("Carrito") },
                        selected = false,
                        onClick = {
                            onCartClick()
                            drawerState.close()
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = FutronoCafe.copy(alpha = 0.12f)
                        )
                    )
                    
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = FutronoCafe
                            )
                        },
                        label = { Text("Cerrar Sesión") },
                        selected = false,
                        onClick = {
                            onLogout()
                            drawerState.close()
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = FutronoCafe.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Futrono",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = FutronoCafe,
                                modifier = Modifier.widthIn(max = screenWidth * 0.5f)
                            )
                        },
                        navigationIcon = {
                            if (useCompactLayout) {
                                IconButton(onClick = { drawerState.open() }) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Menú",
                                        tint = FutronoCafe
                                    )
                                }
                            }
                        },
                        actions = {
                            if (!useCompactLayout) {
                                // Mostrar botones directamente en pantallas grandes
                                Box {
                                    IconButton(onClick = onCartClick) {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = "Carrito",
                                            tint = FutronoNaranja
                                        )
                                    }
                                    if (cartItems.isNotEmpty()) {
                                        Badge(
                                            modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-6).dp),
                                            containerColor = FutronoCafe
                                        ) {
                                            Text(
                                                text = cartItems.sumOf { it.quantity }.toString(),
                                                color = Color.White,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = onLogout) {
                                    Icon(
                                        Icons.Default.Logout,
                                        contentDescription = "Cerrar sesión",
                                        tint = FutronoCafe
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Categoría
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Producto
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (product != null) {
                        SimpleProductCard(
                            product = product!!,
                            onAddToCart = onAddToCart
                        )
                    } else {
                        Text("No hay productos disponibles")
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleProductCard(
    product: Product,
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (product.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%,.0f", product.price).replace(",", ".")} / ${product.unit}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FutronoCafe
                )
                Button(
                    onClick = { onAddToCart(product) },
                    colors = ButtonDefaults.buttonColors(containerColor = FutronoNaranja),
                    enabled = product.stock > 0
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleCartScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Tu carrito está vacío")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            cartItem = item,
                            onUpdateQuantity = onUpdateQuantity,
                            onRemoveItem = onRemoveItem
                        )
                    }
                }

                val total = cartItems.sumOf { it.totalPrice }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total:",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$${String.format("%,.0f", total).replace(",", ".")}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = FutronoCafe
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onCheckout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = FutronoNaranja)
                        ) {
                            Text("Confirmar Pago", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%,.0f", cartItem.product.price).replace(",", ".")} c/u",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onUpdateQuantity(cartItem.product.id, cartItem.quantity - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Reducir")
                }
                Text(
                    text = cartItem.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { onUpdateQuantity(cartItem.product.id, cartItem.quantity + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }
            IconButton(onClick = { onRemoveItem(cartItem.product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

