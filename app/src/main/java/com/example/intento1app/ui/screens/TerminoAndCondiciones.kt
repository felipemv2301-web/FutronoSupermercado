package com.example.intento1app.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.intento1app.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminoAndCondiciones(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    Onclick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Condiciones",
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
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FutronoFondo) // Asegúrate de tener definido este color
                .verticalScroll(rememberScrollState())
        ) {
            // Título de la sección (puedes cambiarlo según lo que vayas a poner)
            Text(
                text = "Terminos Y Condiciones",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "1. Introducción",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "POR FAVOR LEA ESTOS TÉRMINOS DE SERVICIO CUIDADOSAMENTE. ESTE ES UN CONTRATO VINCULANTE. Bienvenido a los servicios operados por Supermercado Futrono. Que consiste en la Aplicacion Movil disponible presente donde visualiza este mensaje. Los Términos de Servicio se aplican tanto si eres un usuario que registra una cuenta en los servicios SupemercadoFutrono Online como si eres un usuario no registrado. Los Términos de Servicio se aplican tanto si eres un usuario que registra una cuenta en los Servicios de Supermercado Futrono  como si eres un usuario no registrado. Aceptas que al hacer clic en \"Registrarse\" o al registrarte, descargar, acceder o utilizar los Servicios de Supermercado Futrono de cualquier otra forma, estás celebrando un acuerdo legalmente vinculante entre tú y Supermercado Futrono con respecto al uso de los Servicios de Supermercado Futrono. Reconoces que has leído, comprendido y aceptas estar obligado por estos Términos de Servicio. Si no estás de acuerdo con estos Términos de Servicio, no accedas ni utilices de ninguna otra forma ninguno de los Servicios de Supermercado Futrono.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "2. Definiciones",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Para efectos de estos Términos y Condiciones, se entenderá por:\n" +
                        "\n" +
                        "\"La Aplicación\": La plataforma móvil \"Supermercado Futrono\" puesta a disposición de los usuarios.\n" +
                        "\n" +
                        "\"Usuario\" o \"Cliente\": Toda persona natural o jurídica que se registre y utilice la aplicación para adquirir productos.\n" +
                        "\n" +
                        "\"Productos\": Bienes de consumo, abarrotes, perecibles y otros artículos disponibles para la venta dentro de la aplicación.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "3. Registro de Usuario y Uso de la Cuenta",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Elegibilidad: Para utilizar los servicios de Supermercado Futrono, el Usuario declara ser mayor de 18 años y tener capacidad legal para contratar. Los menores de edad no pueden realizar compras sin la supervisión de un adulto responsable.\n" +
                        "\n" +
                        "Veracidad de la Información: El Usuario se compromete a entregar información real, completa y actualizada al momento de registrarse (nombre, dirección de entrega en Futrono o alrededores, teléfono y correo electrónico). Supermercado Futrono no se responsabiliza por retrasos en la entrega derivados de direcciones erróneas o inexistentes proporcionadas por el Usuario.\n" +
                        "\n" +
                        "Seguridad de la Cuenta: El Usuario es el único responsable de mantener la confidencialidad de su contraseña. Cualquier actividad realizada desde su cuenta se entenderá efectuada por el Usuario. En caso de sospecha de uso no autorizado, debe notificar a soporte inmediatamente.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "4. Productos, Precios y Disponibilidad (Stock)",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Imágenes Referenciales: Las fotos de los productos en la aplicación son referenciales. Aunque nos esforzamos por mostrar los colores y detalles con precisión, el empaque o presentación final del producto entregado puede variar respecto a la imagen mostrada.\n" +
                        "\n" +
                        "Disponibilidad de Stock: Toda compra está sujeta a confirmación de stock. En el caso de que un producto adquirido no se encuentre disponible al momento del armado del pedido (quiebre de stock), Supermercado Futrono contactará al Usuario para ofrecer:\n" +
                        "\n" +
                        "Un producto sustituto de características similares.\n" +
                        "\n" +
                        "La devolución del dinero correspondiente a ese ítem.\n" +
                        "\n" +
                        "La reprogramación del pedido.\n" +
                        "\n" +
                        "Precios: Los precios publicados incluyen IVA. Supermercado Futrono se reserva el derecho de modificar los precios de los productos en cualquier momento. El precio que pagará el cliente será el publicado al momento de finalizar la compra (\"Checkout\").",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "5. Zonas de Cobertura y Despacho",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Área de Reparto: El servicio de despacho a domicilio está disponible exclusivamente dentro de las zonas habilitadas en la comuna de Futrono y alrededores especificados en la App. Si la dirección ingresada está fuera de la zona de cobertura, el sistema no permitirá finalizar la compra o solo habilitará la opción de \"Retiro en Local\".\n" +
                        "\n" +
                        "Tiempos de Entrega: Los tiempos de despacho son estimados y pueden variar debido a alta demanda, condiciones climáticas o factores externos.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "5. Política de Cambios, Devoluciones y Derecho a Retracto",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Garantía Legal (Productos Defectuosos): Si un producto adquirido a través de la aplicación Supermercado Futrono presenta fallas de fábrica, no es apto para su consumo, está vencido al momento de la entrega o no corresponde a lo solicitado, el Usuario tiene derecho a ejercer su garantía legal dentro de los plazos establecidos por la ley vigente (actualmente 6 meses para productos durables y hasta la fecha de vencimiento para perecibles). En estos casos, el Usuario podrá optar entre:\n" +
                        "\n" +
                        "El cambio inmediato del producto.\n" +
                        "\n" +
                        "La devolución del dinero.\n" +
                        "\n" +
                        "Para hacer efectiva esta garantía, el producto debe ser presentado junto con su boleta o comprobante de compra. En caso de productos perecibles (carnes, lácteos, congelados), el reclamo debe realizarse a la brevedad posible (idealmente dentro de las 24 horas siguientes a la recepción) para verificar la falla de origen y no un problema de manipulación posterior.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Derecho a Retracto (Arrepentimiento de compra): De acuerdo con la normativa vigente para compras electrónicas, el Usuario podrá poner término unilateral al contrato (devolver el producto sin causa justificada) en un plazo de 10 días corridos desde la recepción del producto. IMPORTANTE: Para ejercer este derecho, el producto debe estar:\n" +
                        "\n" +
                        "Sin uso.\n" +
                        "\n" +
                        "Con sus sellos y embalajes originales intactos.\n" +
                        "\n" +
                        "En perfectas condiciones.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Exclusiones al Derecho a Retracto (Productos Perecibles): Dada la naturaleza de los productos comercializados, se excluye expresamente el derecho a retracto en los siguientes casos, por motivos de salubridad e higiene:\n" +
                        "\n" +
                        "Alimentos perecibles: Frutas, verduras, carnes, cecinas, lácteos, panadería y congelados, ya que Supermercado Futrono no puede asegurar que la cadena de frío se haya mantenido una vez que el producto salió de nuestro control.\n" +
                        "\n" +
                        "Productos de uso íntimo o higiene personal que hayan sido abiertos.\n" +
                        "\n" +
                        "Productos que, por su naturaleza, no puedan ser devueltos o puedan deteriorarse con rapidez.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Procedimiento de Devolución: Para gestionar cualquier cambio o devolución, el Usuario deberá contactarse a través de nuestro canal de soporte en la App o dirigirse directamente a nuestro local físico en Futrono.\n" +
                        "\n" +
                        "Reembolsos: En caso de proceder la devolución del dinero, esta se realizará al mismo medio de pago utilizado originalmente, en un plazo no mayor a los días hábiles (usualmente entre 5 a 10 días) dependiendo de la entidad bancaria del Usuario.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "6. Medios de Pago y Facturación",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Medio de Pago Único: Para facilitar y asegurar las transacciones, Supermercado Futrono opera exclusivamente a través de la plataforma de pagos Mercado Pago. El Usuario podrá utilizar los métodos que dicha plataforma ofrece y soporta (tarjetas de crédito, débito, saldo en cuenta Mercado Pago, etc.) dentro del flujo de compra de la Aplicación. No se aceptan pagos en efectivo contra entrega ni transferencias directas fuera del sistema integrado.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Procesamiento y Seguridad: Al realizar el pago, el Usuario será redirigido o procesado a través de la tecnología segura de Mercado Pago. Supermercado Futrono no tiene acceso, no recibe ni almacena los datos bancarios ni la información de las tarjetas de crédito de los Usuarios. Toda la seguridad de la transacción financiera es gestionada y garantizada por Mercado Pago bajo sus propios términos y estándares de seguridad.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Comprobantes de Compra (Boletas y Facturas): Por cada compra realizada y aprobada por el sistema de pago, se emitirá el documento tributario correspondiente:\n" +
                        "\n" +
                        "Boleta Electrónica: Se emitirá por defecto a todos los Usuarios finales.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "7. Política de Privacidad y Protección de Datos",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Recopilación de Información: Para el correcto funcionamiento de la aplicación y la gestión de los pedidos, Supermercado Futrono recopila los siguientes datos personales del Usuario:\n" +
                        "\n" +
                        "Datos de Identificación: Nombre completo y RUT (necesario para la emisión de boletas/facturas y validación de identidad).\n" +
                        "\n" +
                        "Datos de Contacto: Correo electrónico y número de teléfono móvil (para notificaciones sobre el estado del pedido).\n" +
                        "\n" +
                        "Datos de Ubicación: Dirección de domicilio exacta y referencias para el despacho.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Uso de la Información: Los datos recopilados serán utilizados exclusivamente para los siguientes fines:\n" +
                        "\n" +
                        "Procesar, validar y despachar los pedidos realizados.\n" +
                        "\n" +
                        "Emitir los documentos tributarios (Boletas y Facturas) exigidos por el SII.\n" +
                        "\n" +
                        "Contactar al cliente en caso de quiebres de stock, problemas con la dirección de entrega o incidencias en el servicio.\n" +
                        "\n" +
                        "Enviar promociones o información relevante, siempre que el Usuario haya aceptado recibir dichas comunicaciones.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "No Almacenamiento de Datos Financieros: Tal como se estipula en la sección de Medios de Pago, Supermercado Futrono NO recopila ni almacena información bancaria (números de tarjeta, códigos de seguridad, claves). Estos datos son procesados directamente y de forma encriptada por Mercado Pago.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Compartir Información con Terceros: Supermercado Futrono se compromete a no vender, arrendar ni compartir los datos personales de sus usuarios con terceros con fines comerciales. La información solo será compartida con proveedores de servicios estrictamente necesarios para la operación, tales como:\n" +
                        "\n" +
                        "Servicios de Logística: Conductores o empresas de reparto para efectuar la entrega.\n" +
                        "\n" +
                        "Pasarela de Pago (Mercado Pago): Para la validación de la transacción.\n" +
                        "\n" +
                        "Autoridades: En caso de requerimiento legal o tributario (SII).",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Seguridad y Derechos del Usuario (Derechos ARCO): Implementamos medidas de seguridad para proteger los datos contra acceso no autorizado. El Usuario titular de los datos podrá, en cualquier momento, ejercer sus derechos de acceso, rectificación, cancelación u oposición respecto a sus datos personales, contactándose a través de nuestro soporte en la aplicación o por correo electrónico.",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FutronoCafeOscuro, // Asegúrate de tener definido este color
                modifier = Modifier.padding(16.dp)
            )
            // AQUÍ: Agrega tus campos de texto, selectores de motivo, etc.

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// He dejado este componente helper por si quieres reutilizar el estilo de tarjeta
// para mostrar datos fijos (como un ID de reclamo o fecha).
@Composable
private fun DataCard(
    title: String,
    value: String,
    iconVector: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = FutronoCafe
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FutronoCafeOscuro.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FutronoCafeOscuro
                )
            }
        }
    }
}