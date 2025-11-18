package com.example.intento1app.data.services

import com.example.intento1app.data.models.EmailJSRequest
import com.example.intento1app.data.models.EmailJSResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Interfaz de API Retrofit para EmailJS
 */
interface EmailJSApi {
    /**
     * Envía un email usando EmailJS
     * @param url URL completa del endpoint de EmailJS
     * @param request Solicitud con los datos del email
     * @return Respuesta de EmailJS
     */
    @POST
    suspend fun sendEmail(
        @Url url: String,
        @Body request: EmailJSRequest
    ): EmailJSResponse
}

