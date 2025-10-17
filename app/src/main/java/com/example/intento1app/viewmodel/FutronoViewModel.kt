package com.example.intento1app.viewmodel

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

class FutronoViewModel : ViewModel() {
    
    private val repository = ProductRepository()
    
    // Estado de la aplicación
    private val _uiState = MutableStateFlow(FutronoUiState())
    val uiState: StateFlow<FutronoUiState> = _uiState.asStateFlow()
    
    init {
        loadProducts()
    }
    
    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val products = repository.getProducts()
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
    
    fun getProductsByCategory(category: ProductCategory) {
        viewModelScope.launch {
            try {
                val products = repository.getProductsByCategory(category)
                _uiState.update { 
                    it.copy(
                        filteredProducts = products,
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
            // Incrementar cantidad
            val index = currentCart.indexOf(existingItem)
            currentCart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // Agregar nuevo item
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

data class FutronoUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: ProductCategory? = null,
    val cart: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
