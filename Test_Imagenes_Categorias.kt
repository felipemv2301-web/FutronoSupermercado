// ARCHIVO DE PRUEBA - NO USAR EN PRODUCCIÓN
// Este archivo es solo para probar que las imágenes se cargan correctamente

package com.example.intento1app.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.intento1app.R
import com.example.intento1app.data.models.ProductCategory

@Composable
fun TestCategoryImages() {
    Column {
        // Probar cada imagen individualmente
        Text("Prueba de imágenes de categorías:")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Carnes y Pescados
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_carnes),
                contentDescription = "Carnes",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(ProductCategory.CARNES_PESCADOS.color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Carnes y Pescados")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Despensa
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_despensa),
                contentDescription = "Despensa",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(ProductCategory.DESPENSA.color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Despensa")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Frutas y Verduras
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_frutas),
                contentDescription = "Frutas",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(ProductCategory.FRUTAS_VERDURAS.color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Frutas y Verduras")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Bebidas y Snacks
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_bebidas),
                contentDescription = "Bebidas",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(ProductCategory.BEBIDAS_SNACKS.color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Bebidas y Snacks")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Frescos y Lácteos
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_lacteos),
                contentDescription = "Lácteos",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(ProductCategory.FRESCOS_LACTEOS.color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Frescos y Lácteos")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Panadería y Pastelería
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_panaderia),
                contentDescription = "Panadería",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(ProductCategory.PANADERIA_PASTELERIA.color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Panadería y Pastelería")
        }
    }
}
