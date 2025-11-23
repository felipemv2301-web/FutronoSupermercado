package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudSoporte(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    Onclick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reclamo",
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
                .background(FutronoFondo) // Asegúrate de tener definido este color
                .verticalScroll(rememberScrollState())
        ) {
            // Título de la sección (puedes cambiarlo según lo que vayas a poner)
            Text(
                text = "Formulario de Devolución",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )

            // AQUÍ: Agrega tus campos de texto, selectores de motivo, etc.

            Spacer(modifier = Modifier.height(32.dp))
        }
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