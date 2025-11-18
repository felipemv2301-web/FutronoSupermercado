package com.example.intento1app.data.services

import android.util.Log
import com.example.intento1app.data.config.EmailJSConfig
import com.example.intento1app.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.text.SimpleDateFormat
import java.util.*

/**
 * Servicio para enviar emails usando EmailJS
 */
class EmailJSService {
    
    private val gson = Gson()
    
    // Configurar logging interceptor para debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Interceptor para agregar headers de navegador (necesario para EmailJS)
    private val browserHeadersInterceptor = okhttp3.Interceptor { chain ->
        val originalRequest = chain.request()
        val requestWithHeaders = originalRequest.newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .header("Accept", "application/json, text/plain, */*")
            .header("Accept-Language", "es-ES,es;q=0.9,en;q=0.8")
            .header("Content-Type", "application/json")
            .header("Origin", "https://dashboard.emailjs.com")
            .header("Referer", "https://dashboard.emailjs.com/")
            .build()
        chain.proceed(requestWithHeaders)
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(browserHeadersInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(EmailJSConfig.API_BASE_URL)
        .client(okHttpClient)
        // Usar GsonConverterFactory solo para el body de la request (JSON)
        // La respuesta es texto plano "OK", se maneja manualmente
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    private val api = retrofit.create(EmailJSApi::class.java)
    
    /**
     * Formatea un número como string con formato de moneda
     */
    private fun formatCurrency(amount: Double): String {
        return String.format(Locale("es", "CL"), "%,.0f", amount).replace(",", ".")
    }
    
    /**
     * Formatea una fecha como string
     */
    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))
        return format.format(date)
    }
    
    /**
     * Convierte CartItem a EmailItem
     */
    private fun convertToEmailItem(cartItem: CartItem): EmailItem {
        return EmailItem(
            name = cartItem.product.name,
            quantity = cartItem.quantity,
            unitPrice = formatCurrency(cartItem.product.price),
            totalPrice = formatCurrency(cartItem.totalPrice),
            imageUrl = cartItem.product.imageUrl.ifEmpty { null }
        )
    }
    
    /**
     * Envía un email de confirmación de compra
     * 
     * @param toName Nombre del destinatario
     * @param toEmail Email del destinatario
     * @param trackingNumber Número de seguimiento
     * @param paymentId ID del pago
     * @param purchaseDate Fecha de la compra
     * @param cartItems Items del carrito
     * @param subtotal Subtotal de la compra
     * @param iva IVA de la compra
     * @param shipping Costo de envío (opcional)
     * @param totalPrice Precio total de la compra
     * @return Result con el éxito o fallo del envío
     */
    suspend fun sendPurchaseConfirmationEmail(
        toName: String,
        toEmail: String,
        trackingNumber: String,
        paymentId: String,
        purchaseDate: Date,
        cartItems: List<CartItem>,
        subtotal: Double,
        iva: Double,
        shipping: Double? = null,
        totalPrice: Double
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("EmailJSService", "=== INICIANDO ENVÍO DE EMAIL ===")
            Log.d("EmailJSService", "Destinatario: $toName <$toEmail>")
            Log.d("EmailJSService", "Tracking: $trackingNumber")
            Log.d("EmailJSService", "Payment ID: $paymentId")
            
            // Validar email
            if (toEmail.isBlank()) {
                Log.w("EmailJSService", "⚠️ Email vacío, no se enviará el correo")
                return@withContext Result.failure(IllegalArgumentException("El email no puede estar vacío"))
            }
            
            // Convertir items
            val emailItems = cartItems.map { convertToEmailItem(it) }
            Log.d("EmailJSService", "Items convertidos: ${emailItems.size}")
            
            // Crear parámetros del template
            val templateParams = EmailJSTemplateParams(
                toName = toName.ifBlank { "Cliente" },
                toEmail = toEmail,
                trackingNumber = trackingNumber,
                paymentId = paymentId,
                purchaseDate = formatDate(purchaseDate),
                subtotal = formatCurrency(subtotal),
                iva = formatCurrency(iva),
                shipping = shipping?.let { formatCurrency(it) },
                totalPrice = formatCurrency(totalPrice),
                items = emailItems
            )
            
            // Crear solicitud
            val request = EmailJSRequest(
                serviceId = EmailJSConfig.SERVICE_ID,
                templateId = EmailJSConfig.TEMPLATE_ID,
                userId = EmailJSConfig.PUBLIC_KEY,
                templateParams = templateParams
            )
            
            Log.d("EmailJSService", "Solicitud creada, enviando a EmailJS...")
            
            // Construir URL del endpoint de EmailJS
            // Formato: https://api.emailjs.com/api/v1.0/email/send
            val url = "${EmailJSConfig.API_BASE_URL}/api/v1.0/email/send"
            
            Log.d("EmailJSService", "URL: $url")
            Log.d("EmailJSService", "Service ID: ${request.serviceId}")
            Log.d("EmailJSService", "Template ID: ${request.templateId}")
            Log.d("EmailJSService", "User ID: ${request.userId}")
            
            // Log del JSON que se enviará
            val requestJson = gson.toJson(request)
            Log.d("EmailJSService", "Request JSON: $requestJson")
            
            // Enviar email
            val response = try {
                api.sendEmail(url, request)
            } catch (e: HttpException) {
                Log.e("EmailJSService", "❌ HTTP Exception")
                Log.e("EmailJSService", "Code: ${e.code()}")
                Log.e("EmailJSService", "Message: ${e.message()}")
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("EmailJSService", "Error body: $errorBody")
                return@withContext Result.failure(Exception("HTTP ${e.code()}: ${errorBody ?: e.message()}"))
            } catch (e: Exception) {
                Log.e("EmailJSService", "❌ Exception al hacer la petición")
                Log.e("EmailJSService", "Message: ${e.message}")
                e.printStackTrace()
                return@withContext Result.failure(e)
            }
            
            if (response.status == 200) {
                Log.d("EmailJSService", "✅ Email enviado exitosamente")
                Log.d("EmailJSService", "Respuesta: ${response.text}")
                Result.success(Unit)
            } else {
                Log.e("EmailJSService", "❌ Error al enviar email")
                Log.e("EmailJSService", "Status: ${response.status}")
                Log.e("EmailJSService", "Mensaje: ${response.text}")
                Result.failure(Exception("Error al enviar email: ${response.text}"))
            }
        } catch (e: Exception) {
            Log.e("EmailJSService", "❌ Excepción al enviar email")
            Log.e("EmailJSService", "Mensaje: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Envía un email de confirmación de compra usando datos de FirebasePurchase
     * 
     * @param purchase Compra de Firebase
     * @param cartItems Items del carrito originales (para obtener imágenes)
     * @return Result con el éxito o fallo del envío
     */
    suspend fun sendPurchaseConfirmationEmailFromPurchase(
        purchase: FirebasePurchase,
        cartItems: List<CartItem>
    ): Result<Unit> {
        // Crear un mapa de productos por ID para obtener las imágenes
        val productImageMap = cartItems.associate { it.product.id to it.product.imageUrl }
        
        // Convertir FirebaseCartItem a CartItem temporal para usar la función existente
        // Nota: CartItem calcula totalPrice automáticamente como product.price * quantity
        val tempCartItems = purchase.items.map { firebaseItem ->
            CartItem(
                product = Product(
                    id = firebaseItem.productId,
                    name = firebaseItem.productName,
                    description = "",
                    // Usar el precio unitario del firebaseItem para que el cálculo sea correcto
                    price = firebaseItem.unitPrice,
                    imageUrl = productImageMap[firebaseItem.productId] ?: firebaseItem.productImageUrl,
                    category = ProductCategory.DESPENSA, // Usar una categoría por defecto
                    unit = "unidad",
                    stock = 0
                ),
                quantity = firebaseItem.quantity
                // totalPrice se calcula automáticamente: product.price * quantity
            )
        }
        
        val purchaseDate = purchase.purchaseDate?.toDate() ?: Date()
        
        return sendPurchaseConfirmationEmail(
            toName = purchase.userName.ifBlank { "Cliente" },
            toEmail = purchase.userEmail,
            trackingNumber = purchase.trackingNumber,
            paymentId = purchase.paymentId,
            purchaseDate = purchaseDate,
            cartItems = tempCartItems,
            subtotal = purchase.subtotal,
            iva = purchase.iva,
            shipping = if (purchase.shipping > 0) purchase.shipping else null,
            totalPrice = purchase.totalPrice
        )
    }
}

