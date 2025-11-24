# 🔑 Cómo Obtener el Archivo serviceAccountKey.json

## ⚠️ IMPORTANTE

**NO debes crear este archivo manualmente.** Debe ser **descargado directamente desde Firebase Console**.

El archivo contiene credenciales de administrador que solo Firebase puede generar de forma segura.

## 📋 Pasos para Descargar el Archivo

### 1. Acceder a Firebase Console
```
https://console.firebase.google.com/
```

### 2. Seleccionar tu Proyecto
- Busca y selecciona tu proyecto **FutronoSupermercado**

### 3. Ir a Configuración
- Haz clic en el **ícono de engranaje** ⚙️ (arriba a la izquierda)
- Selecciona **"Configuración del proyecto"** o **"Project settings"**

### 4. Pestaña "Cuentas de servicio"
- En el menú superior, haz clic en **"Cuentas de servicio"** o **"Service accounts"**

### 5. Generar Nueva Clave
- En la sección **"Firebase Admin SDK"**
- Haz clic en **"Generar nueva clave privada"** o **"Generate new private key"**
- Aparecerá un diálogo de advertencia
- Haz clic en **"Generar clave"** o **"Generate key"**

### 6. Descargar
- Se descargará automáticamente un archivo JSON
- El nombre será algo como: `futrono-supermercado-xxxxx-firebase-adminsdk-xxxxx.json`

### 7. Renombrar y Mover
- **Renombra** el archivo a: `serviceAccountKey.json`
- **Mueve** el archivo a la carpeta `scripts/` de tu proyecto

## 📄 Estructura del Archivo

El archivo descargado tendrá esta estructura (con valores reales):

```json
{
  "type": "service_account",
  "project_id": "futrono-supermercado-xxxxx",
  "private_key_id": "abc123def456ghi789...",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...\n(muchas líneas más)\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-xxxxx@futrono-supermercado-xxxxx.iam.gserviceaccount.com",
  "client_id": "123456789012345678901",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xxxxx%40futrono-supermercado-xxxxx.iam.gserviceaccount.com"
}
```

## ✅ Verificación

Después de descargar y colocar el archivo, verifica:

1. **Ubicación correcta:**
   ```
   C:\Users\felip\Escritorio\FutronoSupermercado\scripts\serviceAccountKey.json
   ```

2. **Formato correcto:**
   - Debe ser un archivo JSON válido
   - Debe tener todos los campos mostrados arriba
   - El `private_key` debe estar completo (puede tener muchas líneas)

3. **Ejecutar el script:**
   ```bash
   cd scripts
   node migrate-user-address.js
   ```

## 🆘 Problemas Comunes

### "Invalid JSON"
- Verifica que el archivo esté completo
- Asegúrate de no haber editado el archivo manualmente
- Descarga nuevamente desde Firebase Console

### "Permission denied"
- Verifica que las credenciales tengan permisos de escritura en Firestore
- Verifica las reglas de seguridad de Firestore

### "Cannot find module"
- Verifica que el archivo esté en la carpeta `scripts/`
- O colócalo en la raíz del proyecto

## 🔒 Seguridad

- ⚠️ **NUNCA** subas este archivo a Git (ya está en `.gitignore`)
- ⚠️ **NUNCA** compartas este archivo
- ⚠️ **NUNCA** lo publiques en ningún lugar
- Este archivo da acceso completo a tu base de datos Firebase

