package com.example.intento1app.data.config

/**
 * Configuración de EmailJS
 * 
 * ⚠️ IMPORTANTE: En producción, estas keys deben estar en un servidor backend
 * y nunca deben estar hardcodeadas en la aplicación cliente.
 * 
 * Para este prototipo, las keys se pueden configurar aquí.
 */
object EmailJSConfig {
    /**
     * Service ID de EmailJS
     */
    const val SERVICE_ID = "service_7c3tcjg"
    
    /**
     * Public Key de EmailJS
     */
    const val PUBLIC_KEY = "c2Sc5yih7EnDFm5f_"
    
    /**
     * Private Key de EmailJS (solo para uso en backend)
     * ⚠️ ADVERTENCIA: Esta key NO debe estar en el cliente en producción.
     */
    const val PRIVATE_KEY = "H05dtRcd98w7FFTuKNhFC"
    
    /**
     * Template ID de EmailJS
     */
    const val TEMPLATE_ID = "template_qa5hi9r"
    
    /**
     * Base URL de la API de EmailJS
     */
    const val API_BASE_URL = "https://api.emailjs.com"
}

