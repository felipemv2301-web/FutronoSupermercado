
package com.example.intento1app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditProductViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun getProductById(productId: String) {
        viewModelScope.launch {
            val document = db.collection("products").document(productId).get().await()
            val product = document.toObject(Product::class.java)
            _product.value = product?.copy(id = document.id)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            db.collection("products").document(product.id).set(product).await()
        }
    }
}
