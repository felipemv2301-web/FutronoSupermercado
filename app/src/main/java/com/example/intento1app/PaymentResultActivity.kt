package com.example.intento1app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.data.models.PaymentStatus
import com.example.intento1app.data.models.PaymentResult
import com.example.intento1app.data.services.MercadoPagoService
import com.example.intento1app.ui.theme.AccessibleFutronoTheme
import com.example.intento1app.viewmodel.AccessibilityViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Activity que maneja el retorno de MercadoPago después del pago
 */
class PaymentResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Log para debugging
        android.util.Log.d("PaymentResult", "Activity iniciada")
        android.util.Log.d("PaymentResult", "Intent: ${intent}")
        android.util.Log.d("PaymentResult", "Intent data: ${intent.data}")
        android.util.Log.d("PaymentResult", "Intent extras: ${intent.extras}")
        
        val mercadoPagoService = MercadoPagoService(this)
        val uri = intent.data
        
        if (uri != null) {
            android.util.Log.d("PaymentResult", "URI recibida: $uri")
            android.util.Log.d("PaymentResult", "URI scheme: ${uri.scheme}")
            android.util.Log.d("PaymentResult", "URI host: ${uri.host}")
            android.util.Log.d("PaymentResult", "URI path: ${uri.path}")
            android.util.Log.d("PaymentResult", "URI query: ${uri.query}")
            
            val paymentResult = mercadoPagoService.processPaymentResult(uri)
            android.util.Log.d("PaymentResult", "Resultado procesado: ${paymentResult.status} - ${paymentResult.message}")
            
            setContent {
                val accessibilityViewModel: AccessibilityViewModel = viewModel()
                AccessibleFutronoTheme(accessibilityViewModel = accessibilityViewModel) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        PaymentResultScreen(
                            paymentResult = paymentResult,
                            onBackToHome = {
                                // Volver a MainActivity y limpiar el stack
                                val intent = Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                finish()
                            }
                        )
                    }
                }
            }
        } else {
            // Si no hay URI, mostrar error y volver
            setContent {
                val accessibilityViewModel: AccessibilityViewModel = viewModel()
                AccessibleFutronoTheme(accessibilityViewModel = accessibilityViewModel) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        PaymentResultScreen(
                            paymentResult = PaymentResult(
                                status = PaymentStatus.FAILURE,
                                message = "No se pudo procesar el resultado del pago"
                            ),
                            onBackToHome = {
                                val intent = Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentResultScreen(
    paymentResult: PaymentResult,
    onBackToHome: () -> Unit
) {
    val (icon, iconColor, title, message) = when (paymentResult.status) {
        PaymentStatus.SUCCESS -> {
            Quadruple(
                Icons.Filled.CheckCircle,
                Color(0xFF4CAF50),
                "¡Pago Exitoso!",
                paymentResult.message ?: "Tu pago ha sido procesado correctamente."
            )
        }
        PaymentStatus.PENDING -> {
            Quadruple(
                Icons.Filled.Info,
                Color(0xFFFF9800),
                "Pago Pendiente",
                paymentResult.message ?: "Tu pago está pendiente de confirmación. Te notificaremos cuando se complete."
            )
        }
        PaymentStatus.FAILURE -> {
            Quadruple(
                Icons.Filled.Warning,
                Color(0xFFF44336),
                "Pago Rechazado",
                paymentResult.message ?: "No se pudo procesar tu pago. Por favor, intenta nuevamente."
            )
        }
        PaymentStatus.CANCELLED -> {
            Quadruple(
                Icons.Filled.Close,
                Color(0xFF757575),
                "Pago Cancelado",
                paymentResult.message ?: "El pago fue cancelado."
            )
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = iconColor
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = iconColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        paymentResult.paymentId?.let { paymentId ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Información del pago",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID de pago: $paymentId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Mensaje adicional para pagos pendientes
        if (paymentResult.status == PaymentStatus.PENDING) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "ℹ️ Información importante",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tu pago está siendo procesado. Recibirás una notificación cuando se confirme. Puedes verificar el estado en tu cuenta de MercadoPago.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
        
        // Mensaje adicional para pagos fallidos
        if (paymentResult.status == PaymentStatus.FAILURE) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "💡 Sugerencias",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Verifica que los datos de tu tarjeta sean correctos\n• Asegúrate de tener fondos suficientes\n• Intenta con otro método de pago\n• Contacta a tu banco si el problema persiste",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B4513)
            )
        ) {
            Text(
                text = "Volver al Inicio",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// Helper class
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

