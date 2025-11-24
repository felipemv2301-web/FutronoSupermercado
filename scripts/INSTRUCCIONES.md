# 📋 Instrucciones para Ejecutar la Migración

## ⚠️ Error Común: "Cannot find module"

Si recibes el error `Cannot find module`, asegúrate de ejecutar el script desde la **carpeta scripts** o usar la ruta completa.

## 🚀 Pasos para Ejecutar

### 1. Navegar a la carpeta scripts
```bash
cd scripts
```

### 2. Instalar dependencias (solo la primera vez)
```bash
npm install
```

### 3. Obtener credenciales de Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto
3. Ve a **Configuración del proyecto** (ícono de engranaje)
4. Pestaña **Cuentas de servicio**
5. Haz clic en **Generar nueva clave privada**
6. Guarda el archivo JSON descargado como `serviceAccountKey.json` en la carpeta `scripts/`

⚠️ **IMPORTANTE**: Este archivo contiene credenciales sensibles. NO lo subas a Git.

### 4. Ejecutar el script

**Opción A: Desde la carpeta scripts**
```bash
cd scripts
node migrate-user-address.js
```

**Opción B: Desde la raíz del proyecto**
```bash
node scripts/migrate-user-address.js
```

**Opción C: Usando npm script**
```bash
cd scripts
npm run migrate:address
```

## ✅ Verificación

Después de ejecutar, verifica en Firebase Console:
1. Firestore Database → Colección `users`
2. Abre cualquier documento de usuario
3. Verifica que tiene el campo `address` (puede estar vacío `""`)

## 🆘 Solución de Problemas

### Error: "Cannot find module 'firebase-admin'"
```bash
cd scripts
npm install
```

### Error: "Cannot find module 'serviceAccountKey.json'"
- Verifica que el archivo `serviceAccountKey.json` esté en la carpeta `scripts/`
- O colócalo en la raíz del proyecto
- El script buscará en múltiples ubicaciones automáticamente

### Error: "Permission denied"
- Verifica que las credenciales tengan permisos de escritura en Firestore
- Verifica las reglas de seguridad de Firestore

## 📝 Notas

- El script es **seguro**: solo actualiza usuarios que no tienen el campo `address`
- Puedes ejecutarlo **múltiples veces** sin problemas
- Los usuarios que ya tienen dirección no se modifican

