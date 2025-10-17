package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.R
import com.example.intento1app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndContactScreen(
    onBackClick: () -> Unit,
    onLocalAndScheduleClick: () -> Unit = {},
    onWhatsAppClick: () -> Unit = {},
    onReportErrorClick: () -> Unit = {},
    onTermsAndConditionsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ayuda y contacto",
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
        },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(FutronoFondo)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Local y horario
                HelpOptionCard(
                    icon = R.drawable.ic_store,
                    title = "Local y horario",
                    onClick = onLocalAndScheduleClick
                )
                
                // Contáctanos por WhatsApp
                HelpOptionCard(
                    icon = R.drawable.ic_whatsapp,
                    title = "Contáctanos por WhatsApp",
                    onClick = onWhatsAppClick
                )
                
                // Reporta un error en la app
                HelpOptionCard(
                    icon = R.drawable.ic_email,
                    title = "Reporta un error en la app",
                    onClick = onReportErrorClick
                )
                
                // Términos y condiciones
                HelpOptionCard(
                    icon = R.drawable.ic_document,
                    title = "Términos y condiciones",
                    onClick = onTermsAndConditionsClick
                )
            }
        }
    )
}

@Composable
private fun HelpOptionCard(
    icon: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = FutronoCafe,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ir a $title",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
