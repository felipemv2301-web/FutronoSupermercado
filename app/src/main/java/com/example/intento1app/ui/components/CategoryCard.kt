package com.example.intento1app.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import android.graphics.BitmapFactory
import androidx.compose.runtime.remember
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
        colors = CardDefaults.cardColors(
            containerColor = category.containerColor
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val context = LocalContext.current
            val bitmap = remember(category.imageResId) {
                BitmapFactory.decodeResource(context.resources, category.imageResId)
            }
            Image(
                painter = BitmapPainter(bitmap.asImageBitmap()),
                contentDescription = null, // Decorative
                modifier = Modifier
                    .size(55.dp)
                    .padding(bottom = 5.dp)
            )
            ScalableTitleMedium(
                text = category.displayName,
                color = category.textColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}



