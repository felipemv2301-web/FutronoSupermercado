package com.example.intento1app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.Product
import com.google.firebase.firestore.SetOptions
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
    //Consulta el producto con el ID más alto y desde ahí suma un dígito más
    suspend fun getNextProductIdSafely(): Int {
        val counterRef = db.collection("counters").document("products")

        return try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(counterRef)
                val currentId = snapshot.getLong("lastId") ?: 0L
                Log.d("AddProductViewModel", "Current lastId from Firestore: $currentId")
                val nextId = currentId + 1
                transaction.set(counterRef, mapOf("lastId" to nextId), SetOptions.merge())
                nextId.toInt()
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
            1
        }
    }
}
