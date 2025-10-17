package com.example.intento1app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MercadoPagoCheckoutScreen(
    checkoutUrl: String,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    onPaymentFailure: () -> Unit,
    onPaymentPending: () -> Unit
) {
    val context = LocalContext.current
    
    // Estados para manejar la navegación
    var isLoading by remember { mutableStateOf(true) }
    var currentUrl by remember { mutableStateOf(checkoutUrl) }
    
    // Función para manejar URLs de retorno
    fun handleReturnUrl(url: String) {
        when {
            url.contains("payment-success") || url.contains("approved") -> {
                onPaymentSuccess()
            }
            url.contains("payment-failure") || url.contains("rejected") -> {
                onPaymentFailure()
            }
            url.contains("payment-pending") || url.contains("pending") -> {
                onPaymentPending()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Pago con Mercado Pago",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Abrir en navegador externo
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            Icons.Default.Info, 
                            contentDescription = "Abrir en navegador"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00A1E0), // Color oficial de Mercado Pago
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            // Información de seguridad
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Pago Seguro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00A1E0)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estás siendo redirigido a Mercado Pago para completar tu pago de forma segura.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1976D2)
                    )
                }
            }
            
            // WebView para el checkout
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                            }
                            
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    currentUrl = url ?: checkoutUrl
                                }
                                
                                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                    url?.let { currentUrl ->
                                        // Manejar URLs de retorno
                                        if (currentUrl.contains("intento1app://") || 
                                            currentUrl.contains("payment-") ||
                                            currentUrl.contains("approved") ||
                                            currentUrl.contains("rejected") ||
                                            currentUrl.contains("pending")) {
                                            handleReturnUrl(currentUrl)
                                            return true
                                        }
                                    }
                                    return false
                                }
                            }
                            
                            loadUrl(checkoutUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Indicador de carga
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF00A1E0)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando Mercado Pago...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Función de utilidad para abrir checkout en navegador externo
fun openMercadoPagoInBrowser(context: Context, checkoutUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl))
    context.startActivity(intent)
}
