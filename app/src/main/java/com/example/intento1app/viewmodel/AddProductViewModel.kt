package com.example.intento1app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddProductViewModel : ViewModel() {

    // Obtiene una instancia de Firestore
    private val db = Firebase.firestore

    /**
     * Agrega un nuevo producto a la colección "products" en Firestore.
     * @param product El producto a agregar.
     * @param onSuccess Callback que se ejecuta si el producto se agrega exitosamente.
     * @param onError Callback que se ejecuta si ocurre un error.
     */
    fun addProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Usamos el ID del producto como ID del documento en Firestore
                db.collection("products").document(product.id).set(product).await()
                onSuccess()
            } catch (e: Exception) {
                // Manejar el error, por ejemplo, logueándolo o mostrando un mensaje.
                onError(e)
            }
        }
    }
}
