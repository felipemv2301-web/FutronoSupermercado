
package com.example.intento1app.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.intento1app.data.models.Product // <--- Tu clase de dominio
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.ui.components.CategoryCard
import com.example.intento1app.ui.components.ScalableBodyLarge
import com.example.intento1app.ui.components.ScalableHeadlineLarge
import com.example.intento1app.ui.components.ScalableHeadlineMedium
import com.example.intento1app.ui.theme.FutronoCafe
import com.example.intento1app.ui.theme.FutronoNaranja
import com.example.intento1app.viewmodel.AccessibilityViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

val StockHigh = Color(0xFF2E7D32)
val StockMedium = Color(0xFFF57C00)
val StockLow = Color(0xFFD32F2F)

@Composable
fun HomeScreen(
    onCartClick: () -> Unit,
    cartItemCount: Int,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    // AÑADIR ESTADO PARA LA BÚSQUEDA EN EL NIVEL SUPERIOR
    var searchQuery by remember { mutableStateOf("") }

    // Si hay una categoría seleccionada O si hay texto en la barra de búsqueda,
    // muestra la lista de productos. Si no, muestra la selección de categorías.
    if (selectedCategory == null && searchQuery.isBlank()) {
        CategorySelectionScreen(
            onCategoryClick = { category -> selectedCategory = category },
            onCartClick = onCartClick,
            cartItemCount = cartItemCount,
            searchQuery = searchQuery, // Pasa el estado
            onSearchQueryChange = { newQuery -> searchQuery = newQuery }, // Permite actualizarlo
            modifier = modifier
        )
    } else {
        ProductListScreen(
            category = selectedCategory, // Puede ser null si la búsqueda es global
            onBackClick = {
                // Al volver, resetea tanto la categoría como la búsqueda
                selectedCategory = null
                searchQuery = ""
            },
            onAddToCart = onAddToCart,
            searchQuery = searchQuery, // Pasa el texto de búsqueda
            onSearchQueryChange = { newQuery -> searchQuery = newQuery }, // Permite actualizarlo
            modifier = modifier
        )
    }
}

@Composable
fun CategorySelectionScreen(
    onCategoryClick: (ProductCategory) -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                ScalableHeadlineLarge(text = "FUTRONO", color = FutronoCafe, fontWeight = FontWeight.Bold)
                ScalableBodyLarge(text = "Supermercado", color = FutronoNaranja)
            }
            Box {
                FloatingActionButton(
                    onClick = onCartClick,
                    containerColor = FutronoNaranja,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, "Carrito de compras", tint = MaterialTheme.colorScheme.onPrimary)
                }
                if (cartItemCount > 0) {
                    Badge(
                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp),
                        containerColor = FutronoCafe
                    ) {
                        Text(
                            if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar en todo el supermercado...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            shape = RoundedCornerShape(24.dp) // Esquinas redondeadas para un look moderno
        )
        ScalableHeadlineMedium(
            text = "Nuestras Categorías",
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ProductCategory.values()) { category ->
                CategoryCard(
                    category = category,
                    onClick = onCategoryClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    category: ProductCategory?, // <--- Ahora es opcional (nullable)
    onBackClick: () -> Unit,
    onAddToCart: (Product) -> Unit,
    searchQuery: String, // <--- Parámetro nuevo
    onSearchQueryChange: (String) -> Unit, // <--- Parámetro nuevo
    modifier: Modifier = Modifier
) {
    var productsFromDb by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    // LaunchedEffect ahora puede traer TODOS los productos o solo los de una categoría
    LaunchedEffect(category) {
        isLoading = true
        val query = if (category != null) {
            // Si hay categoría, busca solo en esa
            Firebase.firestore.collection("products")
                .whereEqualTo("category", category.name.uppercase())
        } else {
            // Si no hay categoría (búsqueda global), trae todos los productos
            Firebase.firestore.collection("products")
        }

        query.get()
            .addOnSuccessListener { result ->
                productsFromDb = result.toObjects(Product::class.java)
                isLoading = false
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error al obtener productos.", exception)
                isLoading = false
            }
    }

    // Se re-ejecuta cada vez que searchQuery, productsFromDb o selectedCategories cambian
    val filteredProducts = remember(searchQuery, productsFromDb, selectedCategories) {
        productsFromDb.filter { product ->
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

    Scaffold(
        topBar = {
            TopAppBar(
                // El título ahora es dinámico
                title = { Text(category?.displayName ?: "Resultados de la Búsqueda") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                // ...
            )
        }
    ) { paddingValues ->
        // Usar una Columna para poner el buscador arriba y la lista debajo
        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            // Botones de categoría arriba del buscador
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(getProductCategoriesForClient()) { categoryName ->
                        CategoryChipClient(
                            category = categoryName,
                            isSelected = selectedCategories.contains(categoryName),
                            onClick = { 
                                selectedCategories = if (categoryName == "Todos") {
                                    if (selectedCategories.contains("Todos")) {
                                        emptySet()
                                    } else {
                                        setOf("Todos")
                                    }
                                } else {
                                    val newSet = selectedCategories.toMutableSet()
                                    if (newSet.contains(categoryName)) {
                                        newSet.remove(categoryName)
                                    } else {
                                        newSet.remove("Todos") // Si selecciona una categoría específica, quitar "Todos"
                                        newSet.add(categoryName)
                                    }
                                    newSet
                                }
                            }
                        )
                    }
                }
            }

            // Mostrar un buscador interno
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text(if (category != null) "Buscar en ${category.displayName}..." else "Buscar productos...") },
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (filteredProducts.isEmpty()) {
                    // Mensaje dinámico si no hay resultados
                    val message = if (productsFromDb.isEmpty()) {
                        "No hay productos en esta categoría."
                    } else {
                        "No se encontraron productos para \"$searchQuery\"."
                    }
                    Text(message, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                } else {
                    // USA LA LISTA FILTRADA
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredProducts) { product -> // <--- Usa filteredProducts
                            ProductCard(product = product, onAddToCart = onAddToCart)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChipClient(
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

// Funciones para obtener categorías de productos para el cliente
private fun getProductCategoriesForClient(): List<String> {
    return listOf("Todos") + ProductCategory.values().map { it.displayName }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de ${product.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.ImageNotSupported, "Imagen no disponible", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(product.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${String.format("%,.0f", product.price).replace(",", ".")} / ${product.unit}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Stock: ${product.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            product.stock > 10 -> StockHigh
                            product.stock > 0 -> StockMedium
                            else -> StockLow
                        }
                    )
                }
            }
            IconButton(
                onClick = { onAddToCart(product) },
                modifier = Modifier.size(48.dp).clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(containerColor = FutronoNaranja, contentColor = Color.White),
                enabled = product.stock > 0
            ) {
                Icon(Icons.Default.AddShoppingCart, "Agregar al carrito")
            }
        }
    }
}
