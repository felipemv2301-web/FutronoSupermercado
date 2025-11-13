package com.futrono.simplificado.data.services

import com.futrono.simplificado.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class PaymentService {
    // Reemplaza con tu Access Token de MercadoPago
    private val accessToken = "TU_ACCESS_TOKEN_DE_MERCADOPAGO"
    private val baseUrl = "https://api.mercadopago.com"
    
    private val client = OkHttpClient()
    
    suspend fun createPreference(cartItems: List<CartItem>): Result<PaymentResponse> = withContext(Dispatchers.IO) {
        try {
            val items = cartItems.map { item ->
                JSONObject().apply {
                    put("title", item.product.name)
                    put("quantity", item.quantity)
                    put("unit_price", item.product.price)
                }
            }
            
            val requestBody = JSONObject().apply {
                put("items", org.json.JSONArray(items.map { it.toString() }))
                put("auto_return", "approved")
            }.toString()
            
            val request = Request.Builder()
                .url("$baseUrl/checkout/preferences")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val json = JSONObject(responseBody)
                
                val paymentResponse = PaymentResponse(
                    preferenceId = json.getString("id"),
                    initPoint = json.getString("init_point"),
                    sandboxInitPoint = json.optString("sandbox_init_point", null)
                )
                
                Result.success(paymentResponse)
            } else {
                Result.failure(IOException("Error al crear preferencia: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

