package com.example.intento1app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Sistema de tipografía escalable para accesibilidad
 * Permite ajustar dinámicamente el tamaño del texto según las preferencias del usuario
 */
object ScalableTypography {
    
    // Factores de escala predefinidos
    const val SCALE_SMALL = 0.8f
    const val SCALE_NORMAL = 1.0f
    const val SCALE_LARGE = 1.2f
    const val SCALE_EXTRA_LARGE = 1.4f
    
    /**
     * Crea una tipografía escalable basada en el factor de escala proporcionado
     * @param scaleFactor Factor de escala (0.8f a 1.4f)
     * @return Typography escalada
     */
    fun createScalableTypography(scaleFactor: Float): Typography {
        val clampedScale = scaleFactor.coerceIn(SCALE_SMALL, SCALE_EXTRA_LARGE)
        
        return Typography(
            // Títulos grandes
            displayLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (57 * clampedScale).sp,
                lineHeight = (64 * clampedScale).sp,
                letterSpacing = (-0.25 * clampedScale).sp
            ),
            displayMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (45 * clampedScale).sp,
                lineHeight = (52 * clampedScale).sp,
                letterSpacing = 0.sp
            ),
            displaySmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (36 * clampedScale).sp,
                lineHeight = (44 * clampedScale).sp,
                letterSpacing = 0.sp
            ),
            
            // Títulos de encabezado
            headlineLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (32 * clampedScale).sp,
                lineHeight = (40 * clampedScale).sp,
                letterSpacing = 0.sp
            ),
            headlineMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (28 * clampedScale).sp,
                lineHeight = (36 * clampedScale).sp,
                letterSpacing = 0.sp
            ),
            headlineSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (24 * clampedScale).sp,
                lineHeight = (32 * clampedScale).sp,
                letterSpacing = 0.sp
            ),
            
            // Títulos de sección
            titleLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (22 * clampedScale).sp,
                lineHeight = (28 * clampedScale).sp,
                letterSpacing = 0.sp
            ),
            titleMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = (16 * clampedScale).sp,
                lineHeight = (24 * clampedScale).sp,
                letterSpacing = 0.15.sp
            ),
            titleSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = (14 * clampedScale).sp,
                lineHeight = (20 * clampedScale).sp,
                letterSpacing = 0.1.sp
            ),
            
            // Texto del cuerpo
            bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (16 * clampedScale).sp,
                lineHeight = (24 * clampedScale).sp,
                letterSpacing = 0.5.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (14 * clampedScale).sp,
                lineHeight = (20 * clampedScale).sp,
                letterSpacing = 0.25.sp
            ),
            bodySmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = (12 * clampedScale).sp,
                lineHeight = (16 * clampedScale).sp,
                letterSpacing = 0.4.sp
            ),
            
            // Etiquetas
            labelLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = (14 * clampedScale).sp,
                lineHeight = (20 * clampedScale).sp,
                letterSpacing = 0.1.sp
            ),
            labelMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = (12 * clampedScale).sp,
                lineHeight = (16 * clampedScale).sp,
                letterSpacing = 0.5.sp
            ),
            labelSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = (11 * clampedScale).sp,
                lineHeight = (16 * clampedScale).sp,
                letterSpacing = 0.5.sp
            )
        )
    }
    
    /**
     * Tipografía por defecto (escala normal)
     */
    val default: Typography = createScalableTypography(SCALE_NORMAL)
    
    /**
     * Tipografía pequeña para usuarios que prefieren texto más pequeño
     */
    val small: Typography = createScalableTypography(SCALE_SMALL)
    
    /**
     * Tipografía grande para usuarios que necesitan texto más grande
     */
    val large: Typography = createScalableTypography(SCALE_LARGE)
    
    /**
     * Tipografía extra grande para máxima accesibilidad
     */
    val extraLarge: Typography = createScalableTypography(SCALE_EXTRA_LARGE)
}
