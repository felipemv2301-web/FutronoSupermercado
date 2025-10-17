package com.example.intento1app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intento1app.ui.theme.*
import com.example.intento1app.viewmodel.AccessibilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityButtons(
    accessibilityViewModel: AccessibilityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Inicializar accesibilidad al montar el componente
    LaunchedEffect(Unit) {
        accessibilityViewModel.initializeAccessibility(context)
    }
    
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FutronoSuperficie,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la sección
            Text(
                text = "Accesibilidad",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = FutronoCafeOscuro,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Botones de control de tamaño de texto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón para disminuir texto
                AccessibilityButton(
                    icon = null,
                    label = "A-",
                    description = "Disminuir tamaño de texto",
                    backgroundColor = FutronoCafe,
                    onClick = { accessibilityViewModel.decreaseTextSize(context) },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Botón para restablecer texto
                AccessibilityButton(
                    icon = null,
                    label = "A",
                    description = "Restablecer tamaño de texto",
                    backgroundColor = FutronoNaranja,
                    onClick = { accessibilityViewModel.resetTextSize(context) },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Botón para aumentar texto
                AccessibilityButton(
                    icon = null,
                    label = "A+",
                    description = "Aumentar tamaño de texto",
                    backgroundColor = FutronoCafe,
                    onClick = { accessibilityViewModel.increaseTextSize(context) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Indicador del tamaño actual
            Text(
                text = "Tamaño actual: ${(accessibilityViewModel.textScaleFactor * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = FutronoCafeOscuro,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botones adicionales de accesibilidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón de alto contraste
                AccessibilityToggleButton(
                    icon = null,
                    label = "Alto Contraste",
                    description = "Activar modo de alto contraste",
                    isEnabled = accessibilityViewModel.isHighContrastEnabled,
                    onToggle = { accessibilityViewModel.toggleHighContrast(context) },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Botón de lector de pantalla
                AccessibilityToggleButton(
                    icon = null,
                    label = "Lector",
                    description = "Activar lector de pantalla",
                    isEnabled = accessibilityViewModel.isScreenReaderEnabled,
                    onToggle = { accessibilityViewModel.toggleScreenReader(context) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AccessibilityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    label: String,
    description: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = FutronoFondo
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun AccessibilityToggleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    label: String,
    description: String,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isEnabled) FutronoNaranja else FutronoCafeClaro
    val contentColor = FutronoFondo
    
    Button(
        onClick = onToggle,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

// Componente flotante para acceso rápido a accesibilidad
@Composable
fun FloatingAccessibilityButton(
    accessibilityViewModel: AccessibilityViewModel,
    onAccessibilityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onAccessibilityClick,
        modifier = modifier,
        containerColor = FutronoNaranja,
        contentColor = FutronoFondo,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Configuración de accesibilidad",
            modifier = Modifier.size(24.dp)
        )
    }
}
