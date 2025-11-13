package com.futrono.simplificado.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futrono.simplificado.data.models.*
import com.futrono.simplificado.data.services.PaymentService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val paymentService: PaymentService = PaymentService()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()
    
    private val _paymentState = MutableStateFlow(PaymentState.IDLE)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()
    
    private val _paymentSummary = MutableStateFlow<PaymentSummary?>(null)
    val paymentSummary: StateFlow<PaymentSummary?> = _paymentSummary.asStateFlow()
    
    private val _currentError = MutableStateFlow<PaymentError?>(null)
    val currentError: StateFlow<PaymentError?> = _currentError.asStateFlow()
    
    fun preparePaymentSummary(cartItems: List<CartItem>) {
        viewModelScope.launch {
            try {
                val summary = PaymentSummary.fromCartItems(cartItems)
                _paymentSummary.value = summary
            } catch (e: Exception) {
                _currentError.value = PaymentError(
                    code = "SUMMARY_ERROR",
                    message = "Error al preparar el resumen de pago",
                    details = e.message
                )
            }
        }
    }
    
    fun initiatePayment(cartItems: List<CartItem>) {
        viewModelScope.launch {
            try {
                _paymentState.value = PaymentState.LOADING
                _uiState.update { it.copy(isLoading = true) }
                _currentError.value = null
                
                val result = paymentService.createPreference(cartItems)
                
                if (result.isSuccess) {
                    val paymentResponse = result.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            currentPaymentResponse = paymentResponse
                        )
                    }
                    _paymentState.value = PaymentState.PENDING
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _currentError.value = PaymentError(
                        code = "PAYMENT_ERROR",
                        message = error
                    )
                    _paymentState.value = PaymentState.ERROR
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _currentError.value = PaymentError(
                    code = "PAYMENT_ERROR",
                    message = "Error inesperado: ${e.message}"
                )
                _paymentState.value = PaymentState.ERROR
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun clearError() {
        _currentError.value = null
    }
}

