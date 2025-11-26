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

data class DateRange(
    val startDate: java.util.Date? = null,
    val endDate: java.util.Date? = null
)

class AnalyticsViewModel(
    private val firebaseService: FirebaseService = FirebaseService()
) : ViewModel() {
    
    private val _dailyStats = MutableStateFlow<DailyStats>(DailyStats())
    val dailyStats: StateFlow<DailyStats> = _dailyStats.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _dateRange = MutableStateFlow<DateRange?>(null)
    val dateRange: StateFlow<DateRange?> = _dateRange.asStateFlow()
    
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }
    
    init {
        // Iniciar listener de órdenes
        startDailyStatsListener()
    }
    
    /**
     * Establece un rango de fechas para filtrar las estadísticas
     * Si se pasa null, muestra estadísticas del día actual
     */
    fun setDateRange(startDate: java.util.Date? = null, endDate: java.util.Date? = null) {
        _dateRange.value = DateRange(startDate, endDate)
        // Recargar estadísticas con el nuevo rango
        loadStatsForDateRange(startDate, endDate)
    }
    
    /**
     * Carga estadísticas para un rango de fechas específico
     */
    private fun loadStatsForDateRange(startDate: java.util.Date? = null, endDate: java.util.Date? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val startTimestamp = startDate?.let { 
                    com.google.firebase.Timestamp(it) 
                }
                val endTimestamp = endDate?.let { 
                    com.google.firebase.Timestamp(it) 
                }
                
                val statsResult = firebaseService.getSalesStatistics(startTimestamp, endTimestamp)
                
                statsResult.fold(
                    onSuccess = { stats ->
                        _dailyStats.value = DailyStats(
                            totalOrders = stats.totalOrders,
                            completedOrders = stats.completedOrders,
                            pendingOrders = stats.pendingOrders,
                            totalRevenue = stats.totalRevenue
                        )
                        _isLoading.value = false
                        logDailyStatsEvent(_dailyStats.value)
                    },
                    onFailure = { error ->
                        println("AnalyticsViewModel: Error al cargar estadísticas: ${error.message}")
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                println("AnalyticsViewModel: Error al cargar estadísticas: ${e.message}")
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Inicia el listener para obtener estadísticas del día o del rango de fechas
     */
    private fun startDailyStatsListener() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                firebaseService.listenToAllOrders().collect { allOrders ->
                    val dateRange = _dateRange.value
                    
                    // Si hay un rango de fechas específico, usarlo; sino, mostrar totales
                    val filteredOrders = if (dateRange != null && (dateRange.startDate != null || dateRange.endDate != null)) {
                        filterOrdersByDateRange(allOrders, dateRange.startDate, dateRange.endDate)
                    } else {
                        // Sin rango de fechas, mostrar totales de todas las órdenes
                        allOrders
                    }
                    
                    // Calcular estadísticas
                    val stats = calculateDailyStats(filteredOrders)
                    
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
     * Filtra órdenes por un rango de fechas
     */
    private fun filterOrdersByDateRange(
        orders: List<FirebasePurchase>,
        startDate: java.util.Date?,
        endDate: java.util.Date?
    ): List<FirebasePurchase> {
        val startTime = startDate?.time ?: 0L
        val endTime = endDate?.time ?: Long.MAX_VALUE
        
        return orders.filter { order ->
            val orderDate = order.purchaseDate?.toDate()?.time ?: 0L
            orderDate >= startTime && orderDate <= endTime
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
            // Una orden está completada SOLO si tiene el estado "completado" o "entregado"
            // NO contar "approved" porque solo indica pago aprobado, no orden completada
            order.paymentStatus == "pedido_listo" || 
            order.paymentStatus == "entregado" ||
            order.paymentStatus == "completado" ||
            // Verificar array de estado (el más importante)
            order.estado.contains("completado") ||
            order.estado.contains("entregado") ||
            order.estado.contains("pedido_listo")
        }
        val pendingOrders = orders.count { order ->
            // Verificar paymentStatus
            order.paymentStatus == "en_preparacion" || 
            order.paymentStatus == "pendiente" ||
            order.paymentStatus == "pending" ||
            // Verificar array de estado
            order.estado.contains("En preparacion") ||
            order.estado.contains("en_preparacion") ||
            order.estado.contains("pendiente")
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

