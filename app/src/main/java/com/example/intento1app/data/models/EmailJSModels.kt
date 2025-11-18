package com.example.intento1app.data.models

import com.google.gson.annotations.SerializedName

/**
 * Modelos de datos para EmailJS
 */

/**
 * Modelo para un item del carrito en el email
 */
data class EmailItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: String,
    @SerializedName("total_price")
    val totalPrice: String,
    @SerializedName("image_url")
    val imageUrl: String? = null
)

/**
 * Modelo para la solicitud de envío de email a EmailJS
 */
data class EmailJSRequest(
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("template_id")
    val templateId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("template_params")
    val templateParams: EmailJSTemplateParams
)

/**
 * Parámetros del template de EmailJS
 */
data class EmailJSTemplateParams(
    @SerializedName("to_name")
    val toName: String,
    @SerializedName("to_email")
    val toEmail: String,
    @SerializedName("tracking_number")
    val trackingNumber: String,
    @SerializedName("payment_id")
    val paymentId: String,
    @SerializedName("purchase_date")
    val purchaseDate: String,
    @SerializedName("subtotal")
    val subtotal: String,
    @SerializedName("iva")
    val iva: String,
    @SerializedName("shipping")
    val shipping: String? = null,
    @SerializedName("total_price")
    val totalPrice: String,
    @SerializedName("items")
    val items: List<EmailItem>
)

/**
 * Respuesta de EmailJS
 */
data class EmailJSResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("text")
    val text: String
)

