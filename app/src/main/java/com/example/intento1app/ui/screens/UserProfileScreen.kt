package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.R
import com.example.intento1app.data.models.User
import com.example.intento1app.data.models.FirebasePurchase
import com.example.intento1app.ui.theme.*
import com.example.intento1app.viewmodel.AccessibilityViewModel
import com.example.intento1app.viewmodel.PaymentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    currentUser: User,
    accessibilityViewModel: AccessibilityViewModel,
    paymentViewModel: PaymentViewModel,
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
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Perfil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoFondo
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Volver",
                            tint = FutronoFondo,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FutronoCafe,
                    titleContentColor = FutronoFondo
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
            // Saludo con nombre de usuario
            Text(
                text = "¡Hola, ${currentUser.nombre}!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )
            
            // Información personal
            Text(
                text = "Información personal",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro,
                modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp)
            )
            
            // Mis datos
            ProfileOptionCard(
                icon = R.drawable.ic_person,
                title = "Mis datos",
                onClick = onMyDataClick
            )
            
            // Mis medios de pago
            ProfileOptionCard(
                icon = R.drawable.ic_credit_card,
                title = "Mis medios de pago",
                onClick = onPaymentMethodsClick
            )
            
            // Mis datos bancarios
            ProfileOptionCard(
                icon = R.drawable.ic_bank,
                title = "Mis datos bancarios",
                onClick = onMyBankDetailsClick
            )
            
            // Eliminar cuenta
            ProfileOptionCard(
                icon = R.drawable.ic_delete,
                title = "Eliminar cuenta",
                onClick = onDeleteAccountClick
            )
            
            // Pedidos
            Text(
                text = "Pedidos",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro,
                modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp)
            )
            
            // Mis pedidos
            ProfileOptionCard(
                icon = R.drawable.ic_shopping_bag,
                title = "Mis pedidos",
                onClick = onMyOrdersClick
            )
            
            // Centro de ayuda
            Text(
                text = "Centro de ayuda",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro,
                modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp)
            )
            
            // Ayuda y contacto
            ProfileOptionCard(
                icon = R.drawable.ic_headphones,
                title = "Ayuda y contacto",
                onClick = onHelpContactClick
            )
            
            // Cerrar sesión
            ProfileOptionCard(
                icon = R.drawable.ic_logout,
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
    icon: Int,
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
                painter = painterResource(id = icon),
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
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFEEEEEE)
                )
            }
        }
    }
}


