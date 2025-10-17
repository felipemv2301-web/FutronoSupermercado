package com.example.intento1app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.intento1app.viewmodel.AccessibilityViewModel

/**
 * Tema accesible que se adapta dinámicamente a las preferencias del usuario
 */
@Composable
fun AccessibleFutronoTheme(
    accessibilityViewModel: AccessibilityViewModel,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    // Inicializar accesibilidad
    LaunchedEffect(Unit) {
        accessibilityViewModel.initializeAccessibility(context)
    }
    
    // Crear tipografía escalable basada en las preferencias del usuario
    val scalableTypography = remember(accessibilityViewModel.textScaleFactor) {
        ScalableTypography.createScalableTypography(accessibilityViewModel.textScaleFactor)
    }
    
    // Aplicar el tema con tipografía escalable
    CompositionLocalProvider(LocalTypography provides scalableTypography) {
        MaterialTheme(
            colorScheme = if (accessibilityViewModel.isHighContrastEnabled) {
                // Esquema de alto contraste
                createHighContrastColorScheme()
            } else {
                // Esquema normal
                createNormalColorScheme()
            },
            typography = scalableTypography,
            content = content
        )
    }
}

/**
 * Crea un esquema de colores de alto contraste para mejor accesibilidad
 */
@Composable
private fun createHighContrastColorScheme(): androidx.compose.material3.ColorScheme {
    return androidx.compose.material3.ColorScheme(
        primary = FutronoCafeOscuro,
        onPrimary = FutronoFondo,
        primaryContainer = FutronoCafe,
        onPrimaryContainer = FutronoFondo,
        secondary = FutronoNaranjaOscuro,
        onSecondary = FutronoFondo,
        secondaryContainer = FutronoNaranja,
        onSecondaryContainer = FutronoFondo,
        tertiary = FutronoNaranjaOscuro,
        onTertiary = FutronoFondo,
        tertiaryContainer = FutronoNaranja,
        onTertiaryContainer = FutronoFondo,
        background = FutronoFondo,
        onBackground = FutronoCafeOscuro,
        surface = FutronoSuperficie,
        onSurface = FutronoCafeOscuro,
        surfaceBright = FutronoSuperficie,
        surfaceDim = FutronoSuperficie,
        surfaceContainer = FutronoSuperficie,
        surfaceContainerHigh = FutronoSuperficie,
        surfaceContainerHighest = FutronoSuperficie,
        surfaceContainerLow = FutronoSuperficie,
        surfaceContainerLowest = FutronoSuperficie,
        error = FutronoError,
        onError = FutronoFondo,
        errorContainer = FutronoError.copy(alpha = 0.8f),
        onErrorContainer = FutronoFondo,
        surfaceVariant = FutronoSuperficie,
        onSurfaceVariant = FutronoCafeOscuro,
        outline = FutronoCafeOscuro,
        outlineVariant = FutronoCafe,
        scrim = FutronoCafeOscuro.copy(alpha = 0.32f),
        inverseSurface = FutronoCafeOscuro,
        inverseOnSurface = FutronoFondo,
        inversePrimary = FutronoNaranja,
        surfaceTint = FutronoCafe
    )
}

/**
 * Crea un esquema de colores normal
 */
@Composable
private fun createNormalColorScheme(): androidx.compose.material3.ColorScheme {
    return androidx.compose.material3.ColorScheme(
        primary = FutronoCafe,
        onPrimary = FutronoFondo,
        primaryContainer = FutronoCafeClaro,
        onPrimaryContainer = FutronoFondo,
        secondary = FutronoNaranja,
        onSecondary = FutronoFondo,
        secondaryContainer = FutronoNaranjaClaro,
        onSecondaryContainer = FutronoCafeOscuro,
        tertiary = FutronoNaranjaOscuro,
        onTertiary = FutronoFondo,
        tertiaryContainer = FutronoNaranjaClaro,
        onTertiaryContainer = FutronoCafeOscuro,
        background = FutronoFondo,
        onBackground = FutronoCafeOscuro,
        surface = FutronoSuperficie,
        onSurface = FutronoCafeOscuro,
        surfaceBright = FutronoSuperficie,
        surfaceDim = FutronoSuperficie,
        surfaceContainer = FutronoSuperficie,
        surfaceContainerHigh = FutronoSuperficie,
        surfaceContainerHighest = FutronoSuperficie,
        surfaceContainerLow = FutronoSuperficie,
        surfaceContainerLowest = FutronoSuperficie,
        error = FutronoError,
        onError = FutronoFondo,
        errorContainer = FutronoError.copy(alpha = 0.8f),
        onErrorContainer = FutronoFondo,
        surfaceVariant = FutronoSuperficie,
        onSurfaceVariant = FutronoCafeOscuro,
        outline = FutronoCafe,
        outlineVariant = FutronoCafeClaro,
        scrim = FutronoCafe.copy(alpha = 0.32f),
        inverseSurface = FutronoCafeOscuro,
        inverseOnSurface = FutronoFondo,
        inversePrimary = FutronoNaranja,
        surfaceTint = FutronoCafe
    )
}

/**
 * LocalComposition para la tipografía escalable
 */
val LocalTypography = androidx.compose.runtime.staticCompositionLocalOf<Typography> {
    ScalableTypography.default
}
