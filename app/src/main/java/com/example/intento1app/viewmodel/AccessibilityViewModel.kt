package com.example.intento1app.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AccessibilityViewModel : ViewModel() {
    
    // Estados para el tamaño de texto
    var textScaleFactor by mutableStateOf(1.0f)
        private set
    
    // Estados para otras funciones de accesibilidad
    var isHighContrastEnabled by mutableStateOf(false)
        private set
    
    var isScreenReaderEnabled by mutableStateOf(false)
        private set
    
    companion object {
        private const val PREFS_NAME = "accessibility_prefs"
        private const val KEY_TEXT_SCALE = "text_scale_factor"
        private const val KEY_HIGH_CONTRAST = "high_contrast_enabled"
        private const val KEY_SCREEN_READER = "screen_reader_enabled"
        
        // Factores de escala predefinidos
        const val SCALE_SMALL = 0.8f
        const val SCALE_NORMAL = 1.0f
        const val SCALE_LARGE = 1.2f
        const val SCALE_EXTRA_LARGE = 1.4f
    }
    
    fun initializeAccessibility(context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadPreferences(prefs)
        }
    }
    
    private fun loadPreferences(prefs: SharedPreferences) {
        textScaleFactor = prefs.getFloat(KEY_TEXT_SCALE, SCALE_NORMAL)
        isHighContrastEnabled = prefs.getBoolean(KEY_HIGH_CONTRAST, false)
        isScreenReaderEnabled = prefs.getBoolean(KEY_SCREEN_READER, false)
    }
    
    fun updateTextScale(context: Context, newScale: Float) {
        textScaleFactor = newScale
        saveTextScale(context, newScale)
    }
    
    fun increaseTextSize(context: Context) {
        val newScale = when {
            textScaleFactor < SCALE_SMALL -> SCALE_SMALL
            textScaleFactor < SCALE_NORMAL -> SCALE_NORMAL
            textScaleFactor < SCALE_LARGE -> SCALE_LARGE
            textScaleFactor < SCALE_EXTRA_LARGE -> SCALE_EXTRA_LARGE
            else -> SCALE_EXTRA_LARGE
        }
        updateTextScale(context, newScale)
    }
    
    fun decreaseTextSize(context: Context) {
        val newScale = when {
            textScaleFactor > SCALE_EXTRA_LARGE -> SCALE_EXTRA_LARGE
            textScaleFactor > SCALE_LARGE -> SCALE_LARGE
            textScaleFactor > SCALE_NORMAL -> SCALE_NORMAL
            textScaleFactor > SCALE_SMALL -> SCALE_SMALL
            else -> SCALE_SMALL
        }
        updateTextScale(context, newScale)
    }
    
    fun resetTextSize(context: Context) {
        updateTextScale(context, SCALE_NORMAL)
    }
    
    private fun saveTextScale(context: Context, scale: Float) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putFloat(KEY_TEXT_SCALE, scale).apply()
        }
    }
    
    fun toggleHighContrast(context: Context) {
        isHighContrastEnabled = !isHighContrastEnabled
        saveHighContrast(context, isHighContrastEnabled)
    }
    
    private fun saveHighContrast(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
        }
    }
    
    fun toggleScreenReader(context: Context) {
        isScreenReaderEnabled = !isScreenReaderEnabled
        saveScreenReader(context, isScreenReaderEnabled)
    }
    
    private fun saveScreenReader(context: Context, enabled: Boolean) {
        println("AccessibilityViewModel: Guardando lector de pantalla: $enabled")
        viewModelScope.launch {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_SCREEN_READER, enabled).apply()
            println("AccessibilityViewModel: Lector de pantalla guardado exitosamente")
        }
    }
}
