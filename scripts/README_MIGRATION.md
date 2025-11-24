# Scripts de Migración - Campo Address en Usuarios

Este directorio contiene scripts para agregar el campo `address` a los usuarios existentes en Firebase Firestore.

## 📋 Descripción

Los usuarios existentes en la base de datos no tienen el campo `address`. Estos scripts agregan el campo con un valor vacío (`""`) a todos los usuarios que no lo tengan.

## 🚀 Opción 1: Script Node.js (Recomendado)

### Requisitos
- Node.js instalado
- Firebase Admin SDK configurado

### Pasos

1. **Instalar dependencias:**
   ```bash
   npm install firebase-admin
   ```

2. **Configurar credenciales de Firebase Admin SDK:**
   
   **Opción A: Archivo de credenciales (desarrollo)**
   - Descargar el archivo de credenciales desde Firebase Console
   - Ir a: Firebase Console → Configuración del proyecto → Cuentas de servicio
   - Generar nueva clave privada
   - Guardar como `serviceAccountKey.json` en la raíz del proyecto
   - ⚠️ **IMPORTANTE**: Agregar `serviceAccountKey.json` al `.gitignore`

   **Opción B: Variable de entorno (producción)**
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS="/path/to/serviceAccountKey.json"
   ```

3. **Ejecutar el script:**
   ```bash
   node scripts/migrate-user-address.js
   ```

### Salida esperada:
```
🚀 Iniciando migración de direcciones de usuarios...

📊 Total de usuarios encontrados: 10

✅ Batch de 10 usuarios actualizado

📈 Resumen de la migración:
   ✅ Usuarios actualizados: 10
   ⏭️  Usuarios omitidos (ya tenían dirección): 0
   ❌ Errores: 0

✨ Migración completada exitosamente!

🎉 Proceso finalizado
```

## 📱 Opción 2: Script Kotlin (Desde la App)

Este script se puede ejecutar una vez desde la aplicación Android.

### Pasos

1. **Agregar el archivo `UserMigrationHelper.kt`** al proyecto (ya está en `scripts/migrate-user-address.kt`)

2. **Ejecutar la migración desde MainActivity** (una sola vez):

   ```kotlin
   // En MainActivity.kt, dentro de onCreate o en un LaunchedEffect
   LaunchedEffect(Unit) {
       // Solo ejecutar una vez - usar SharedPreferences para marcar
       val prefs = getSharedPreferences("migration_prefs", MODE_PRIVATE)
       val migrationDone = prefs.getBoolean("address_migration_done", false)
       
       if (!migrationDone) {
           UserMigrationHelper.migrateUserAddresses(
               onComplete = { updated, skipped ->
                   Log.d("Migration", "Migración completada: $updated actualizados, $skipped omitidos")
                   prefs.edit().putBoolean("address_migration_done", true).apply()
               },
               onError = { error ->
                   Log.e("Migration", "Error en migración: ${error.message}")
               }
           )
       }
   }
   ```

3. **Remover el código después de ejecutarlo** (opcional, pero recomendado)

## ⚠️ Consideraciones Importantes

### Seguridad
- **NUNCA** subir `serviceAccountKey.json` a Git
- Agregar al `.gitignore`:
  ```
  serviceAccountKey.json
  scripts/serviceAccountKey.json
  ```

### Antes de Ejecutar
1. **Hacer backup de la base de datos** (recomendado)
2. Probar en un entorno de desarrollo primero
3. Verificar que tienes permisos de escritura en Firestore

### Después de Ejecutar
1. Verificar en Firebase Console que los usuarios tienen el campo `address`
2. Probar que la app funciona correctamente con usuarios existentes
3. Remover el código de migración de la app (si usaste la opción Kotlin)

## 🔍 Verificación

Después de ejecutar la migración, verifica en Firebase Console:

1. Ir a Firestore Database
2. Abrir la colección `users`
3. Verificar que los documentos tienen el campo `address` (puede estar vacío `""`)

## 📝 Notas

- El script es **idempotente**: se puede ejecutar múltiples veces sin problemas
- Solo actualiza usuarios que **no tienen** el campo `address` o lo tienen vacío
- Los usuarios que ya tienen dirección no se modifican
- El campo `updatedAt` se actualiza automáticamente

## 🆘 Solución de Problemas

### Error: "Permission denied"
- Verificar que las credenciales de Firebase Admin tienen permisos de escritura
- Verificar las reglas de seguridad de Firestore

### Error: "Batch too large"
- El script maneja automáticamente batches de 500 operaciones
- Si tienes más de 500 usuarios, el script los procesará en múltiples batches

### Error: "Cannot find module 'firebase-admin'"
- Ejecutar: `npm install firebase-admin`

## 📞 Soporte

Si encuentras problemas, revisa los logs del script o de la app para más detalles.

