# Configuración de Firebase para Inventario de Productos

## Pasos para migrar de lista estática a Firebase Firestore

### 1. Configurar Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto `inova9-app`
3. Ve a **Firestore Database** en el menú lateral
4. Crea una nueva base de datos si no existe
5. Selecciona **Modo de prueba** para desarrollo

### 2. Crear la colección de productos

1. En Firestore Database, haz clic en **Comenzar colección**
2. Nombre de la colección: `products`
3. Agrega los siguientes campos para el primer documento (puedes usar cualquier ID):

```json
{
  "id": "1",
  "name": "Carne de Vacuno Premium",
  "description": "Carne de Vacuno de primera calidad, perfecta para asados",
  "price": 15990.0,
  "category": "CARNES_PESCADOS",
  "imageUrl": "",
  "unit": "kg",
  "stock": 100,
  "isAvailable": true
}
```

### 3. Poblar la base de datos

**Opción A: Usar la app (Recomendado)**
1. Ejecuta la app
2. Ve a la pantalla de productos
3. Verás un botón "Poblar Firebase con productos de ejemplo"
4. Haz clic en "Poblar" para agregar todos los productos automáticamente

**Opción B: Importar manualmente**
1. Usa el archivo `FirebaseDataSeeder.kt` como referencia
2. Agrega cada producto manualmente en Firebase Console

### 4. Estructura de datos en Firestore

Cada documento en la colección `products` debe tener:

```json
{
  "id": "string",           // ID único del producto
  "name": "string",         // Nombre del producto
  "description": "string",  // Descripción
  "price": "number",        // Precio (Double)
  "category": "string",     // Categoría (enum: CARNES_PESCADOS, DESPENSA, etc.)
  "imageUrl": "string",     // URL de imagen (opcional)
  "unit": "string",         // Unidad de medida (kg, litro, unidad, etc.)
  "stock": "number",        // Stock disponible (Int)
  "isAvailable": "boolean"  // Si está disponible
}
```

### 5. Categorías disponibles

- `CARNES_PESCADOS`
- `DESPENSA`
- `FRUTAS_VERDURAS`
- `BEBIDAS_SNACKS`
- `FRESCOS_LACTEOS`
- `PANADERIA_PASTELERIA`

### 6. Configurar reglas de seguridad (Opcional)

Para desarrollo, puedes usar estas reglas en Firestore:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /products/{document} {
      allow read: if true;  // Permitir lectura a todos
      allow write: if true; // Permitir escritura a todos (solo para desarrollo)
    }
  }
}
```

**⚠️ IMPORTANTE**: Para producción, configura reglas de seguridad apropiadas.

### 7. Verificar la implementación

1. Ejecuta la app
2. Ve a la pantalla de productos
3. Los productos deberían cargarse desde Firebase
4. Verifica que el filtrado por categoría funcione
5. Los productos se actualizarán automáticamente cuando cambies datos en Firebase Console

### 8. Ventajas de usar Firebase

- ✅ **Gestión dinámica**: Modifica productos sin recompilar la app
- ✅ **Tiempo real**: Los cambios se reflejan inmediatamente
- ✅ **Escalabilidad**: Maneja grandes cantidades de productos
- ✅ **Sincronización**: Datos consistentes entre dispositivos
- ✅ **Backup automático**: Los datos están respaldados en la nube

### 9. Eliminar el botón de poblar (Opcional)

Una vez que hayas poblado Firebase, puedes eliminar el botón temporal:

1. Busca el bloque de código que contiene "Botón temporal para poblar Firebase"
2. Elimina todo el bloque `Card` que contiene el botón
3. Esto limpiará la interfaz para producción

### 10. Monitoreo y logs

La app incluye logs detallados para debugging:
- Busca en Logcat los mensajes con tag "ProductFirebaseService"
- Los errores se muestran en la interfaz de usuario
- Los logs te ayudarán a identificar problemas de conectividad o datos
