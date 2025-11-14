package com.example.intento1app.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intento1app.ui.theme.FutronoNaranja
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.ui.theme.FutronoBlanco
import com.example.intento1app.ui.theme.FutronoCafe
import com.example.intento1app.ui.theme.FutronoFondo
import com.example.intento1app.viewmodel.AddProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClick: () -> Unit,
    onProductAdded: () -> Unit,
    modifier: Modifier = Modifier,
    addProductViewModel: AddProductViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedUnit by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val categories = ProductCategory.values().map { it.name }
    val units = listOf("L", "ml", "g", "Kg", "un", "pack", "m", "cm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar Producto",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoBlanco
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = FutronoBlanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FutronoCafe,
                    titleContentColor = FutronoBlanco
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                val labelStyle = TextStyle(fontSize = 18.sp)
                OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Producto", style = labelStyle) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            // Categoría
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    label = { Text("Categoría", style = labelStyle) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = !isLoading
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            // Precio + Unidad (mejor organizados)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio", style = labelStyle) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                )

                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = it }
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        label = { Text("Unidad", style = labelStyle) },
                        readOnly = true,
                        modifier = Modifier
                            .width(100.dp) // Unidad más pequeña y fija
                            .menuAnchor(),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        units.forEach { unitOption ->
                            DropdownMenuItem(
                                text = { Text(unitOption) },
                                onClick = {
                                    unit = unitOption
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock", style = labelStyle) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción", style = labelStyle) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val productCategory = ProductCategory.values().find {
                        it.name.equals(category, ignoreCase = true)
                    }
                    val priceDouble = price.toDoubleOrNull()
                    val stockInt = stock.toIntOrNull()

                    if (name.isNotBlank() && productCategory != null &&
                        priceDouble != null && stockInt != null && unit.isNotBlank()
                    ) {
                        isLoading = true
                        addProductViewModel.viewModelScope.launch {
                            val nextId = addProductViewModel.getNextProductIdSafely()
                            Log.d("AddProductScreen", "Nuevo ID generado: $nextId")
                            val newProduct = Product(
                                id = nextId.toString(),
                                name = name,
                                description = description,
                                price = priceDouble,
                                category = productCategory,
                                stock = stockInt,
                                unit = unit
                            )
                            addProductViewModel.addProduct(
                                product = newProduct,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Producto agregado con éxito", Toast.LENGTH_SHORT).show()
                                    onProductAdded()
                                },
                                onError = { exception ->
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    } else {
                        Toast.makeText(context, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = FutronoNaranja),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Agregar Producto", color = MaterialTheme.colorScheme.onPrimary, style = labelStyle)
                }
            }
        }
    }
}
