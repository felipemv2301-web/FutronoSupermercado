package com.example.intento1app.data.models

import com.example.intento1app.data.config.MercadoPagoConfig
import com.google.gson.annotations.SerializedName

/**
 * Modelos de datos para la integración con MercadoPago
 */

data class PaymentPreferenceRequest(
    @SerializedName("items")
    val items: List<PreferenceItem>,
    @SerializedName("payer")
    val payer: Payer? = null,
    @SerializedName("back_urls")
    val backUrls: BackUrls? = null, // Hacer opcional para evitar errores
    @SerializedName("auto_return")
    val autoReturn: String? = null, // Hacer opcional
    @SerializedName("notification_url")
    val notificationUrl: String? = null
)

data class PreferenceItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("currency_id")
    val currencyId: String = MercadoPagoConfig.DEFAULT_CURRENCY
)

data class Payer(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("surname")
    val surname: String? = null,
    @SerializedName("email")
    val email: String? = null
)

data class BackUrls(
    @SerializedName("success")
    val success: String,
    @SerializedName("pending")
    val pending: String,
    @SerializedName("failure")
    val failure: String
)

data class PaymentPreferenceResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("init_point")
    val initPoint: String,
    @SerializedName("sandbox_init_point")
    val sandboxInitPoint: String? = null,
    @SerializedName("status")
    val status: String? = null
)

data class PaymentResult(
    val status: PaymentStatus,
    val paymentId: String? = null,
    val message: String? = null
)

enum class PaymentStatus {
    SUCCESS,
    PENDING,
    FAILURE,
    CANCELLED
}

