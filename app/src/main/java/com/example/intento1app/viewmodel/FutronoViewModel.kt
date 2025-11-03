package com.example.intento1app.viewmodel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.CartItem
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.models.ProductCategory
import com.example.intento1app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception // Asegúrate de tener esta importación para las excepciones
import javax.inject.Inject


class FutronoViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FutronoUiState())
    val uiState: StateFlow<FutronoUiState> = _uiState.asStateFlow()

    init {
        // Carga los productos desde Firebase en cuanto se crea el ViewModel
        loadProducts()
    }

    // --- 1. UNA ÚNICA FUNCIÓN loadProducts ---
    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Usamos el nombre que parece correcto para obtener desde Firebase
                val products = repository.getProductsFromFirestore()
                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error al cargar productos",
                        isLoading = false
                    )
                }
            }
        }
    }

    // --- 2. FUNCIÓN addProduct CORREGIDA Y SIMPLIFICADA ---
    // La hacemos 'suspend' para poder esperar su resultado desde la UI
    suspend fun addProduct(newProduct: Product) {
        try {
            // Usamos el nombre correcto para añadir a Firebase
            val success = repository.addProductToFirestore(newProduct)
            if (success) {
                // Si se guardó bien, recargamos la lista completa desde la fuente de verdad (Firestore)
                loadProducts()
            } else {
                _uiState.update { it.copy(error = "No se pudo guardar el producto") }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message ?: "Error al añadir producto") }
        }
    }

    fun getProductsByCategory(category: ProductCategory) {
        viewModelScope.launch {
            try {
                // Esta función probablemente necesite un método en el repositorio
                // Por ahora, filtramos la lista que ya tenemos en memoria
                val allProducts = _uiState.value.products
                val filtered = allProducts.filter { it.category == category }
                _uiState.update {
                    it.copy(
                        filteredProducts = filtered,
                        selectedCategory = category
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al filtrar productos")
                }
            }
        }
    }
    fun addToCart(product: Product) {
        val currentCart = _uiState.value.cart.toMutableList()
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            val index = currentCart.indexOf(existingItem)
            currentCart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentCart.add(CartItem(product, 1))
        }

        _uiState.update { it.copy(cart = currentCart) }
    }

    fun removeFromCart(productId: String) {
        val currentCart = _uiState.value.cart.toMutableList()
        currentCart.removeAll { it.product.id == productId }
        _uiState.update { it.copy(cart = currentCart) }
    }

    fun updateCartItemQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }

        val currentCart = _uiState.value.cart.toMutableList()
        val index = currentCart.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            currentCart[index] = currentCart[index].copy(quantity = quantity)
            _uiState.update { it.copy(cart = currentCart) }
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cart = emptyList()) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun getCartTotal(): Double {
        return _uiState.value.cart.sumOf { it.totalPrice }
    }

    fun getCartItemCount(): Int {
        return _uiState.value.cart.sumOf { it.quantity }
    }
}

annotation class HiltViewModel

data class FutronoUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: ProductCategory? = null,
    val cart: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
