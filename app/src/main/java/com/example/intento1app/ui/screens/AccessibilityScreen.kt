package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.ui.components.*
import com.example.intento1app.ui.theme.*
import com.example.intento1app.viewmodel.AccessibilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityScreen(
    accessibilityViewModel: AccessibilityViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Inicializar accesibilidad
    LaunchedEffect(Unit) {
        accessibilityViewModel.initializeAccessibility(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ScalableTitleMedium(
                        text = "Configuración de Accesibilidad",
                        color = FutronoBlanco
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = FutronoBlanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FutronoCafe
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(FutronoFondo)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección de control de tamaño de texto
            AccessibilitySection(
                title = "Tamaño de Texto",
                description = "Ajusta el tamaño del texto para mejorar la legibilidad"
            ) {
                TextSizeControls(accessibilityViewModel)
            }
            
            // Sección de alto contraste
            AccessibilitySection(
                title = "Alto Contraste",
                description = "Activa el modo de alto contraste para mejor visibilidad"
            ) {
                HighContrastToggle(accessibilityViewModel)
            }
            
            // Sección de lector de pantalla
            AccessibilitySection(
                title = "Lector de Pantalla",
                description = "Activa funciones de lectura por voz"
            ) {
                ScreenReaderToggle(accessibilityViewModel)
            }
            
            // Sección de prueba para verificar que la tipografía escalable esté funcionando
            AccessibilitySection(
                title = "Prueba de Tipografía Escalable",
                description = "Este texto debería cambiar de tamaño cuando ajustes la configuración"
            ) {
                ScalableTestText(
                    text = "Texto de prueba que debería escalar: ${(accessibilityViewModel.textScaleFactor * 100).toInt()}%",
                    modifier = Modifier.padding(16.dp)
                )
                
                // Prueba adicional con MaterialTheme.typography
                Text(
                    text = "Texto con MaterialTheme: ${(accessibilityViewModel.textScaleFactor * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Sección de información
            AccessibilitySection(
                title = "Información de Accesibilidad",
                description = "Conoce más sobre las funciones disponibles"
            ) {
                AccessibilityInfo()
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AccessibilitySection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FutronoSuperficie
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ScalableTitleMedium(
                text = title,
                color = FutronoCafeOscuro,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ScalableBodyMedium(
                text = description,
                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            content()
        }
    }
}

@Composable
private fun TextSizeControls(accessibilityViewModel: AccessibilityViewModel) {
    val context = LocalContext.current
    
    Column {
        // Indicador del tamaño actual
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = FutronoNaranjaClaro
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScalableBodyMedium(
                    text = "Tamaño actual:",
                    color = FutronoCafeOscuro
                )
                
                ScalableTitleMedium(
                    text = "${(accessibilityViewModel.textScaleFactor * 100).toInt()}%",
                    color = FutronoCafeOscuro
                )
            }
        }
        
        // Botones de control
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
                         AccessibilityButton(
                 icon = null,
                 label = "A-",
                 description = "Disminuir tamaño de texto",
                 backgroundColor = FutronoCafe,
                 onClick = { accessibilityViewModel.decreaseTextSize(context) },
                 modifier = Modifier.weight(1f)
             )
            
            Spacer(modifier = Modifier.width(8.dp))
            
                                                         AccessibilityButton(
                     icon = null,
                     label = "A",
                     description = "Restablecer tamaño de texto",
                     backgroundColor = FutronoNaranja,
                     onClick = { accessibilityViewModel.resetTextSize(context) },
                     modifier = Modifier.weight(1f)
                 )
            
            Spacer(modifier = Modifier.width(8.dp))
            
                         AccessibilityButton(
                 icon = null,
                 label = "A+",
                 description = "Aumentar tamaño de texto",
                 backgroundColor = FutronoCafe,
                 onClick = { accessibilityViewModel.increaseTextSize(context) },
                 modifier = Modifier.weight(1f)
             )
        }
        
        // Información adicional
        ScalableBodySmall(
            text = "Los cambios se aplican inmediatamente a toda la aplicación",
            color = FutronoCafeOscuro.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun HighContrastToggle(accessibilityViewModel: AccessibilityViewModel) {
    val context = LocalContext.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ScalableBodyMedium(
                text = "Modo Alto Contraste",
                color = FutronoCafeOscuro
            )
            ScalableBodySmall(
                text = "Mejora el contraste entre elementos",
                color = FutronoCafeOscuro.copy(alpha = 0.6f)
            )
        }
        
        Switch(
            checked = accessibilityViewModel.isHighContrastEnabled,
            onCheckedChange = { accessibilityViewModel.toggleHighContrast(context) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = FutronoNaranja,
                checkedTrackColor = FutronoNaranjaClaro,
                uncheckedThumbColor = FutronoCafeClaro,
                uncheckedTrackColor = FutronoCafeClaro.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun ScreenReaderToggle(accessibilityViewModel: AccessibilityViewModel) {
    val context = LocalContext.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ScalableBodyMedium(
                text = "Lector de Pantalla",
                color = FutronoCafeOscuro
            )
            ScalableBodySmall(
                text = "Activa funciones de lectura por voz",
                color = FutronoCafeOscuro.copy(alpha = 0.6f)
            )
        }
        
        Switch(
            checked = accessibilityViewModel.isScreenReaderEnabled,
            onCheckedChange = { accessibilityViewModel.toggleScreenReader(context) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = FutronoNaranja,
                checkedTrackColor = FutronoNaranjaClaro,
                uncheckedThumbColor = FutronoCafeClaro,
                uncheckedTrackColor = FutronoCafeClaro.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun AccessibilityInfo() {
    Column {
        ScalableBodyMedium(
            text = "Funciones de Accesibilidad Disponibles:",
            color = FutronoCafeOscuro,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val features = listOf(
            "• Ajuste de tamaño de texto (80% - 140%)",
            "• Modo de alto contraste",
            "• Soporte para lectores de pantalla",
            "• Colores corporativos con contraste optimizado",
            "• Botones de tamaño adecuado para interacción táctil",
            "• Descripciones de contenido para tecnologías asistivas"
        )
        
        features.forEach { feature ->
            ScalableBodySmall(
                text = feature,
                color = FutronoCafeOscuro.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ScalableBodySmall(
            text = "Esta aplicación cumple con las recomendaciones de accesibilidad universal y Material Design para garantizar una experiencia inclusiva para todos los usuarios.",
            color = FutronoCafeOscuro.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
