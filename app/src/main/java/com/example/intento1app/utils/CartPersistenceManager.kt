package com.example.intento1app.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.intento1app.data.models.CartItem
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Gestor de persistencia del carrito de compras.
 * Guarda y carga el carrito desde SharedPreferences para que persista
 * entre sesiones de la aplicación, excepto cuando se completa un pago.
 */
object CartPersistenceManager {
    private const val PREFS_NAME = "cart_persistence"
    private const val KEY_CART_ITEMS = "saved_cart_items"
    
    // Gson configurado para ignorar campos no serializables (como ImageVector, Color, etc.)
    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .create()
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Guarda el carrito en SharedPreferences
     */
    fun saveCart(context: Context, cartItems: List<CartItem>) {
        try {
            val prefs = getSharedPreferences(context)
            val cartItemsJson = gson.toJson(cartItems)
            
            prefs.edit()
                .putString(KEY_CART_ITEMS, cartItemsJson)
                .apply()
        } catch (e: Exception) {
            android.util.Log.e("CartPersistence", "Error al guardar el carrito: ${e.message}", e)
        }
    }
    
    /**
     * Carga el carrito desde SharedPreferences
     */
    fun loadCart(context: Context): List<CartItem> {
        return try {
            val prefs = getSharedPreferences(context)
            val json = prefs.getString(KEY_CART_ITEMS, null)
            
            if (json != null && json.isNotEmpty()) {
                val type = object : TypeToken<List<CartItem>>() {}.type
                gson.fromJson<List<CartItem>>(json, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("CartPersistence", "Error al cargar el carrito: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * Limpia el carrito guardado (usado después de un pago exitoso)
     */
    fun clearCart(context: Context) {
        try {
            val prefs = getSharedPreferences(context)
            prefs.edit()
                .remove(KEY_CART_ITEMS)
                .apply()
        } catch (e: Exception) {
            android.util.Log.e("CartPersistence", "Error al limpiar el carrito: ${e.message}", e)
        }
    }
}

