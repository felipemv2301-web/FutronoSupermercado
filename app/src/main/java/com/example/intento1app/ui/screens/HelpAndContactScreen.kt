package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.content.ClipData
import android.content.ClipboardManager
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
    onClaimClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showWhatsAppDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val whatsAppNumber = "+56972630846"
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ayuda y contacto",
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
                            tint = FutronoFondo,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FutronoCafe,
                    titleContentColor = FutronoBlanco
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
                    iconVector = Icons.Default.Store,
                    title = "Local y horario",
                    onClick = { showScheduleDialog = true }
                )
                
                // Contáctanos por WhatsApp
                HelpOptionCard(
                    iconVector = Icons.Default.Message,
                    title = "Contáctanos por WhatsApp",
                    onClick = { showWhatsAppDialog = true }
                )
                
                // Términos y condiciones
                HelpOptionCard(
                    iconVector = Icons.Default.Description,
                    title = "Términos y condiciones",
                    onClick = onTermsAndConditionsClick
                )
                HelpOptionCard(
                    iconVector = Icons.Default.ReportProblem,
                    title = "Reclamo de pedido",
                    onClick = onClaimClick
                )
            }
        }
    )
    
    // Diálogo modal de WhatsApp
    if (showWhatsAppDialog) {
        AlertDialog(
            onDismissRequest = { showWhatsAppDialog = false },
            containerColor = FutronoBlanco,
            titleContentColor = FutronoCafeOscuro,
            textContentColor = FutronoCafeOscuro,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Número de WhatsApp",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(
                        onClick = { showWhatsAppDialog = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = FutronoCafeOscuro
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = whatsAppNumber,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoCafe,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Copia el número para contactarnos por WhatsApp",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FutronoCafeOscuro.copy(alpha = 0.7f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Copiar al portapapeles
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("WhatsApp Number", whatsAppNumber)
                        clipboard.setPrimaryClip(clip)
                        
                        // Mostrar mensaje de confirmación
                        android.widget.Toast.makeText(
                            context,
                            "Número copiado al portapapeles",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FutronoCafe
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copiar número",
                        color = FutronoBlanco
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWhatsAppDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
    
    // Diálogo modal de Horarios
    if (showScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            containerColor = FutronoBlanco,
            titleContentColor = FutronoCafeOscuro,
            textContentColor = FutronoCafeOscuro,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Horarios de Atención",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(
                        onClick = { showScheduleDialog = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = FutronoCafeOscuro
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Lunes a Viernes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Lunes a Sábado:",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = FutronoCafeOscuro,
                            modifier = Modifier.width(120.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "9:00 AM - 1:00 PM",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FutronoCafeOscuro
                            )
                            Text(
                                text = "3:00 PM - 7:30 PM",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FutronoCafeOscuro
                            )
                        }
                    }

                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Sábados y Domingos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Domingo:",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = FutronoCafeOscuro,
                            modifier = Modifier.width(120.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerrado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FutronoCafeOscuro.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showScheduleDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun HelpOptionCard(
    iconVector: ImageVector,
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
                    imageVector = iconVector,
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
