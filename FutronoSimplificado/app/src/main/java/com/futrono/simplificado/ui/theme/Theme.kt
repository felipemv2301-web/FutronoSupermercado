package com.futrono.simplificado.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FutronoLightColorScheme = lightColorScheme(
    primary = FutronoCafe,
    onPrimary = FutronoFondo,
    primaryContainer = FutronoCafeClaro,
    secondary = FutronoNaranja,
    onSecondary = FutronoFondo,
    background = FutronoFondo,
    onBackground = FutronoCafeOscuro,
    surface = FutronoSuperficie,
    onSurface = FutronoCafeOscuro,
    error = FutronoError
)

private val FutronoDarkColorScheme = darkColorScheme(
    primary = FutronoCafeClaro,
    onPrimary = FutronoFondo,
    primaryContainer = FutronoCafe,
    secondary = FutronoNaranjaClaro,
    onSecondary = FutronoFondo,
    background = FutronoCafeOscuro,
    onBackground = FutronoFondo,
    surface = FutronoCafe,
    onSurface = FutronoFondo,
    error = FutronoError
)

@Composable
fun FutronoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

