
package com.example.intento1app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.Product
import com.example.intento1app.data.services.ProductFirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProductViewModel : ViewModel() {

    private val productFirebaseService = ProductFirebaseService()
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun getProductById(productId: String) {
        viewModelScope.launch {
            try {
                val product = productFirebaseService.getProductById(productId)
                _product.value = product
            } catch (e: Exception) {
                Log.e("EditProductViewModel", "Error al obtener producto: ${e.message}")
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                val result = productFirebaseService.updateProduct(product)
                if (result.isSuccess) {
                    Log.d("EditProductViewModel", "Producto actualizado exitosamente")
                } else {
                    Log.e("EditProductViewModel", "Error al actualizar producto: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("EditProductViewModel", "Error al actualizar producto: ${e.message}")
            }
        }
    }
}
