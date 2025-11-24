package com.futrono.simplificado.ui.screens

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
    var isLoading by remember { mutableStateOf(true) }
    var currentUrl by remember { mutableStateOf(checkoutUrl) }
    var paymentProcessed by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    
    fun handleReturnUrl(url: String) {
        if (paymentProcessed) return
        
        val uri = try { Uri.parse(url) } catch (e: Exception) { null }
        val status = uri?.getQueryParameter("status")
        val collectionStatus = uri?.getQueryParameter("collection_status")
        
        when {
            url.contains("status=approved") || collectionStatus == "approved" -> {
                paymentProcessed = true
                onPaymentSuccess()
            }
            url.contains("status=rejected") || collectionStatus == "rejected" -> {
                paymentProcessed = true
                onPaymentFailure()
            }
            url.contains("status=pending") || collectionStatus == "pending" -> {
                paymentProcessed = true
                onPaymentPending()
            }
        }
    }
    
    LaunchedEffect(webViewRef) {
        if (webViewRef != null && !paymentProcessed) {
            while (!paymentProcessed) {
                kotlinx.coroutines.delay(1000)
                webViewRef?.url?.let { url ->
                    if (url != currentUrl) {
                        currentUrl = url
                        handleReturnUrl(url)
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago con Mercado Pago", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Info, contentDescription = "Abrir en navegador")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00A1E0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pago Seguro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FutronoBlanco)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estás siendo redirigido a Mercado Pago para completar tu pago de forma segura.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1976D2))
                }
            }
            
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webViewRef = this
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                            }
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    url?.let {
                                        currentUrl = it
                                        handleReturnUrl(it)
                                    }
                                }
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    url?.let {
                                        currentUrl = it
                                        handleReturnUrl(it)
                                    }
                                }
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    request?.url?.toString()?.let { urlString ->
                                        currentUrl = urlString
                                        if (urlString.contains("status=")) {
                                            handleReturnUrl(urlString)
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
                
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF00A1E0))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Cargando Mercado Pago...", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

