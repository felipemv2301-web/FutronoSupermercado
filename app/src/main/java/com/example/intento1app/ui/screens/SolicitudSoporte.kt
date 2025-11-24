package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.FirebaseClaim
import com.example.intento1app.data.models.User
import com.example.intento1app.data.services.FirebaseService
import com.example.intento1app.ui.theme.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudSoporte(
    currentUser: User?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSuccess: () -> Unit = {}
) {
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()
    
    // Estados para cargar órdenes
    var isLoadingOrders by remember { mutableStateOf(false) }
    var orderNumbers by remember { mutableStateOf<List<String>>(emptyList()) }
    var isOrderDropdownExpanded by remember { mutableStateOf(false) }
    
    // Estados del formulario
    var orderNumber by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf(currentUser?.telefono ?: "") }
    var observation by remember { mutableStateOf("") }
    
    // Estados de validación y UI
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Errores de validación
    var orderNumberError by remember { mutableStateOf("") }
    var problemError by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf("") }
    
    // Cargar órdenes del usuario cuando se monta el componente
    LaunchedEffect(currentUser?.id) {
        if (currentUser != null && currentUser.id != "guest") {
            isLoadingOrders = true
            val result = firebaseService.getUserPurchasesFromPayments(currentUser.id)
            
            result.fold(
                onSuccess = { purchases ->
                    // Extraer números de orden únicos y ordenarlos (más recientes primero)
                    orderNumbers = purchases
                        .mapNotNull { it.orderNumber }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .sortedDescending() // Ordenar descendente (más recientes primero)
                    isLoadingOrders = false
                },
                onFailure = { error ->
                    isLoadingOrders = false
                    errorMessage = "Error al cargar tus pedidos: ${error.message}"
                    showErrorDialog = true
                }
            )
        } else {
            isLoadingOrders = false
        }
    }
    
    fun validateForm(): Boolean {
        var isValid = true
        
        orderNumberError = ""
        problemError = ""
        phoneNumberError = ""
        
        if (orderNumber.isBlank()) {
            orderNumberError = "El número de orden es requerido"
            isValid = false
        }
        
        if (problem.isBlank()) {
            problemError = "Debes describir el problema"
            isValid = false
        }
        
        if (phoneNumber.isBlank()) {
            phoneNumberError = "El número de teléfono es requerido"
            isValid = false
        } else if (phoneNumber.length < 8) {
            phoneNumberError = "El número de teléfono debe tener al menos 8 dígitos"
            isValid = false
        }
        
        return isValid
    }
    
    fun submitClaim() {
        if (!validateForm()) {
            return
        }
        
        if (currentUser == null || currentUser.id == "guest") {
            errorMessage = "Debes iniciar sesión para realizar un reclamo"
            showErrorDialog = true
            return
        }
        
        isLoading = true
        
        val claim = FirebaseClaim(
            orderNumber = orderNumber.trim(),
            problem = problem.trim(),
            phoneNumber = phoneNumber.trim(),
            observation = observation.trim(),
            userId = currentUser.id,
            userEmail = currentUser.email,
            status = listOf("Reclamo pendiente")
        )
        
        coroutineScope.launch {
            val result = firebaseService.saveClaim(claim)
            
            result.fold(
                onSuccess = {
                    isLoading = false
                    showSuccessDialog = true
                    // Limpiar formulario
                    orderNumber = ""
                    problem = ""
                    observation = ""
                },
                onFailure = { error ->
                    isLoading = false
                    errorMessage = error.message ?: "Error al enviar el reclamo. Por favor, intenta de nuevo."
                    showErrorDialog = true
                }
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reclamo de Pedido",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = "Formulario de Reclamo",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Completa el siguiente formulario para realizar un reclamo sobre tu pedido",
                style = MaterialTheme.typography.bodyMedium,
                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo: N° de Orden (Lista desplegable)
            if (isLoadingOrders) {
                // Mostrar indicador de carga mientras se cargan las órdenes
                OutlinedTextField(
                    value = "Cargando tus pedidos...",
                    onValueChange = { },
                    label = { Text("N° de Orden *") },
                    leadingIcon = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = FutronoCafe,
                            strokeWidth = 2.dp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = FutronoCafeOscuro
                    )
                )
            } else if (orderNumbers.isEmpty()) {
                // Si no hay órdenes, mostrar mensaje
                OutlinedTextField(
                    value = "No tienes pedidos disponibles",
                    onValueChange = { },
                    label = { Text("N° de Orden *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = FutronoCafe
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = FutronoCafeOscuro
                    ),
                    supportingText = {
                        Text(
                            "No tienes pedidos para reclamar. Realiza una compra primero.",
                            color = FutronoCafeOscuro.copy(alpha = 0.7f)
                        )
                    }
                )
            } else {
                // Lista desplegable con las órdenes
                ExposedDropdownMenuBox(
                    expanded = isOrderDropdownExpanded,
                    onExpandedChange = { isOrderDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = orderNumber,
                        onValueChange = { 
                            orderNumber = it
                            orderNumberError = ""
                        },
                        readOnly = true,
                        label = { Text("N° de Orden *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = FutronoCafe
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isOrderDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (orderNumberError.isEmpty()) FutronoCafe else Color.Red,
                            unfocusedBorderColor = if (orderNumberError.isEmpty()) MaterialTheme.colorScheme.outline else Color.Red,
                            focusedLabelColor = FutronoCafe,
                            unfocusedLabelColor = FutronoCafeOscuro
                        ),
                        isError = orderNumberError.isNotEmpty(),
                        supportingText = if (orderNumberError.isNotEmpty()) {
                            { Text(orderNumberError, color = Color.Red) }
                        } else {
                            { Text("Selecciona un pedido de la lista", color = FutronoCafeOscuro.copy(alpha = 0.7f)) }
                        }
                    )
                    
                    ExposedDropdownMenu(
                        expanded = isOrderDropdownExpanded,
                        onDismissRequest = { isOrderDropdownExpanded = false }
                    ) {
                        orderNumbers.forEach { orderNum ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = orderNum,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    orderNumber = orderNum
                                    isOrderDropdownExpanded = false
                                    orderNumberError = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            // Campo: Problema
            OutlinedTextField(
                value = problem,
                onValueChange = { 
                    problem = it
                    problemError = ""
                },
                label = { Text("Problema *") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = FutronoCafe
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (problemError.isEmpty()) FutronoCafe else Color.Red,
                    unfocusedBorderColor = if (problemError.isEmpty()) MaterialTheme.colorScheme.outline else Color.Red,
                    focusedLabelColor = FutronoCafe,
                    unfocusedLabelColor = FutronoCafeOscuro
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = problemError.isNotEmpty(),
                supportingText = if (problemError.isNotEmpty()) {
                    { Text(problemError, color = Color.Red) }
                } else {
                    { Text("Describe el problema con tu pedido", color = FutronoCafeOscuro.copy(alpha = 0.7f)) }
                }
            )
            
            // Campo: Número de Teléfono
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { 
                    phoneNumber = it
                    phoneNumberError = ""
                },
                label = { Text("Número de Teléfono *") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = FutronoCafe
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (phoneNumberError.isEmpty()) FutronoCafe else Color.Red,
                    unfocusedBorderColor = if (phoneNumberError.isEmpty()) MaterialTheme.colorScheme.outline else Color.Red,
                    focusedLabelColor = FutronoCafe,
                    unfocusedLabelColor = FutronoCafeOscuro
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneNumberError.isNotEmpty(),
                supportingText = if (phoneNumberError.isNotEmpty()) {
                    { Text(phoneNumberError, color = Color.Red) }
                } else null
            )
            
            // Campo: Observación
            OutlinedTextField(
                value = observation,
                onValueChange = { observation = it },
                label = { Text("Observación (opcional)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = FutronoCafe
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FutronoCafe,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = FutronoCafe,
                    unfocusedLabelColor = FutronoCafeOscuro
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                supportingText = {
                    Text("Información adicional que pueda ayudar a resolver tu reclamo", color = FutronoCafeOscuro.copy(alpha = 0.7f))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de envío
            Button(
                onClick = { submitClaim() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FutronoCafeOscuro
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isLoading) "Enviando..." else "Enviar Reclamo",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "* Campos obligatorios",
                style = MaterialTheme.typography.bodySmall,
                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
    
    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onSuccess()
                onBackClick()
            },
            title = {
                Text(
                    "Reclamo Enviado",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text("Tu reclamo ha sido enviado exitosamente. Nos pondremos en contacto contigo pronto.")
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showSuccessDialog = false
                        onSuccess()
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoCafe
                    )
                ) {
                    Text("Aceptar")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    "Error",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Red
                )
            },
            text = {
                Text(errorMessage)
            },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoCafe
                    )
                ) {
                    Text("Aceptar")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// He dejado este componente helper por si quieres reutilizar el estilo de tarjeta
// para mostrar datos fijos (como un ID de reclamo o fecha).
@Composable
private fun DataCard(
    title: String,
    value: String,
    iconVector: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = FutronoCafe
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoCafeOscuro
                )
            }
        }
    }
}