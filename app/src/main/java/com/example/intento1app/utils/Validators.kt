package com.example.intento1app.utils

import android.util.Patterns

/**
 * Utilidades de validación para la aplicación Inova9
 * Incluye validadores para RUT chileno, teléfono y email
 */
object Validators {
    
    /**
     * Valida un RUT chileno usando el algoritmo del módulo 11
     * @param rut RUT a validar (puede incluir puntos y guión)
     * @return true si el RUT es válido, false en caso contrario
     */
    fun validarRUT(rut: String): Boolean {
        val limpio = rut.replace(".", "").replace("-", "").uppercase()
        if (limpio.length < 2) return false

        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last()

        // Verificar que el cuerpo contenga solo dígitos
        if (!cuerpo.all { it.isDigit() }) return false

        var suma = 0
        var multiplicador = 2

        for (i in cuerpo.reversed()) {
            suma += Character.getNumericValue(i) * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val resto = suma % 11
        val dvEsperado = when (val resultado = 11 - resto) {
            11 -> '0'
            10 -> 'K'
            else -> resultado.toString().first()
        }

        return dv == dvEsperado
    }

    /**
     * Valida un número de teléfono móvil chileno
     * Acepta formato: +569XXXXXXXX (solo móviles)
     * @param telefono Número de teléfono a validar
     * @return true si el teléfono es válido, false en caso contrario
     */
    fun validarTelefono(telefono: String): Boolean {
        val regex = Regex("""^\+569\d{8}$""")
        return regex.matches(telefono)
    }

    /**
     * Valida un correo electrónico usando el patrón estándar de Android
     * @param email Correo electrónico a validar
     * @return true si el email es válido, false en caso contrario
     */
    fun validarEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida que una contraseña tenga al menos 6 caracteres
     * @param password Contraseña a validar
     * @return true si la contraseña es válida, false en caso contrario
     */
    fun validarPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Valida que una contraseña sea robusta (mínimo 8 caracteres, mayúsculas, minúsculas, números y símbolos)
     * @param password Contraseña a validar
     * @return true si la contraseña es robusta, false en caso contrario
     */
    fun validarPasswordRobusta(password: String): Boolean {
        if (password.length < 8) return false
        
        val tieneMayuscula = password.any { it.isUpperCase() }
        val tieneMinuscula = password.any { it.isLowerCase() }
        val tieneNumero = password.any { it.isDigit() }
        val tieneSimbolo = password.any { !it.isLetterOrDigit() }
        
        return tieneMayuscula && tieneMinuscula && tieneNumero && tieneSimbolo
    }

    /**
     * Obtiene el mensaje de error específico para contraseña robusta
     * @param password Contraseña a validar
     * @return Mensaje de error descriptivo
     */
    fun obtenerMensajeErrorPasswordRobusta(password: String): String {
        if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        
        val tieneMayuscula = password.any { it.isUpperCase() }
        val tieneMinuscula = password.any { it.isLowerCase() }
        val tieneNumero = password.any { it.isDigit() }
        val tieneSimbolo = password.any { !it.isLetterOrDigit() }
        
        val errores = mutableListOf<String>()
        if (!tieneMayuscula) errores.add("una mayúscula")
        if (!tieneMinuscula) errores.add("una minúscula")
        if (!tieneNumero) errores.add("un número")
        if (!tieneSimbolo) errores.add("un símbolo")
        
        return "La contraseña debe contener: ${errores.joinToString(", ")}"
    }

    /**
     * Valida que dos contraseñas coincidan
     * @param password Contraseña original
     * @param confirmPassword Confirmación de contraseña
     * @return true si las contraseñas coinciden, false en caso contrario
     */
    fun validarConfirmacionPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    /**
     * Valida que dos emails coincidan
     * @param email Email original
     * @param confirmEmail Confirmación de email
     * @return true si los emails coinciden, false en caso contrario
     */
    fun validarConfirmacionEmail(email: String, confirmEmail: String): Boolean {
        return email == confirmEmail
    }

    /**
     * Valida que un campo no esté vacío
     * @param campo Campo a validar
     * @return true si el campo no está vacío, false en caso contrario
     */
    fun validarCampoObligatorio(campo: String): Boolean {
        return campo.isNotBlank()
    }

    /**
     * Obtiene un mensaje de error específico para cada tipo de validación
     * @param tipo Tipo de validación que falló
     * @return Mensaje de error descriptivo
     */
    fun obtenerMensajeError(tipo: TipoError): String {
        return when (tipo) {
            TipoError.CAMPO_OBLIGATORIO -> "Este campo es obligatorio"
            TipoError.RUT_INVALIDO -> "El RUT ingresado no es válido"
            TipoError.TELEFONO_INVALIDO -> "El teléfono debe tener formato +569XXXXXXXX (solo móviles)"
            TipoError.EMAIL_INVALIDO -> "El email ingresado no es válido"
            TipoError.EMAIL_NO_COINCIDE -> "Los emails no coinciden"
            TipoError.PASSWORD_CORTA -> "La contraseña debe tener al menos 6 caracteres"
            TipoError.PASSWORD_NO_COINCIDE -> "Las contraseñas no coinciden"
            TipoError.PASSWORD_NO_ROBUSTA -> "La contraseña debe ser más robusta"
        }
    }
}

/**
 * Enum que define los tipos de errores de validación
 */
enum class TipoError {
    CAMPO_OBLIGATORIO,
    RUT_INVALIDO,
    TELEFONO_INVALIDO,
    EMAIL_INVALIDO,
    EMAIL_NO_COINCIDE,
    PASSWORD_CORTA,
    PASSWORD_NO_COINCIDE,
    PASSWORD_NO_ROBUSTA
}
