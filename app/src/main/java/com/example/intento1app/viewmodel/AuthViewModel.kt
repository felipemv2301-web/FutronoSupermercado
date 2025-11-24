package com.example.intento1app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intento1app.data.models.FirebaseUser
import com.example.intento1app.data.models.UserRole
import com.example.intento1app.data.models.RoleUtils
import com.example.intento1app.data.services.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la autenticación con Firebase
 */
class AuthViewModel : ViewModel() {
    
    private val firebaseService = FirebaseService()
    
    // Estados de la UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    // Estados para roles
    private val _userRoles = MutableStateFlow<List<String>>(emptyList())
    val userRoles: StateFlow<List<String>> = _userRoles.asStateFlow()
    
    private val _isClient = MutableStateFlow(false)
    val isClient: StateFlow<Boolean> = _isClient.asStateFlow()
    
    private val _isWorker = MutableStateFlow(false)
    val isWorker: StateFlow<Boolean> = _isWorker.asStateFlow()
    
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()
    
    init {
        // Verificar si hay un usuario logueado al inicializar
        viewModelScope.launch {
            try {
                println("AuthViewModel: Inicializando - Verificando sesión persistente...")
                checkCurrentUser()
            } catch (e: Exception) {
                println("AuthViewModel: Error al verificar usuario actual: ${e.message}")
                _currentUser.value = null
                _isLoggedIn.value = false
                clearRoles()
            }
        }
    }
    
    /**
     * Registra un nuevo usuario en Firebase
     */
    fun registerUser(
        email: String,
        password: String,
        displayName: String,
        phoneNumber: String = "",
        rut: String = "",
        address: String = "",
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                println("AuthViewModel: Registrando usuario: $email")
                println("AuthViewModel: Teléfono a guardar: $phoneNumber")
                println("AuthViewModel: RUT a guardar: $rut")
                println("AuthViewModel: Dirección a guardar: $address")
                
                val result = firebaseService.registerUser(email, password, displayName, phoneNumber, rut, address)
                
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    updateUserRoles(user)
                    
                    println("AuthViewModel: Usuario registrado exitosamente en Firebase: ${user.email}")
                    println("AuthViewModel: Roles del usuario: ${user.roles}")
                    println("AuthViewModel: RUT guardado: ${user.rut}")
                    println("AuthViewModel: Dirección guardada: ${user.address}")
                    onSuccess(user)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _errorMessage.value = error
                    println("AuthViewModel: Error al registrar usuario: $error")
                    onError(error)
                }
                
            } catch (e: Exception) {
                val error = "Error inesperado: ${e.message}"
                _errorMessage.value = error
                println("AuthViewModel: Excepción al registrar: ${e.message}")
                onError(error)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Inicia sesión con email y contraseña
     */
    fun signInUser(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                println("AuthViewModel: Iniciando sesión: $email")
                
                val result = firebaseService.signInUser(email, password)
                
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    updateUserRoles(user)
                    
                    println("AuthViewModel: Sesión iniciada exitosamente en Firebase: ${user.email}")
                    println("AuthViewModel: Roles del usuario: ${user.roles}")
                    onSuccess(user)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _errorMessage.value = error
                    println("AuthViewModel: Error al iniciar sesión: $error")
                    onError(error)
                }
                
            } catch (e: Exception) {
                val error = "Error inesperado: ${e.message}"
                _errorMessage.value = error
                println("AuthViewModel: Excepción al iniciar sesión: ${e.message}")
                onError(error)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Envía un email de recuperación de contraseña
     */
    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                println("AuthViewModel: Enviando email de recuperación a: $email")
                
                val result = firebaseService.sendPasswordResetEmail(email)
                
                if (result.isSuccess) {
                    println("AuthViewModel: Email de recuperación enviado exitosamente")
                    onSuccess()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _errorMessage.value = error
                    println("AuthViewModel: Error al enviar email de recuperación: $error")
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "Error inesperado: ${e.message}"
                _errorMessage.value = error
                println("AuthViewModel: Excepción al enviar email de recuperación: ${e.message}")
                onError(error)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Cierra la sesión del usuario
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                firebaseService.signOut()
                _currentUser.value = null
                _isLoggedIn.value = false
                _errorMessage.value = null
                clearRoles()
                
                println("AuthViewModel: Sesión cerrada exitosamente en Firebase")
            } catch (e: Exception) {
                println("AuthViewModel: Error al cerrar sesión: ${e.message}")
            }
        }
    }
    
    /**
     * Verifica si hay un usuario logueado al inicializar la aplicación
     */
    private suspend fun checkCurrentUser() {
        try {
            println("AuthViewModel: Verificando usuario actual...")
            val result = firebaseService.getCurrentUserWithRoles()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    updateUserRoles(user)
                    println("AuthViewModel: Sesión persistente encontrada - Usuario: ${user.email}")
                    println("AuthViewModel: Roles del usuario: ${user.roles}")
                } else {
                    _currentUser.value = null
                    _isLoggedIn.value = false
                    clearRoles()
                    println("AuthViewModel: No hay sesión persistente")
                }
            } else {
                _currentUser.value = null
                _isLoggedIn.value = false
                clearRoles()
                println("AuthViewModel: Error al obtener usuario: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            println("AuthViewModel: Error al verificar usuario actual: ${e.message}")
            _currentUser.value = null
            _isLoggedIn.value = false
            clearRoles()
        }
    }
    
    /**
     * Actualiza los roles del usuario actual
     */
    private fun updateUserRoles(user: FirebaseUser) {
        _userRoles.value = user.roles
        _isClient.value = RoleUtils.isClient(user)
        _isWorker.value = RoleUtils.isWorker(user)
        _isAdmin.value = RoleUtils.isAdmin(user)
        
        println("AuthViewModel: Roles actualizados - Cliente: ${_isClient.value}, Trabajador: ${_isWorker.value}, Admin: ${_isAdmin.value}")
    }
    
    /**
     * Limpia los roles del usuario
     */
    private fun clearRoles() {
        _userRoles.value = emptyList()
        _isClient.value = false
        _isWorker.value = false
        _isAdmin.value = false
    }
    
    /**
     * Limpia los mensajes de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Verifica si el usuario actual tiene un rol específico
     */
    fun hasRole(role: UserRole): Boolean {
        return _currentUser.value?.let { user ->
            RoleUtils.hasRole(user, role)
        } ?: false
    }
    
    /**
     * Verifica si el usuario actual es cliente
     */
    fun isClient(): Boolean {
        return _isClient.value
    }
    
    /**
     * Verifica si el usuario actual es trabajador
     */
    fun isWorker(): Boolean {
        return _isWorker.value
    }
    
    /**
     * Verifica si el usuario actual es admin
     */
    fun isAdmin(): Boolean {
        return _isAdmin.value
    }
    
    /**
     * Obtiene el rol principal del usuario actual
     */
    fun getPrimaryRole(): UserRole? {
        return _currentUser.value?.let { user ->
            RoleUtils.getPrimaryRole(user)
        }
    }
    
    /**
     * Verifica manualmente la sesión actual (útil para debugging)
     */
    fun refreshSession() {
        viewModelScope.launch {
            try {
                println("AuthViewModel: Refrescando sesión manualmente...")
                checkCurrentUser()
            } catch (e: Exception) {
                println("AuthViewModel: Error al refrescar sesión: ${e.message}")
            }
        }
    }
}
