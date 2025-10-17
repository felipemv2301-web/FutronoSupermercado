package com.example.intento1app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.intento1app.ui.theme.LocalTypography

/**
 * Componente de texto escalable que respeta las preferencias de accesibilidad
 * Utiliza la tipografía escalable del tema actual
 */
@Composable
fun ScalableText(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: androidx.compose.ui.text.font.FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: androidx.compose.ui.text.font.FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean? = null,
    overflow: TextOverflow? = null,
    maxLines: Int? = null,
    minLines: Int? = null,
    onTextLayout: ((androidx.compose.ui.text.TextLayoutResult) -> Unit)? = null,
    style: TextStyle = TextStyle.Default
) {
    val currentTypography = LocalTypography.current
    
    Text(
        text = text,
        modifier = modifier,
        color = color ?: MaterialTheme.colorScheme.onSurface,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        softWrap = softWrap ?: true,
        overflow = overflow ?: TextOverflow.Clip,
        maxLines = maxLines ?: Int.MAX_VALUE,
        minLines = minLines ?: 1,
        onTextLayout = onTextLayout,
        style = style,
        // Usar la tipografía escalable del tema
        fontSize = if (fontSize == TextUnit.Unspecified) {
            currentTypography.bodyLarge.fontSize
        } else {
            fontSize
        }
    )
}

/**
 * Texto escalable para headlines grandes
 */
@Composable
fun ScalableHeadlineLarge(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    fontWeight: FontWeight? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        fontWeight = fontWeight,
        style = LocalTypography.current.headlineLarge
    )
}

/**
 * Texto escalable para headlines medianos
 */
@Composable
fun ScalableHeadlineMedium(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    fontWeight: FontWeight? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        fontWeight = fontWeight,
        style = LocalTypography.current.headlineMedium
    )
}

/**
 * Texto escalable para headlines pequeños
 */
@Composable
fun ScalableHeadlineSmall(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    fontWeight: FontWeight? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        fontWeight = fontWeight,
        style = LocalTypography.current.headlineSmall
    )
}

/**
 * Texto escalable para títulos grandes
 */
@Composable
fun ScalableTitleLarge(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    fontWeight: FontWeight? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        fontWeight = fontWeight,
        style = LocalTypography.current.titleLarge
    )
}

/**
 * Texto escalable para títulos medianos
 */
@Composable
fun ScalableTitleMedium(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    fontWeight: FontWeight? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        fontWeight = fontWeight,
        style = LocalTypography.current.titleMedium
    )
}

/**
 * Texto escalable para títulos pequeños
 */
@Composable
fun ScalableTitleSmall(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    fontWeight: FontWeight? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        fontWeight = fontWeight,
        style = LocalTypography.current.titleSmall
    )
}

/**
 * Texto escalable para el cuerpo principal
 */
@Composable
fun ScalableBodyLarge(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null,
    fontWeight: FontWeight? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        fontWeight = fontWeight,
        style = LocalTypography.current.bodyLarge
    )
}

/**
 * Texto escalable para el cuerpo mediano
 */
@Composable
fun ScalableBodyMedium(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        style = LocalTypography.current.bodyMedium
    )
}

/**
 * Texto escalable para el cuerpo pequeño
 */
@Composable
fun ScalableBodySmall(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        style = LocalTypography.current.bodySmall
    )
}

/**
 * Texto escalable para etiquetas
 */
@Composable
fun ScalableLabelLarge(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        style = LocalTypography.current.labelLarge
    )
}

/**
 * Texto escalable para etiquetas medianas
 */
@Composable
fun ScalableLabelMedium(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        style = LocalTypography.current.labelMedium
    )
}

/**
 * Texto escalable para etiquetas pequeñas
 */
@Composable
fun ScalableLabelSmall(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int? = null
) {
    ScalableText(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        style = LocalTypography.current.labelSmall
    )
}

/**
 * Componente de prueba para verificar que la tipografía escalable esté funcionando
 */
@Composable
fun ScalableTestText(
    text: String,
    modifier: Modifier = Modifier
) {
    val currentTypography = LocalTypography.current
    Text(
        text = text,
        style = currentTypography.bodyLarge,
        modifier = modifier
    )
}
