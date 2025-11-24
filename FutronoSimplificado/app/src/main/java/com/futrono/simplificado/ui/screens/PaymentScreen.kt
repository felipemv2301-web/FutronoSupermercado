package com.futrono.simplificado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.futrono.simplificado.data.models.*
import com.futrono.simplificado.viewmodel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    cartItems: List<CartItem>,
    currentUser: User? = null,
    onPaymentComplete: () -> Unit,
    onBackToCart: () -> Unit,
    onNavigateToCheckout: (String) -> Unit = {},
    viewModel: PaymentViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()
    val paymentSummary by viewModel.paymentSummary.collectAsStateWithLifecycle()
    val currentError by viewModel.currentError.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState) {
        if (paymentState == PaymentState.PENDING && uiState.currentPaymentResponse != null) {
            val paymentResponse = uiState.currentPaymentResponse!!
            onNavigateToCheckout(paymentResponse.initPoint)
        }
    }
    
    LaunchedEffect(cartItems) {
        if (cartItems.isNotEmpty()) {
            viewModel.preparePaymentSummary(cartItems)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago Seguro") },
                navigationIcon = {
                    IconButton(onClick = onBackToCart) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Seguridad",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Confirmación de Pedido",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tu pedido será procesado y aparecerá en tu historial",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resumen de Compra",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.product.name} x${item.quantity}")
                                Text("$${String.format("%,.0f", item.totalPrice).replace(",", ".")}")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Calcular subtotal, IVA y total
                        val subtotal = cartItems.sumOf { it.totalPrice }
                        val iva = subtotal * 0.19 // 19% IVA
                        val total = subtotal + iva
                        
                        // Subtotal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Subtotal",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "$${String.format("%,.0f", subtotal).replace(",", ".")}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // IVA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "IVA (19%)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "$${String.format("%,.0f", iva).replace(",", ".")}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total a Pagar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$${String.format("%,.0f", total).replace(",", ".")}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            item {
                Button(
                    onClick = {
                        viewModel.initiatePayment(cartItems)
                        kotlinx.coroutines.GlobalScope.launch {
                            kotlinx.coroutines.delay(1000)
                            val currentState = viewModel.uiState.value
                            val currentPaymentState = viewModel.paymentState.value
                            if (currentPaymentState == PaymentState.PENDING && currentState.currentPaymentResponse != null) {
                                onNavigateToCheckout(currentState.currentPaymentResponse!!.initPoint)
                            }
                        }
                    },
                    enabled = cartItems.isNotEmpty() && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (paymentState) {
                            PaymentState.PENDING -> Color(0xFF00A1E0)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Procesando...")
                        }
                        paymentState == PaymentState.PENDING -> {
                            Icon(Icons.Default.Info, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Redirigiendo a Mercado Pago...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        else -> {
                            Icon(Icons.Default.Info, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Pagar con Mercado Pago", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            currentError?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(error.message, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                            IconButton(onClick = { viewModel.clearError() }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }
                    }
                }
            }
        }
    }
}

