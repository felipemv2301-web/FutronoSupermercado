package com.example.intento1app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.FirebaseUser
import com.example.intento1app.data.models.FirebasePurchase
import com.example.intento1app.data.services.FirebaseService
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class CustomerStats(
    val totalClients: Int = 0,
    val newClientsToday: Int = 0,
    val activeClients: Int = 0
)

class CustomersViewModel(
    private val firebaseService: FirebaseService = FirebaseService()
) : ViewModel() {
    
    private val _clients = MutableStateFlow<List<FirebaseUser>>(emptyList())
    val clients: StateFlow<List<FirebaseUser>> = _clients.asStateFlow()
    
    private val _stats = MutableStateFlow<CustomerStats>(CustomerStats())
    val stats: StateFlow<CustomerStats> = _stats.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Mapa para almacenar órdenes recientes por cliente (para calcular actividad)
    private val _clientOrders = MutableStateFlow<Map<String, List<FirebasePurchase>>>(emptyMap())
    
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }
    
    init {
        startClientsListener()
    }
    
    /**
     * Inicia el listener para obtener clientes en tiempo real
     */
    private fun startClientsListener() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Escuchar cambios en clientes
                firebaseService.listenToAllClients().collect { clientsList ->
                    _clients.value = clientsList
                    
                    // Calcular estadísticas
                    calculateStats(clientsList)
                    
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                println("CustomersViewModel: Error al obtener clientes: ${e.message}")
                _isLoading.value = false
            }
        }
        
        // También escuchar órdenes para calcular actividad
        viewModelScope.launch {
            try {
                firebaseService.listenToAllOrders().collect { orders ->
                    // Agrupar órdenes por userId
                    val ordersByUser = orders.groupBy { it.userId }
                    _clientOrders.value = ordersByUser
                    
                    // Recalcular estadísticas con nueva información de órdenes
                    calculateStats(_clients.value)
                }
            } catch (e: Exception) {
                println("CustomersViewModel: Error al obtener órdenes: ${e.message}")
            }
        }
    }
    
    /**
     * Calcula las estadísticas de clientes
     */
    private fun calculateStats(clients: List<FirebaseUser>) {
        val totalClients = clients.size
        
        // Filtrar clientes nuevos hoy
        val newClientsToday = filterTodayClients(clients)
        
        // Calcular clientes activos (isActive == true O tienen órdenes recientes)
        val activeClients = calculateActiveClients(clients, _clientOrders.value)
        
        val newStats = CustomerStats(
            totalClients = totalClients,
            newClientsToday = newClientsToday.size,
            activeClients = activeClients
        )
        
        _stats.value = newStats
        
        // Registrar evento en Analytics
        logCustomerStatsEvent(newStats)
    }
    
    /**
     * Filtra los clientes registrados hoy
     */
    private fun filterTodayClients(clients: List<FirebaseUser>): List<FirebaseUser> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        
        return clients.filter { client ->
            val createdAt = client.createdAt?.toDate()?.time ?: 0L
            createdAt >= startOfDay && createdAt < endOfDay
        }
    }
    
    /**
     * Calcula el número de clientes activos
     * Un cliente es activo si:
     * 1. isActive == true, O
     * 2. Tiene órdenes en los últimos 30 días
     */
    private fun calculateActiveClients(
        clients: List<FirebaseUser>,
        ordersByUser: Map<String, List<FirebasePurchase>>
    ): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        val thirtyDaysAgo = Timestamp(calendar.time)
        
        return clients.count { client ->
            // Verificar si isActive es true
            val isActive = client.isActive
            
            // Verificar si tiene órdenes recientes (últimos 30 días)
            val hasRecentOrders = ordersByUser[client.id]?.any { order ->
                val orderDate = order.purchaseDate ?: return@any false
                orderDate.compareTo(thirtyDaysAgo) >= 0
            } ?: false
            
            isActive || hasRecentOrders
        }
    }
    
    /**
     * Obtiene la última compra de un cliente
     */
    fun getLastPurchase(clientId: String): FirebasePurchase? {
        return _clientOrders.value[clientId]?.maxByOrNull { 
            it.purchaseDate?.toDate()?.time ?: 0L 
        }
    }
    
    /**
     * Obtiene el total gastado por un cliente
     */
    fun getTotalSpent(clientId: String): Double {
        return _clientOrders.value[clientId]?.sumOf { it.totalPrice } ?: 0.0
    }
    
    /**
     * Obtiene el número de pedidos de un cliente
     */
    fun getTotalOrders(clientId: String): Int {
        return _clientOrders.value[clientId]?.size ?: 0
    }
    
    /**
     * Formatea la fecha de última compra para mostrar
     */
    fun formatLastPurchase(clientId: String): String {
        val lastPurchase = getLastPurchase(clientId)
        return if (lastPurchase != null) {
            val purchaseDate = lastPurchase.purchaseDate?.toDate()
            if (purchaseDate != null) {
                formatDateRelative(purchaseDate.time)
            } else {
                "Sin compras"
            }
        } else {
            "Sin compras"
        }
    }
    
    /**
     * Formatea una fecha de forma relativa (ej: "Hace 2 horas", "Ayer", etc.)
     */
    private fun formatDateRelative(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 7 -> "${days / 7} semana${if (days / 7 > 1) "s" else ""} atrás"
            days > 0 -> "${days} día${if (days > 1) "s" else ""} atrás"
            hours > 0 -> "Hace ${hours} hora${if (hours > 1) "s" else ""}"
            minutes > 0 -> "Hace ${minutes} minuto${if (minutes > 1) "s" else ""}"
            else -> "Hace unos momentos"
        }
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
            println("CustomersViewModel: Error al registrar evento: ${e.message}")
        }
    }
    
    /**
     * Registra las estadísticas de clientes en Analytics
     */
    private fun logCustomerStatsEvent(stats: CustomerStats) {
        logEvent("customer_stats_updated", mapOf(
            "total_clients" to stats.totalClients,
            "new_clients_today" to stats.newClientsToday,
            "active_clients" to stats.activeClients
        ))
    }
    
    /**
     * Registra cuando un usuario se vuelve activo
     */
    fun logUserActiveEvent(userId: String, userName: String) {
        logEvent("user_active", mapOf(
            "user_id" to userId,
            "user_name" to userName
        ))
    }
    
    /**
     * Registra un evento personalizado
     */
    fun logCustomEvent(eventName: String, params: Map<String, Any>? = null) {
        logEvent(eventName, params)
    }
}

