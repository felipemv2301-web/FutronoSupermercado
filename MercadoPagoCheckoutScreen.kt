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
import android.util.Log
import android.webkit.WebResourceRequest
import android.os.Build

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
    var paymentProcessed by remember { mutableStateOf(false) } // Bandera para evitar procesamiento múltiple
    var webViewRef by remember { mutableStateOf<WebView?>(null) } // Referencia al WebView
    
    // Función para manejar URLs de retorno
    fun handleReturnUrl(url: String) {
        // Evitar procesamiento múltiple
        if (paymentProcessed) {
            Log.d("MercadoPagoCheckout", "Pago ya procesado, ignorando URL: $url")
            return
        }
        
        Log.d("MercadoPagoCheckout", "Verificando URL: $url")
        
        // Extraer parámetros de la URL
        val uri = try {
            Uri.parse(url)
        } catch (e: Exception) {
            null
        }
        
        // Verificar parámetros de query
        val status = uri?.getQueryParameter("status")
        val collectionStatus = uri?.getQueryParameter("collection_status")
        val paymentId = uri?.getQueryParameter("payment_id")
        val preferenceId = uri?.getQueryParameter("preference_id")
        
        Log.d("MercadoPagoCheckout", "Parámetros - status: $status, collection_status: $collectionStatus, payment_id: $paymentId")
        
        // Solo procesar URLs de retorno específicas de Mercado Pago (no URLs normales del checkout)
        when {
            // URLs de retorno personalizadas (deep links)
            url.startsWith("intento1app://payment-success") || 
            url.contains("intento1app://payment-success") -> {
                Log.d("MercadoPagoCheckout", "✅ Pago exitoso detectado (deep link)")
                paymentProcessed = true
                onPaymentSuccess()
            }
            url.startsWith("intento1app://payment-failure") || 
            url.contains("intento1app://payment-failure") -> {
                Log.d("MercadoPagoCheckout", "❌ Pago rechazado detectado (deep link)")
                paymentProcessed = true
                onPaymentFailure()
            }
            url.startsWith("intento1app://payment-pending") || 
            url.contains("intento1app://payment-pending") -> {
                Log.d("MercadoPagoCheckout", "⏳ Pago pendiente detectado (deep link)")
                paymentProcessed = true
                onPaymentPending()
            }
            // URLs de retorno de Mercado Pago con parámetros de estado
            status == "approved" || collectionStatus == "approved" -> {
                Log.d("MercadoPagoCheckout", "✅ Pago exitoso detectado (parámetros: status=$status, collection_status=$collectionStatus)")
                paymentProcessed = true
                onPaymentSuccess()
            }
            status == "rejected" || collectionStatus == "rejected" -> {
                Log.d("MercadoPagoCheckout", "❌ Pago rechazado detectado (parámetros: status=$status, collection_status=$collectionStatus)")
                paymentProcessed = true
                onPaymentFailure()
            }
            status == "pending" || collectionStatus == "pending" -> {
                Log.d("MercadoPagoCheckout", "⏳ Pago pendiente detectado (parámetros: status=$status, collection_status=$collectionStatus)")
                paymentProcessed = true
                onPaymentPending()
            }
            // Detección alternativa por contenido en la URL
            url.contains("status=approved") || url.contains("collection_status=approved") -> {
                Log.d("MercadoPagoCheckout", "✅ Pago exitoso detectado (contenido en URL)")
                paymentProcessed = true
                onPaymentSuccess()
            }
            url.contains("status=rejected") || url.contains("collection_status=rejected") -> {
                Log.d("MercadoPagoCheckout", "❌ Pago rechazado detectado (contenido en URL)")
                paymentProcessed = true
                onPaymentFailure()
            }
            url.contains("status=pending") || url.contains("collection_status=pending") -> {
                Log.d("MercadoPagoCheckout", "⏳ Pago pendiente detectado (contenido en URL)")
                paymentProcessed = true
                onPaymentPending()
            }
        }
    }
    
    // LaunchedEffect para verificar periódicamente la URL del WebView
    LaunchedEffect(webViewRef) {
        if (webViewRef != null && !paymentProcessed) {
            while (!paymentProcessed) {
                kotlinx.coroutines.delay(1000) // Verificar cada segundo
                webViewRef?.let { webView ->
                    try {
                        val url = webView.url
                        if (url != null && url != currentUrl) {
                            Log.d("MercadoPagoCheckout", "URL detectada en polling: $url")
                            currentUrl = url
                            handleReturnUrl(url)
                        }
                    } catch (e: Exception) {
                        Log.e("MercadoPagoCheckout", "Error al obtener URL del WebView: ${e.message}")
                    }
                }
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
                            // Guardar referencia al WebView
                            webViewRef = this
                            
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                // Configuraciones adicionales para mejor compatibilidad
                                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                allowFileAccess = true
                                allowContentAccess = true
                            }
                            
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    url?.let { 
                                        currentUrl = it
                                        Log.d("MercadoPagoCheckout", "Página cargada: $it")
                                        // Verificar siempre si es una URL de retorno
                                        handleReturnUrl(it)
                                        
                                        // También verificar después de un pequeño delay por si Mercado Pago hace redirección con JavaScript
                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            view?.url?.let { delayedUrl ->
                                                if (delayedUrl != it) {
                                                    Log.d("MercadoPagoCheckout", "URL cambió después de onPageFinished: $delayedUrl")
                                                    currentUrl = delayedUrl
                                                    handleReturnUrl(delayedUrl)
                                                }
                                            }
                                        }, 500)
                                    }
                                }
                                
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    url?.let { 
                                        Log.d("MercadoPagoCheckout", "Iniciando carga de página: $it")
                                        currentUrl = it
                                        // Verificar siempre si es una URL de retorno
                                        handleReturnUrl(it)
                                    }
                                }
                                
                                // Método moderno para Android 24+
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url?.toString()
                                    
                                    url?.let { urlString ->
                                        currentUrl = urlString
                                        // Solo interceptar URLs de retorno específicas (deep links o URLs con parámetros de estado)
                                        if (urlString.startsWith("intento1app://") || 
                                            (urlString.contains("status=") && urlString.contains("collection_status="))) {
                                            Log.d("MercadoPagoCheckout", "Interceptando URL de retorno: $urlString")
                                            handleReturnUrl(urlString)
                                            return true
                                        }
                                    }
                                    // Permitir que el WebView cargue URLs normales de Mercado Pago
                                    return false
                                }
                                
                                // Método legacy para compatibilidad con versiones anteriores
                                @Deprecated("Deprecated in Java")
                                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                    url?.let { urlString ->
                                        currentUrl = urlString
                                        // Solo interceptar URLs de retorno específicas
                                        if (urlString.startsWith("intento1app://") || 
                                            (urlString.contains("status=") && urlString.contains("collection_status="))) {
                                            Log.d("MercadoPagoCheckout", "Interceptando URL de retorno (legacy): $urlString")
                                            handleReturnUrl(urlString)
                                            return true
                                        }
                                    }
                                    // Permitir que el WebView cargue URLs normales de Mercado Pago
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
