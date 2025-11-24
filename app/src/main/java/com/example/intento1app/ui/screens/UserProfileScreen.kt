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
    onDeleteAccountClick: () -> Unit = {},
    onMyOrdersClick: () -> Unit = {},
    onHelpContactClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onDevolutionClick: () -> Unit ={}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
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
                onClick = onDeleteAccountClick
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
}

@Composable
private fun ProfileOptionCard(
    iconVector: ImageVector,
    title: String,
    onClick: () -> Unit,
    isLogout: Boolean = false,
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
                tint = if (isLogout) FutronoError else FutronoCafe
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isLogout) FutronoError else FutronoCafeOscuro,
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


