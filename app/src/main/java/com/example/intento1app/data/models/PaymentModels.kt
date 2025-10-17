package com.example.intento1app.data.models

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

@Stable
data class PaymentRequest(
    val amount: Double,
    val currency: String = "CLP",
    val orderId: String,
    val sessionId: String,
    val returnUrl: String,
    val items: List<CartItem>
) {
    val totalAmount: Double
        get() = items.sumOf { it.totalPrice }
}

@Stable
data class PaymentResponse(
    val preferenceId: String, // ID de preferencia de Mercado Pago
    val initPoint: String,    // URL de checkout de Mercado Pago
    val orderId: String,
    val sessionId: String,
    val sandboxInitPoint: String? = null // URL de sandbox para testing
)

@Stable
data class PaymentStatus(
    val orderId: String,
    val sessionId: String,
    val status: PaymentState,
    val amount: Double,
    val authorizationCode: String? = null,
    val responseCode: String? = null,
    val responseMessage: String? = null,
    val transactionDate: String? = null
)

@Stable
enum class PaymentState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR,
    PENDING,
    CANCELLED,
    FAILED
}

@Stable
data class PaymentError(
    val code: String,
    val message: String,
    val details: String? = null
)

@Stable
data class PaymentSession(
    val sessionId: String,
    val orderId: String,
    val amount: Double,
    val items: List<CartItem>,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30 * 60 * 1000) // 30 minutos
)

@Stable
data class PaymentSummary(
    val subtotal: Double,
    val total: Double,
    val items: List<CartItem>
) {
    companion object {
        fun fromCartItems(items: List<CartItem>): PaymentSummary {
            val subtotal = items.sumOf { it.totalPrice }
            val total = subtotal // Sin IVA ni envío por ahora
            
            return PaymentSummary(
                subtotal = subtotal,
                total = total,
                items = items
            )
        }
    }
}

// Modelos específicos para Mercado Pago
@Stable
data class MercadoPagoPreference(
    val id: String,
    @SerializedName("init_point") val initPoint: String,
    @SerializedName("sandbox_init_point") val sandboxInitPoint: String?,
    val items: List<MercadoPagoItem>,
    val payer: MercadoPagoPayer?,
    @SerializedName("back_urls") val backUrls: MercadoPagoBackUrls?,
    @SerializedName("auto_return") val autoReturn: String? = "approved",
    @SerializedName("payment_methods") val paymentMethods: MercadoPagoPaymentMethods?,
    @SerializedName("notification_url") val notificationUrl: String?,
    @SerializedName("statement_descriptor") val statementDescriptor: String?
)

@Stable
data class MercadoPagoItem(
    val id: String,
    val title: String,
    val description: String?,
    val pictureUrl: String?,
    val categoryId: String?,
    val quantity: Int,
    val currencyId: String = "CLP",
    @SerializedName("unit_price") val unitPrice: Int // Usar snake_case para Mercado Pago
)

@Stable
data class MercadoPagoPayer(
    val name: String?,
    val surname: String?,
    val email: String?,
    val phone: MercadoPagoPhone?,
    val identification: MercadoPagoIdentification?,
    val address: MercadoPagoAddress?
)

@Stable
data class MercadoPagoPhone(
    val areaCode: String?,
    val number: String?
)

@Stable
data class MercadoPagoIdentification(
    val type: String?,
    val number: String?
)

@Stable
data class MercadoPagoAddress(
    val streetName: String?,
    val streetNumber: Int?,
    val zipCode: String?
)

@Stable
data class MercadoPagoBackUrls(
    val success: String?,
    val failure: String?,
    val pending: String?
)

@Stable
data class MercadoPagoPaymentMethods(
    val excludedPaymentMethods: List<MercadoPagoExcludedPaymentMethod>?,
    val excludedPaymentTypes: List<MercadoPagoExcludedPaymentType>?,
    val installments: Int?
)

@Stable
data class MercadoPagoExcludedPaymentMethod(
    val id: String
)

@Stable
data class MercadoPagoExcludedPaymentType(
    val id: String
)

@Stable
data class MercadoPagoPaymentResult(
    val id: Long?,
    val status: String?,
    val statusDetail: String?,
    val transactionAmount: Double?,
    val currencyId: String?,
    val description: String?,
    val paymentMethodId: String?,
    val paymentTypeId: String?,
    val dateApproved: String?,
    val dateCreated: String?,
    val dateLastUpdated: String?,
    val collectorId: Long?,
    val payer: MercadoPagoPayer?,
    val metadata: Map<String, Any>?,
    val orderId: String?,
    val processingMode: String?,
    val merchantAccountId: String?
)
