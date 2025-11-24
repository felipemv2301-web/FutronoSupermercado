/**
 * Script de migración para agregar el campo 'address' a usuarios existentes en Firestore
 * 
 * Requisitos:
 * 1. Instalar dependencias: npm install firebase-admin
 * 2. Configurar credenciales de Firebase Admin SDK
 * 3. Ejecutar: node scripts/migrate-user-address.js
 */

const admin = require('firebase-admin');
const path = require('path');
const fs = require('fs');

// Buscar el archivo de credenciales en diferentes ubicaciones
const possiblePaths = [
  path.join(__dirname, 'serviceAccountKey.json'),
  path.join(__dirname, '..', 'serviceAccountKey.json'),
  path.join(process.cwd(), 'serviceAccountKey.json'),
  path.join(process.cwd(), 'scripts', 'serviceAccountKey.json')
];

let serviceAccountPath = null;
for (const possiblePath of possiblePaths) {
  if (fs.existsSync(possiblePath)) {
    serviceAccountPath = possiblePath;
    break;
  }
}

// Inicializar Firebase Admin SDK
if (serviceAccountPath) {
  console.log(`📁 Usando credenciales desde: ${serviceAccountPath}`);
  const serviceAccount = require(serviceAccountPath);
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
} else if (process.env.GOOGLE_APPLICATION_CREDENTIALS) {
  console.log('📁 Usando credenciales desde variable de entorno GOOGLE_APPLICATION_CREDENTIALS');
  admin.initializeApp({
    credential: admin.credential.applicationDefault()
  });
} else {
  console.error('❌ Error: No se encontró el archivo de credenciales de Firebase Admin SDK');
  console.error('   Buscado en:');
  possiblePaths.forEach(p => console.error(`   - ${p}`));
  console.error('\n   Opciones:');
  console.error('   1. Colocar serviceAccountKey.json en la carpeta scripts/ o raíz del proyecto');
  console.error('   2. Configurar variable de entorno GOOGLE_APPLICATION_CREDENTIALS');
  console.error('   3. Descargar desde: Firebase Console → Configuración → Cuentas de servicio');
  process.exit(1);
}

const db = admin.firestore();

async function migrateUserAddresses() {
  console.log('🚀 Iniciando migración de direcciones de usuarios...\n');
  
  try {
    const usersRef = db.collection('users');
    const snapshot = await usersRef.get();
    
    if (snapshot.empty) {
      console.log('✅ No hay usuarios en la base de datos.');
      return;
    }
    
    console.log(`📊 Total de usuarios encontrados: ${snapshot.size}\n`);
    
    let updatedCount = 0;
    let skippedCount = 0;
    let errorCount = 0;
    
    let batch = db.batch();
    let batchCount = 0;
    const BATCH_SIZE = 500; // Firestore permite máximo 500 operaciones por batch
    
    for (const doc of snapshot.docs) {
      const userData = doc.data();
      
      // Solo actualizar si el campo 'address' no existe o está vacío
      if (!userData.address || userData.address === '') {
        batch.update(doc.ref, {
          address: '',
          updatedAt: admin.firestore.FieldValue.serverTimestamp()
        });
        batchCount++;
        updatedCount++;
        
        // Si el batch está lleno, ejecutarlo y crear uno nuevo
        if (batchCount >= BATCH_SIZE) {
          await batch.commit();
          console.log(`✅ Batch de ${batchCount} usuarios actualizado`);
          batch = db.batch(); // Crear nuevo batch
          batchCount = 0;
        }
      } else {
        skippedCount++;
        console.log(`⏭️  Usuario ${doc.id} ya tiene dirección: "${userData.address}"`);
      }
    }
    
    // Ejecutar el batch final si hay operaciones pendientes
    if (batchCount > 0) {
      await batch.commit();
      console.log(`✅ Batch final de ${batchCount} usuarios actualizado`);
    }
    
    console.log('\n📈 Resumen de la migración:');
    console.log(`   ✅ Usuarios actualizados: ${updatedCount}`);
    console.log(`   ⏭️  Usuarios omitidos (ya tenían dirección): ${skippedCount}`);
    console.log(`   ❌ Errores: ${errorCount}`);
    console.log('\n✨ Migración completada exitosamente!');
    
  } catch (error) {
    console.error('❌ Error durante la migración:', error);
    throw error;
  }
}

// Ejecutar la migración
migrateUserAddresses()
  .then(() => {
    console.log('\n🎉 Proceso finalizado');
    process.exit(0);
  })
  .catch((error) => {
    console.error('\n💥 Error fatal:', error);
    process.exit(1);
  });

