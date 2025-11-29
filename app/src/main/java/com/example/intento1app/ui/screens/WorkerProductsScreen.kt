package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intento1app.ui.theme.*
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.viewmodel.WorkerProductsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.intento1app.ui.components.WorkerProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerProductsScreen(
    onNavigateBack: () -> Unit,
    onAddProductClick: () -> Unit,
    onEditProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    workerProductsViewModel: WorkerProductsViewModel = viewModel()
) {
    val products by workerProductsViewModel.products.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    // Filtrar productos por búsqueda y categorías
    val filteredProducts = remember(products, searchQuery, selectedCategories) {
        products.filter { product ->
            // Filtro por búsqueda
            val matchesSearch = searchQuery.isBlank() || 
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
            
            // Filtro por categorías
            val matchesCategory = selectedCategories.isEmpty() || 
                selectedCategories.contains("Todos") ||
                selectedCategories.contains(product.category.displayName)
            
            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Gestión de Productos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoBlanco
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = FutronoBlanco

                    )
                }
            },
            actions = {
                IconButton(onClick = onAddProductClick) { // <-- Llama a la nueva función aquí
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar producto",
                        tint = FutronoBlanco
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

        // Contenido
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Buscador
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Buscar productos...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    singleLine = true
                )
            }

            // Estadísticas de productos
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = FutronoVerde
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Resumen de Productos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProductStatItem(
                                title = "Total Productos",
                                value = products.size.toString(),
                                color = Color.White
                            )
                            ProductStatItem(
                                title = "Bajo Stock",
                                value = products.count { it.stock <= 10 }.toString(),
                                color = Color.White
                            )
                            ProductStatItem(
                                title = "Agotados",
                                value = products.count { it.stock <= 0 }.toString(),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Categorías
            item {
                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(getProductCategories()) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = selectedCategories.contains(category),
                            onClick = { 
                                selectedCategories = if (category == "Todos") {
                                    if (selectedCategories.contains("Todos")) {
                                        emptySet()
                                    } else {
                                        setOf("Todos")
                                    }
                                } else {
                                    val newSet = selectedCategories.toMutableSet()
                                    if (newSet.contains(category)) {
                                        newSet.remove(category)
                                    } else {
                                        newSet.remove("Todos") // Si selecciona una categoría específica, quitar "Todos"
                                        newSet.add(category)
                                    }
                                    newSet
                                }
                            }
                        )
                    }
                }
            }

            // Lista de productos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Productos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    if (searchQuery.isNotEmpty() || selectedCategories.isNotEmpty()) {
                        Text(
                            text = "${filteredProducts.size} resultados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(filteredProducts) { product ->
                WorkerProductCard(
                    product = product,
                    onEditProduct = { onEditProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
private fun ProductStatItem(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(text = category)
        },
        selected = isSelected,
        modifier = modifier
    )
}


// Funciones para obtener categorías de productos
private fun getProductCategories(): List<String> {
    return listOf("Todos") + ProductCategory.values().map { it.displayName }
}
