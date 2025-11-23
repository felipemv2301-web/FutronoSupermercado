package com.example.intento1app.data.config

/**
 * Configuración para EmailJS
 * 
 * EmailJS permite enviar emails desde aplicaciones cliente sin necesidad de un servidor backend.
 * 
 * 🔧 CONFIGURAR AQUÍ:
 * 1. Crea una cuenta en https://www.emailjs.com/
 * 2. Crea un servicio de email (Gmail, Outlook, etc.)
 * 3. Crea una plantilla de email
 * 4. Obtén tu Public Key desde el dashboard
 * 5. Configura los valores abajo
 */
object EmailJSConfig {
    /**
     * Public Key de EmailJS
     * 
     * Obtén tu Public Key desde: https://dashboard.emailjs.com/admin/integration
     */
    const val PUBLIC_KEY = "c2Sc5yih7EnDFm5f_" // ⚠️ CONFIGURA AQUÍ
    
    /**
     * Private Key (Access Token) de EmailJS
     * 
     * Necesario cuando EmailJS está en modo estricto.
     * Obtén tu Private Key desde: https://dashboard.emailjs.com/admin/integration
     * 
     * ⚠️ IMPORTANTE: En producción, considera usar un backend para proteger esta clave.
     */
    const val PRIVATE_KEY = "H05dtRcd98w7FFTuKNhFC" // ⚠️ CONFIGURA AQUÍ (necesario en modo estricto)
    
    /**
     * Service ID de EmailJS
     * 
     * Obtén tu Service ID desde: https://dashboard.emailjs.com/admin
     */
    const val SERVICE_ID = "service_7c3tcjg" // ⚠️ CONFIGURA AQUÍ
    
    /**
     * Template ID de EmailJS para confirmación de compra
     * 
     * Crea una plantilla en: https://dashboard.emailjs.com/admin/template
     * 
     * Variables disponibles en la plantilla:
     * - {{user_name}}: Nombre del usuario
     * - {{user_email}}: Email del usuario
     * - {{order_number}}: Número de pedido/tracking
     * - {{total_price}}: Precio total
     * - {{total_items}}: Cantidad de items
     * - {{items_list}}: Lista de productos (formato HTML o texto)
     * - {{payment_id}}: ID del pago de MercadoPago
     */
    const val TEMPLATE_ID = "template_qa5hi9r" // ⚠️ CONFIGURA AQUÍ
    
    /**
     * URL base de la API de EmailJS
     */
    const val API_BASE_URL = "https://api.emailjs.com/api/v1.0/email/send"
    
    /**
     * Verifica si la configuración está completa
     */
    fun isConfigured(): Boolean {
        return PUBLIC_KEY != "TU_PUBLIC_KEY_AQUI" &&
               SERVICE_ID != "TU_SERVICE_ID_AQUI" &&
               TEMPLATE_ID != "TU_TEMPLATE_ID_AQUI"
    }
    
    /**
     * Verifica si la clave privada está configurada (necesaria para modo estricto)
     */
    fun hasPrivateKey(): Boolean {
        return PRIVATE_KEY != "TU_PRIVATE_KEY_AQUI" && PRIVATE_KEY.isNotEmpty()
    }
}

