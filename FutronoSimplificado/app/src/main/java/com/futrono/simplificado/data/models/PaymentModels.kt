package com.futrono.simplificado.data.models

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
    val preferenceId: String,
    @SerializedName("init_point") val initPoint: String,
    @SerializedName("sandbox_init_point") val sandboxInitPoint: String? = null
)

enum class PaymentState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR,
    PENDING,
    CANCELLED,
    FAILED
}

data class PaymentError(
    val code: String,
    val message: String,
    val details: String? = null
)

data class PaymentSummary(
    val subtotal: Double,
    val total: Double,
    val items: List<CartItem>
) {
    companion object {
        fun fromCartItems(items: List<CartItem>): PaymentSummary {
            val subtotal = items.sumOf { it.totalPrice }
            val total = subtotal
            return PaymentSummary(
                subtotal = subtotal,
                total = total,
                items = items
            )
        }
    }
}

// Modelos para MercadoPago API
data class MercadoPagoPreferenceRequest(
    val items: List<MercadoPagoItem>,
    val payer: MercadoPagoPayer? = null,
    @SerializedName("back_urls") val backUrls: MercadoPagoBackUrls? = null,
    @SerializedName("auto_return") val autoReturn: String? = "approved"
)

data class MercadoPagoItem(
    val title: String,
    val quantity: Int,
    val unit_price: Double
)

data class MercadoPagoPayer(
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val phone: MercadoPagoPhone? = null
)

data class MercadoPagoPhone(
    val area_code: String? = null,
    val number: String? = null
)

data class MercadoPagoBackUrls(
    val success: String? = null,
    val failure: String? = null,
    val pending: String? = null
)

data class PaymentUiState(
    val isLoading: Boolean = false,
    val currentPaymentResponse: PaymentResponse? = null
)

