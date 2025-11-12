package com.example.intento1app.data.config

/**
 * Configuración de MercadoPago
 * 
 * ⚠️ IMPORTANTE: En producción, estas keys deben estar en un servidor backend
 * y nunca deben estar hardcodeadas en la aplicación cliente.
 * 
 * Para este prototipo, las keys se pueden configurar aquí.
 * 
 * 📍 DÓNDE CONFIGURAR LAS KEYS:
 * 
 * 1. Abre este archivo: MercadoPagoConfig.kt
 * 2. Reemplaza "TU_PUBLIC_KEY_AQUI" con tu Public Key real
 * 3. Reemplaza "TU_ACCESS_TOKEN_AQUI" con tu Access Token real
 * 
 * 🔑 CÓMO OBTENER TUS KEYS:
 * 
 * 1. Ve a: https://www.mercadopago.com.ar/developers/panel
 * 2. Inicia sesión con tu cuenta de MercadoPago
 * 3. Crea o selecciona una aplicación
 * 4. Ve a la sección "Credenciales"
 * 5. Copia tu Public Key y Access Token
 * 
 * 🧪 Para pruebas (Sandbox):
 *    - Las keys empiezan con "TEST-"
 *    - Usa tarjetas de prueba
 * 
 * 🚀 Para producción:
 *    - Las keys empiezan con "APP_USR-"
 *    - Procesa pagos reales
 */
object MercadoPagoConfig {
    /**
     * Public Key de MercadoPago
     * 
     * 🔧 CONFIGURAR AQUÍ:
     * Reemplaza "TU_PUBLIC_KEY_AQUI" con tu Public Key real
     * 
     * Ejemplo: "TEST-12345678-12345678-1234567890abcdefghijklmnopqrstuvwxyz-12345678"
     * 
     * Obtén tu key en: https://www.mercadopago.com.ar/developers/panel/credentials
     */
    const val PUBLIC_KEY = "APP_USR-f0e0806e-5b18-4268-ba6f-74ec408937c4"
    
    /**
     * Access Token de MercadoPago
     * 
     * 🔧 CONFIGURAR AQUÍ:
     * Reemplaza "TU_ACCESS_TOKEN_AQUI" con tu Access Token real
     * 
     * Ejemplo: "TEST-12345678-12345678-1234567890abcdefghijklmnopqrstuvwxyz-12345678"
     * 
     * ⚠️ ADVERTENCIA: En producción, este token NO debe estar en el cliente.
     * Debe estar en un servidor backend que cree las preferencias.
     * 
     * Obtén tu token en: https://www.mercadopago.com.ar/developers/panel/credentials
     */
    const val ACCESS_TOKEN = "APP_USR-301076459148124-111123-bed63daf5782936594703f6885003446-2984123494"
    
    // URLs de retorno después del pago
    // 
    // OPCIÓN 1: Servidor de redirección público (recomendado)
    // Despliega el servidor en redirect-server/ en Render, Vercel, Netlify, Railway o Heroku
    // Luego configura aquí la URL base de tu servidor
    // Ejemplo: "https://mercadopago-redirect.onrender.com"
    val REDIRECT_SERVICE_URL: String? = "https://api-mercadopago-0cr1.onrender.com" // ⚠️ CONFIGURA AQUÍ tu URL del servidor
    
    /**
     * URL del webhook para recibir notificaciones de MercadoPago
     * Se construye automáticamente basado en REDIRECT_SERVICE_URL
     */
    fun getWebhookUrl(): String? {
        return REDIRECT_SERVICE_URL?.let { "$it/webhook" }
    }
    
    // OPCIÓN 2: Servidor local (solo para desarrollo, no funciona con MercadoPago)
    const val LOCAL_SERVER_PORT = 8080
    const val LOCAL_SERVER_HOST = "localhost"
    
    // URLs que apuntan al servidor de redirección
    fun getSuccessUrl(): String {
        return if (REDIRECT_SERVICE_URL != null) {
            "$REDIRECT_SERVICE_URL/payment/success"
        } else {
            // Fallback: sin back_urls (el usuario verificará manualmente)
            "futrono://payment/success"
        }
    }
    
    fun getPendingUrl(): String {
        return if (REDIRECT_SERVICE_URL != null) {
            "$REDIRECT_SERVICE_URL/payment/pending"
        } else {
            "futrono://payment/pending"
        }
    }
    
    fun getFailureUrl(): String {
        return if (REDIRECT_SERVICE_URL != null) {
            "$REDIRECT_SERVICE_URL/payment/failure"
        } else {
            "futrono://payment/failure"
        }
    }
    
    // Base URL de la API de MercadoPago
    const val API_BASE_URL = "https://api.mercadopago.com"
    
    /**
     * Moneda por defecto para los pagos
     * 
     * 🔧 CONFIGURAR AQUÍ:
     * Cambia según tu país:
     * - "CLP" para Chile (Pesos Chilenos)
     * - "ARS" para Argentina (Pesos Argentinos)
     * - "BRL" para Brasil (Reales)
     * - "MXN" para México (Pesos Mexicanos)
     * - "COP" para Colombia (Pesos Colombianos)
     * - "PEN" para Perú (Soles)
     * 
     * Lista completa: https://www.mercadopago.com.ar/developers/es/docs/checkout-api/currency-and-country-codes
     */
    const val DEFAULT_CURRENCY = "CLP" // Cambiado a CLP para Chile
}

