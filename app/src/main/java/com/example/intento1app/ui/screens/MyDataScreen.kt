package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.R
import com.example.intento1app.data.models.User
import com.example.intento1app.data.models.FirebaseUser
import com.example.intento1app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDataScreen(
    currentUser: User,
    firebaseUser: FirebaseUser?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Mis Datos",
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
                .verticalScroll(rememberScrollState())
        ) {
            // Información personal
            Text(
                text = "Información Personal",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )
            
            // Datos del usuario
            DataCard(
                title = "Nombre",
                value = currentUser.nombre,
                icon = R.drawable.ic_person
            )
            
            DataCard(
                title = "Apellido",
                value = currentUser.apellido,
                icon = R.drawable.ic_person
            )
            
            DataCard(
                title = "RUT",
                value = currentUser.rut.ifEmpty { "No registrado" },
                icon = R.drawable.ic_credit_card
            )
            
            DataCard(
                title = "Teléfono",
                value = firebaseUser?.phoneNumber?.ifEmpty { currentUser.telefono } ?: currentUser.telefono.ifEmpty { "No registrado" },
                icon = R.drawable.ic_phone
            )
            
            DataCard(
                title = "Email",
                value = currentUser.email,
                icon = R.drawable.ic_email
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Información de la cuenta
            Text(
                text = "Información de la Cuenta",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )
            
            firebaseUser?.let { user ->
                DataCard(
                    title = "Email Verificado",
                    value = if (user.isEmailVerified) "Sí" else "No",
                    icon = R.drawable.ic_check_circle
                )
                
                DataCard(
                    title = "Cuenta Activa",
                    value = if (user.isActive) "Sí" else "No",
                    icon = R.drawable.ic_check_circle
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DataCard(
    title: String,
    value: String,
    icon: Int,
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
                painter = painterResource(id = icon),
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
