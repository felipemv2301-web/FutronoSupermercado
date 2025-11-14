package com.example.intento1app.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.FirebasePurchase
import com.example.intento1app.data.services.FirebaseService
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DailyStats(
    val totalOrders: Int = 0,
    val completedOrders: Int = 0,
    val pendingOrders: Int = 0,
    val totalRevenue: Double = 0.0
)

class AnalyticsViewModel(
    private val firebaseService: FirebaseService = FirebaseService()
) : ViewModel() {
    
    private val _dailyStats = MutableStateFlow<DailyStats>(DailyStats())
    val dailyStats: StateFlow<DailyStats> = _dailyStats.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }
    
    init {
        // Iniciar listener de órdenes
        startDailyStatsListener()
    }
    
    /**
     * Inicia el listener para obtener estadísticas del día
     */
    private fun startDailyStatsListener() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                firebaseService.listenToAllOrders().collect { allOrders ->
                    // Filtrar órdenes del día actual
                    val todayOrders = filterTodayOrders(allOrders)
                    
                    // Calcular estadísticas
                    val stats = calculateDailyStats(todayOrders)
                    
                    _dailyStats.value = stats
                    _isLoading.value = false
                    
                    // Registrar evento en Analytics
                    logDailyStatsEvent(stats)
                }
            } catch (e: Exception) {
                println("AnalyticsViewModel: Error al obtener estadísticas: ${e.message}")
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Filtra las órdenes del día actual
     */
    private fun filterTodayOrders(orders: List<FirebasePurchase>): List<FirebasePurchase> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        
        return orders.filter { order ->
            val orderDate = order.purchaseDate?.toDate()?.time ?: 0L
            orderDate >= startOfDay && orderDate < endOfDay
        }
    }
    
    /**
     * Calcula las estadísticas del día
     */
    private fun calculateDailyStats(orders: List<FirebasePurchase>): DailyStats {
        val totalOrders = orders.size
        val completedOrders = orders.count { order ->
            order.paymentStatus == "pedido_listo" || 
            order.paymentStatus == "entregado" ||
            order.paymentStatus == "completado"
        }
        val pendingOrders = orders.count { order ->
            order.paymentStatus == "en_preparacion" || 
            order.paymentStatus == "pendiente" ||
            order.paymentStatus == "pending"
        }
        val totalRevenue = orders.sumOf { it.totalPrice }
        
        return DailyStats(
            totalOrders = totalOrders,
            completedOrders = completedOrders,
            pendingOrders = pendingOrders,
            totalRevenue = totalRevenue
        )
    }
    
    /**
     * Registra un evento en Firebase Analytics
     */
    private fun logEvent(eventName: String, params: Map<String, Any>?) {
        try {
            val bundle = Bundle().apply {
                params?.forEach { (key, value) ->
                    when (value) {
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Double -> putDouble(key, value)
                        is Float -> putFloat(key, value)
                        is String -> putString(key, value)
                        else -> putString(key, value.toString())
                    }
                }
            }
            firebaseAnalytics.logEvent(eventName, bundle)
        } catch (e: Exception) {
            println("AnalyticsViewModel: Error al registrar evento: ${e.message}")
        }
    }
    
    /**
     * Registra las estadísticas del día en Analytics
     */
    private fun logDailyStatsEvent(stats: DailyStats) {
        logEvent("daily_stats_updated", mapOf(
            "total_orders" to stats.totalOrders,
            "completed_orders" to stats.completedOrders,
            "pending_orders" to stats.pendingOrders,
            "total_revenue" to stats.totalRevenue
        ))
    }
    
    /**
     * Registra un evento personalizado
     */
    fun logCustomEvent(eventName: String, params: Map<String, Any>? = null) {
        logEvent(eventName, params)
    }
}

