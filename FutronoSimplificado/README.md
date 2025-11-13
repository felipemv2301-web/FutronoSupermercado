# Futrono Supermercado - Versión Simplificada

Proyecto Android simplificado con las siguientes funcionalidades:

## Características

- ✅ **Login** con Firebase Authentication
- ✅ **1 Categoría** (Despensa) con **1 Producto** desde Firebase
- ✅ **Carrito de compras** simplificado
- ✅ **Confirmación de pago** con redirección a MercadoPago
- ✅ **Integración con MercadoPago** para procesamiento de pagos

## Estructura del Proyecto

```
FutronoSimplificado/
├── app/
│   ├── src/main/
│   │   ├── java/com/futrono/simplificado/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── models/ (Models.kt, PaymentModels.kt)
│   │   │   │   └── services/ (FirebaseService.kt, PaymentService.kt)
│   │   │   ├── viewmodel/ (AuthViewModel.kt, PaymentViewModel.kt)
│   │   │   └── ui/
│   │   │       ├── screens/ (PaymentScreen.kt, MercadoPagoCheckoutScreen.kt)
│   │   │       └── theme/ (Color.kt, Theme.kt)
│   │   └── AndroidManifest.xml
│   └── google-services.json
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml
```

## Configuración

### 1. Firebase

El proyecto usa el archivo `google-services.json` que debe estar en `app/google-services.json`. Asegúrate de que:
- El `package_name` en `google-services.json` sea `com.futrono.simplificado`
- Tengas configurado Firebase Authentication y Firestore

### 2. MercadoPago

Edita `app/src/main/java/com/futrono/simplificado/data/services/PaymentService.kt` y reemplaza:
```kotlin
private val accessToken = "TU_ACCESS_TOKEN_DE_MERCADOPAGO"
```
con tu Access Token de MercadoPago.

### 3. Productos en Firebase

Asegúrate de tener al menos un producto en Firestore con:
- Colección: `products`
- Campo `category`: `"DESPENSA"`
- Campos requeridos: `name`, `description`, `price`, `unit`, `stock`, `isAvailable`, `imageUrl` (opcional)

## Flujo de la Aplicación

1. **Login** → Usuario inicia sesión con Firebase
2. **Home** → Muestra 1 categoría (Despensa) con 1 producto
3. **Carrito** → Agregar/quitar productos
4. **Confirmar Pago** → Resumen y botón para pagar
5. **MercadoPago** → Redirección a checkout de MercadoPago

## Dependencias Principales

- Jetpack Compose
- Firebase (Auth, Firestore)
- Coil (carga de imágenes)
- OkHttp (llamadas HTTP a MercadoPago)
- Material 3

## Notas

- Si no hay productos en Firebase, se muestra un producto demo
- El proyecto está configurado para usar una nueva base de datos Firebase
- El `package_name` es `com.futrono.simplificado` (diferente al proyecto original)

