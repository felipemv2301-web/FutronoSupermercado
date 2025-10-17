package com.example.intento1app.data.services

import com.example.intento1app.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface MercadoPagoApiService {
    
    @Headers("Content-Type: application/json")
    @POST("checkout/preferences")
    suspend fun createPreference(
        @Header("Authorization") authorization: String,
        @Body preference: MercadoPagoPreferenceRequest
    ): Response<MercadoPagoPreference>
    
    @GET("v1/payments/{paymentId}")
    suspend fun getPayment(
        @Header("Authorization") authorization: String,
        @Path("paymentId") paymentId: Long
    ): Response<MercadoPagoPaymentResult>
    
    @GET("v1/payments")
    suspend fun searchPayments(
        @Header("Authorization") authorization: String,
        @Query("external_reference") externalReference: String
    ): Response<MercadoPagoSearchResponse>
}

// Modelos para las requests de la API
data class MercadoPagoPreferenceRequest(
    val items: List<MercadoPagoItem>,
    val payer: MercadoPagoPayer? = null,
    val backUrls: MercadoPagoBackUrls? = null,
    val autoReturn: String? = "approved",
    val paymentMethods: MercadoPagoPaymentMethods? = null,
    val notificationUrl: String? = null,
    val statementDescriptor: String? = null,
    val externalReference: String? = null,
    val expires: Boolean? = true,
    val expirationDateFrom: String? = null,
    val expirationDateTo: String? = null
)

data class MercadoPagoSearchResponse(
    val paging: MercadoPagoPaging,
    val results: List<MercadoPagoPaymentResult>
)

data class MercadoPagoPaging(
    val total: Int,
    val limit: Int,
    val offset: Int
)
