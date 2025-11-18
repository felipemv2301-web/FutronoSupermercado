package com.example.intento1app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.R
import com.example.intento1app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBankDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var accountHolderName by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf("Vista") } // Default selection
    var showBankDropdown by remember { mutableStateOf(false) }
    
    // Estados de validación
    var rutError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    // Funciones de validación simplificadas
    fun validateRut(rut: String): String {
        if (rut.isEmpty()) return ""
        
        // Validar que solo contenga números y K
        val rutPattern = Regex("^[0-9Kk]+$")
        if (!rutPattern.matches(rut)) {
            return "RUT solo puede contener números y K"
        }
        
        if (rut.length < 7) return "RUT muy corto"
        if (rut.length > 12) return "RUT muy largo"
        return ""
    }
    
    fun validatePhone(phone: String): String {
        if (phone.isEmpty()) return ""
        
        // Validar que solo contenga números y +
        val phonePattern = Regex("^[0-9+]+$")
        if (!phonePattern.matches(phone)) {
            return "Teléfono solo puede contener números y +"
        }
        
        if (phone.length < 8) return "Teléfono muy corto"
        return ""
    }

    // Lista de bancos chilenos
    val banks = listOf(
        "Banco de Crédito e Inversiones (BCI)",
        "Banco Santander",
        "BancoEstado",
        "Banco de Chile",
        "Scotiabank Chile",
        "Itaú CorpBanca",
        "Santiago",
        "Consorcio Financiero",
        "Banco BICE",
        "Banco Falabella"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis datos bancarios",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = FutronoBlanco
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = FutronoBlanco,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FutronoCafe,
                    titleContentColor = FutronoBlanco
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(FutronoBlanco)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Texto explicativo
                Text(
                    text = "Por favor, ingresa los datos de tu cuenta Corriente o Vista para realizar la devolución de tu dinero en caso de no poder reembolsarte a la tarjeta con la cual pagues tu pedido.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FutronoCafeOscuro,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Nombre del titular
                Text(
                    text = "Nombre del titular",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = accountHolderName,
                    onValueChange = { accountHolderName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = FutronoCafe,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = FutronoCafe,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // RUT
                Text(
                    text = "RUT",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = rut,
                    onValueChange = { newValue ->
                        // Filtrar solo números y K - bloqueo total de letras
                        val filteredValue = newValue.filter { char ->
                            char.isDigit() || char == 'K' || char == 'k'
                        }
                        // Siempre actualizar con el valor filtrado
                        rut = filteredValue
                        rutError = validateRut(filteredValue)
                    },
                    isError = rutError.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (rutError.isNotEmpty()) 8.dp else 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (rutError.isEmpty()) FutronoCafe else Color.Red,
                        unfocusedBorderColor = if (rutError.isEmpty()) Color.Gray else Color.Red,
                        focusedLabelColor = if (rutError.isEmpty()) FutronoCafe else Color.Red,
                        unfocusedLabelColor = if (rutError.isEmpty()) Color.Gray else Color.Red,
                        cursorColor = FutronoCafe,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                if (rutError.isNotEmpty()) {
                    Text(
                        text = rutError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Teléfono
                Text(
                    text = "Teléfono",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { newValue ->
                        // Filtrar solo números y + en tiempo real
                        val filteredValue = newValue.filter { it.isDigit() || it == '+' }
                        phone = filteredValue
                        phoneError = validatePhone(filteredValue)
                    },
                    isError = phoneError.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (phoneError.isNotEmpty()) 8.dp else 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (phoneError.isEmpty()) FutronoCafe else Color.Red,
                        unfocusedBorderColor = if (phoneError.isEmpty()) Color.Gray else Color.Red,
                        focusedLabelColor = if (phoneError.isEmpty()) FutronoCafe else Color.Red,
                        unfocusedLabelColor = if (phoneError.isEmpty()) Color.Gray else Color.Red,
                        cursorColor = FutronoCafe,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                if (phoneError.isNotEmpty()) {
                    Text(
                        text = phoneError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Banco
                Text(
                    text = "Banco",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Lista desplegable de bancos
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { 
                            showBankDropdown = !showBankDropdown
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (bankName.isEmpty()) Color.White else FutronoCafe.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (bankName.isEmpty()) Color.Gray else FutronoCafe
                    )
                ) {
                    Column {
                        Text(
                            text = if (bankName.isEmpty()) "Seleccionar banco" else bankName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (bankName.isEmpty()) Color.Gray else FutronoCafeOscuro,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                        
                        if (showBankDropdown) {
                            Divider(color = Color.Gray.copy(alpha = 0.3f))
                            banks.forEach { bank ->
                                Text(
                                    text = bank,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = FutronoCafeOscuro,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            bankName = bank
                                            showBankDropdown = false
                                        }
                                        .padding(16.dp)
                                )
                                if (bank != banks.last()) {
                                    Divider(
                                        color = Color.Gray.copy(alpha = 0.2f),
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                }

                // Tipo de cuenta
                Text(
                    text = "Tipo de cuenta",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AccountTypeChip(
                        text = "Corriente",
                        isSelected = accountType == "Corriente",
                        onClick = { accountType = "Corriente" }
                    )
                    AccountTypeChip(
                        text = "Vista",
                        isSelected = accountType == "Vista",
                        onClick = { accountType = "Vista" }
                    )
                    AccountTypeChip(
                        text = "Ahorro",
                        isSelected = accountType == "Ahorro",
                        onClick = { accountType = "Ahorro" }
                    )
                }

                // Número de cuenta
                Text(
                    text = "Número de cuenta",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FutronoCafe,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = FutronoCafe,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = FutronoCafe,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // Botón Guardar datos
                Button(
                    onClick = {
                        // Validar todos los campos antes de guardar
                        val rutValidation = validateRut(rut)
                        val phoneValidation = validatePhone(phone)
                        
                        if (rutValidation.isEmpty() && phoneValidation.isEmpty() && 
                            accountHolderName.isNotEmpty() && bankName.isNotEmpty() && 
                            accountNumber.isNotEmpty()) {
                            
                            // TODO: Implementar lógica para guardar datos bancarios
                            println("Guardar datos bancarios:")
                            println("Titular: $accountHolderName")
                            println("RUT: $rut")
                            println("Teléfono: $phone")
                            println("Banco: $bankName")
                            println("Tipo de cuenta: $accountType")
                            println("Número de cuenta: $accountNumber")
                        } else {
                            // Mostrar errores si hay campos vacíos o inválidos
                            if (accountHolderName.isEmpty()) {
                                println("Error: Nombre del titular es requerido")
                            }
                            if (bankName.isEmpty()) {
                                println("Error: Banco es requerido")
                            }
                            if (accountNumber.isEmpty()) {
                                println("Error: Número de cuenta es requerido")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FutronoCafe)
                ) {
                    Text(
                        text = "Guardar datos",
                        color = FutronoBlanco,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    )
}

@Composable
private fun AccountTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isSelected) FutronoCafe else Color.Gray,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) FutronoCafe.copy(alpha = 0.1f) else Color.White
    ) {
        Text(
            text = text,
            color = if (isSelected) FutronoCafe else FutronoCafeOscuro,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
