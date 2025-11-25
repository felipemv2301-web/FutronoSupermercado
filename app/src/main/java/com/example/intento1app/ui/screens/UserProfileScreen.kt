package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.User
import com.example.intento1app.ui.theme.*
import com.example.intento1app.viewmodel.AccessibilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    currentUser: User,
    accessibilityViewModel: AccessibilityViewModel,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onAccessibilityClick: () -> Unit,
    onMyDataClick: () -> Unit = {},
    onPaymentMethodsClick: () -> Unit = {},
    onBankDataClick: () -> Unit = {},
    onMyBankDetailsClick: () -> Unit = {},
    onDeleteAccountClick: (String) -> Unit = {},
    onMyOrdersClick: () -> Unit = {},
    onHelpContactClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onDevolutionClick: () -> Unit ={}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showFirstDeleteDialog by remember { mutableStateOf(false) }
    var showSecondDeleteDialog by remember { mutableStateOf(false) }
    var isAgreementChecked by remember { mutableStateOf(false) }
    var isDeletingAccount by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var showPasswordError by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Perfil de ${currentUser.nombre}",
                        style = MaterialTheme.typography.titleMedium.copy(
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
        ) {

            // Información personal
            Text(
                text = "Información personal",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafe,
                modifier = Modifier.padding(16.dp, 15.dp, 16.dp, 8.dp)
            )
            
            // Mis datos
            ProfileOptionCard(
                iconVector = Icons.Default.Person,
                title = "Mis datos",
                onClick = onMyDataClick
            )
            

            
            // Mis datos bancarios
            ProfileOptionCard(
                iconVector = Icons.Default.AccountBalance,
                title = "Mis datos bancarios",
                onClick = onMyBankDetailsClick
            )
            
            // Eliminar cuenta
            ProfileOptionCard(
                iconVector = Icons.Default.Delete,
                title = "Eliminar cuenta",
                onClick = { showFirstDeleteDialog = true },
                isDelete = true
            )
            
            // Pedidos
            Text(
                text = "Pedidos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafe,
                modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp)
            )
            
            // Mis pedidos
            ProfileOptionCard(
                iconVector = Icons.Default.ShoppingBag,
                title = "Mis pedidos",
                onClick = onMyOrdersClick
            )
            
            // Centro de ayuda
            Text(
                text = "Centro de ayuda",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafe,
                modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp)
            )
            
            // Ayuda y contacto
            ProfileOptionCard(
                iconVector = Icons.Default.HeadsetMic,
                title = "Ayuda y contacto",
                onClick = onHelpContactClick
            )
            
            // Cerrar sesión
            ProfileOptionCard(
                iconVector = Icons.Default.Logout,
                title = "Cerrar sesión",
                onClick = { showLogoutDialog = true },
                isLogout = true
            )
        }
    }
    
    // Diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = FutronoBlanco, // Color de fondo del diálogo
            titleContentColor = FutronoCafeOscuro, // Color del título
            textContentColor = FutronoCafeOscuro, // Color del texto
            title = {
                Text(
                    text = "Cerrar Sesión",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text("¿Estás seguro de que quieres cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoError
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Primer diálogo de confirmación para eliminar cuenta
    if (showFirstDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showFirstDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = FutronoError,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Eliminar Cuenta",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoError
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "¿Estás seguro de que quieres eliminar tu cuenta?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Esta acción es irreversible y eliminará todos tus datos permanentemente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.8f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFirstDeleteDialog = false
                        showSecondDeleteDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoNaranja
                    )
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showFirstDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Segundo diálogo con advertencia y checkbox
    if (showSecondDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showSecondDeleteDialog = false
                isAgreementChecked = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = FutronoError,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Advertencia Importante",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoError
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Por políticas del sistema, después de eliminar tu cuenta NO podrás crear una nueva cuenta con los mismos datos por un período mínimo de 1 año.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Esto significa que:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoError
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "• No podrás usar el mismo email",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• No podrás usar el mismo teléfono",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• No podrás usar el mismo RUT",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• Deberás esperar al menos 1 año",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Campo de contraseña para reautenticación
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            showPasswordError = false
                        },
                        label = { Text("Contraseña") },
                        placeholder = { Text("Ingresa tu contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = showPasswordError,
                        supportingText = {
                            if (showPasswordError) {
                                Text(
                                    text = "La contraseña es requerida",
                                    color = FutronoError
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FutronoCafe,
                            unfocusedBorderColor = FutronoCafeOscuro.copy(alpha = 0.5f),
                            errorBorderColor = FutronoError
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isAgreementChecked,
                            onCheckedChange = { isAgreementChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = FutronoError
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Estoy de acuerdo con esto",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (password.isBlank()) {
                            showPasswordError = true
                        } else if (isAgreementChecked) {
                            isDeletingAccount = true
                            showSecondDeleteDialog = false
                            onDeleteAccountClick(password)
                            // Limpiar campos después de cerrar
                            password = ""
                            isAgreementChecked = false
                            showPasswordError = false
                        }
                    },
                    enabled = isAgreementChecked && password.isNotBlank() && !isDeletingAccount,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoError
                    )
                ) {
                    if (isDeletingAccount) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Eliminar Cuenta")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSecondDeleteDialog = false
                        isAgreementChecked = false
                        password = ""
                        showPasswordError = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ProfileOptionCard(
    iconVector: ImageVector,
    title: String,
    onClick: () -> Unit,
    isLogout: Boolean = false,
    isDelete: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
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
                tint = when {
                    isLogout -> FutronoError
                    isDelete -> FutronoError
                    else -> FutronoCafe
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = when {
                    isLogout -> FutronoError
                    isDelete -> FutronoError
                    else -> FutronoCafeOscuro
                },
                modifier = Modifier.weight(1f)
            )
            if (!isLogout) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFEEEEEE)
                )
            }
        }
    }
}


