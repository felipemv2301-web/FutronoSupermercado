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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intento1app.ui.theme.FutronoNaranja
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.viewmodel.AddProductViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClick: () -> Unit,
    onProductAdded: () -> Unit, // <-- Cambiado para notificar que se agregó.
    modifier: Modifier = Modifier,
    addProductViewModel: AddProductViewModel = viewModel() // <-- Inyecta el ViewModel
) {
    // Estados para los campos del formulario
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) } // Estado para mostrar carga

    val context = LocalContext.current
    val categories = ProductCategory.values().map { it.name }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text("Agregar Producto") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del Producto") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = {
                Log.d("AddProductScreen", "onExpandedChange triggered! New value: $it")
                expandedCategory = it
            }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = { },
                label = { Text("Categoría") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(), // <-- Importante para el Dropdown
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

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val productCategory = ProductCategory.values().find { it.name.equals(category, ignoreCase = true) }
                val priceDouble = price.toDoubleOrNull()
                val stockInt = stock.toIntOrNull()

                if (name.isNotBlank() && productCategory != null && priceDouble != null && stockInt != null) {
                    isLoading = true // Inicia la carga
                    val newProduct = Product(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        price = priceDouble,
                        category = productCategory,
                        stock = stockInt
                    )

                    addProductViewModel.addProduct(
                        product = newProduct,
                        onSuccess = {
                            isLoading = false // Finaliza la carga
                            Toast.makeText(context, "Producto agregado con éxito", Toast.LENGTH_SHORT).show()
                            onProductAdded() // Llama al callback para, por ejemplo, navegar hacia atrás
                        },
                        onError = { exception ->
                            isLoading = false // Finaliza la carga
                            Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = FutronoNaranja),
            enabled = !isLoading // Deshabilita el botón mientras se carga
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Agregar Producto", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
