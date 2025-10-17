package com.example.intento1app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.ui.components.ScalableHeadlineLarge
import com.example.intento1app.ui.components.ScalableHeadlineMedium
import com.example.intento1app.ui.components.ScalableTitleLarge
import com.example.intento1app.ui.components.ScalableTitleMedium
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun CategoryCard(
    category: ProductCategory,
    onClick: (ProductCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(135.dp)
            .clickable { onClick(category) }
            .semantics {
                contentDescription = "Categoría ${category.displayName}. Toca para ver productos."
            },
        shape = RoundedCornerShape(1.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),

        // 2. Usamos 'backgroundColor' del enum para el fondo de la tarjeta.
        colors = CardDefaults.cardColors(
            containerColor = category.containerColor
        )
    ) {
        // 3. El contenido se alinea directamente en la Card, no necesitamos un 'Box' extra.
        Column(
            modifier = Modifier
                .fillMaxSize() // Ocupa todo el espacio de la Card
                .padding(12.dp), // Un padding ligero para que el texto no toque los bordes
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 4. El texto ahora usa 'textColor' del enum para asegurar el contraste.
            ScalableTitleMedium(
                text = category.displayName,
                color = category.textColor, // La corrección clave
                textAlign = TextAlign.Center,
                maxLines = 2,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



