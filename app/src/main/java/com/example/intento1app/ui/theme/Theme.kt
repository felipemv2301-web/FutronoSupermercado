package com.example.intento1app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores claro personalizado para Futrono
// Usa la paleta corporativa de café y naranja
private val FutronoLightColorScheme = lightColorScheme(
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
    error = FutronoError,
    onError = FutronoFondo
)

// Esquema de colores oscuro personalizado para Futrono
// Adapta la paleta corporativa para modo oscuro
private val FutronoDarkColorScheme = darkColorScheme(
    primary = FutronoCafeClaro,
    onPrimary = FutronoFondo,
    primaryContainer = FutronoCafe,
    onPrimaryContainer = FutronoFondo,
    secondary = FutronoNaranjaClaro,
    onSecondary = FutronoFondo,
    secondaryContainer = FutronoNaranja,
    onSecondaryContainer = FutronoFondo,
    tertiary = FutronoNaranja,
    onTertiary = FutronoFondo,
    tertiaryContainer = FutronoNaranjaOscuro,
    onTertiaryContainer = FutronoFondo,
    background = FutronoCafeOscuro,
    onBackground = FutronoFondo,
    surface = FutronoCafe,
    onSurface = FutronoFondo,
    error = FutronoError,
    onError = FutronoFondo
)

@Composable
fun FutronoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> FutronoDarkColorScheme
        else -> FutronoLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Usar la API moderna para configurar la barra de estado
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}