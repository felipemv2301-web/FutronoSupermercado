package com.futrono.simplificado.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futrono.simplificado.data.models.FirebaseUser
import com.futrono.simplificado.data.services.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val firebaseService = FirebaseService()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        viewModelScope.launch {
            try {
                checkCurrentUser()
            } catch (e: Exception) {
                _currentUser.value = null
                _isLoggedIn.value = false
            }
        }
    }
    
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
                
                val result = firebaseService.signInUser(email, password)
                
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    onSuccess(user)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _errorMessage.value = error
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "Error inesperado: ${e.message}"
                _errorMessage.value = error
                onError(error)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                firebaseService.signOut()
                _currentUser.value = null
                _isLoggedIn.value = false
                _errorMessage.value = null
            } catch (e: Exception) {
                println("Error al cerrar sesión: ${e.message}")
            }
        }
    }
    
    private suspend fun checkCurrentUser() {
        try {
            val result = firebaseService.getCurrentUserWithRoles()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                } else {
                    _currentUser.value = null
                    _isLoggedIn.value = false
                }
            } else {
                _currentUser.value = null
                _isLoggedIn.value = false
            }
        } catch (e: Exception) {
            _currentUser.value = null
            _isLoggedIn.value = false
        }
    }
}

