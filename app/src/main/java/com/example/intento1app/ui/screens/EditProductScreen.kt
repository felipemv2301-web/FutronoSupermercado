
package com.example.intento1app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intento1app.viewmodel.EditProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    editProductViewModel: EditProductViewModel = viewModel()
) {
    val product by editProductViewModel.product.collectAsState()

    LaunchedEffect(productId) {
        editProductViewModel.getProductById(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                product == null -> {
                    // Mostrar indicador de carga
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    val p = product!!
                    var name by remember(p.id) { mutableStateOf(p.name) }
                    var description by remember(p.id) { mutableStateOf(p.description) }
                    var price by remember(p.id) { mutableStateOf(p.price.toString()) }
                    var stock by remember(p.id) { mutableStateOf(p.stock.toString()) }
                    var unit by remember(p.id) { mutableStateOf(p.unit) }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { newName -> name = newName },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { newDescription -> description = newDescription },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { newPrice -> price = newPrice },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { newStock -> stock = newStock },
                        label = { Text("Stock") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { newUnit -> unit = newUnit },
                        label = { Text("Unidad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val updatedProduct = p.copy(
                                name = name,
                                description = description,
                                price = price.toDoubleOrNull() ?: p.price,
                                stock = stock.toIntOrNull() ?: p.stock,
                                unit = unit
                            )
                            editProductViewModel.updateProduct(updatedProduct)
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}
