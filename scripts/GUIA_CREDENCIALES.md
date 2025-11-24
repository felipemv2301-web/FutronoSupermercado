# 🔑 Guía para Obtener Credenciales de Firebase Admin SDK

## 📋 Pasos Detallados

### 1. Acceder a Firebase Console
- Ve a: https://console.firebase.google.com/
- Inicia sesión con tu cuenta de Google

### 2. Seleccionar tu Proyecto
- Selecciona el proyecto **FutronoSupermercado** (o el nombre de tu proyecto)

### 3. Ir a Configuración del Proyecto
- Haz clic en el **ícono de engranaje** (⚙️) junto a "Project Overview"
- Selecciona **"Configuración del proyecto"** o **"Project settings"**

### 4. Ir a Cuentas de Servicio
- En el menú superior, haz clic en la pestaña **"Cuentas de servicio"** o **"Service accounts"**

### 5. Generar Nueva Clave Privada
- En la sección "Firebase Admin SDK", haz clic en **"Generar nueva clave privada"** o **"Generate new private key"**
- Se abrirá un diálogo de confirmación
- Haz clic en **"Generar clave"** o **"Generate key"**

### 6. Descargar el Archivo
- Se descargará automáticamente un archivo JSON
- El nombre del archivo será algo como: `futrono-supermercado-xxxxx-firebase-adminsdk-xxxxx.json`

### 7. Renombrar y Colocar el Archivo
- **Renombra** el archivo a: `serviceAccountKey.json`
- **Mueve** el archivo a la carpeta `scripts/` de tu proyecto

### 8. Verificar
El archivo debe estar en:
```
C:\Users\felip\Escritorio\FutronoSupermercado\scripts\serviceAccountKey.json
```

## ⚠️ IMPORTANTE: Seguridad

- **NUNCA** subas este archivo a Git
- El archivo ya está en `.gitignore` para protegerlo
- No compartas este archivo con nadie
- Contiene credenciales de administrador de Firebase

## ✅ Estructura del Archivo

El archivo `serviceAccountKey.json` debe verse así:
```json
{
  "type": "service_account",
  "project_id": "tu-proyecto-id",
  "private_key_id": "...",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "...",
  "client_id": "...",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  ...
}
```

## 🚀 Después de Obtener las Credenciales

Una vez que tengas el archivo `serviceAccountKey.json` en la carpeta `scripts/`, ejecuta:

```bash
cd scripts
node migrate-user-address.js
```

## 🆘 Problemas Comunes

### "Permission denied"
- Verifica que las credenciales tengan permisos de escritura en Firestore
- Verifica las reglas de seguridad de Firestore

### "Invalid credentials"
- Verifica que el archivo JSON esté completo y no corrupto
- Asegúrate de haber descargado el archivo correcto desde Firebase Console

