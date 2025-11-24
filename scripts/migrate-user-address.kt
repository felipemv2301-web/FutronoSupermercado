/**
 * Script de migración en Kotlin para agregar el campo 'address' a usuarios existentes
 * 
 * Este script se puede ejecutar una vez desde la app Android para migrar usuarios existentes.
 * 
 * USO:
 * 1. Agregar esta función a MainActivity o crear una Activity temporal
 * 2. Llamar a migrateUserAddresses() una vez (por ejemplo, en onCreate)
 * 3. Remover el código después de ejecutarlo
 */

package com.example.intento1app.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.tasks.await
import android.util.Log

object UserMigrationHelper {
    private const val TAG = "UserMigration"
    private val db = FirebaseFirestore.getInstance()
    
    /**
     * Migra los usuarios existentes agregando el campo 'address' si no existe
     * 
     * @param onComplete Callback que se ejecuta cuando la migración termina
     * @param onError Callback que se ejecuta si hay un error
     */
    suspend fun migrateUserAddresses(
        onComplete: (updated: Int, skipped: Int) -> Unit = { _, _ -> },
        onError: (Exception) -> Unit = {}
    ) {
        try {
            Log.d(TAG, "🚀 Iniciando migración de direcciones de usuarios...")
            
            val usersRef = db.collection("users")
            val snapshot = usersRef.get().await()
            
            if (snapshot.isEmpty) {
                Log.d(TAG, "✅ No hay usuarios en la base de datos.")
                onComplete(0, 0)
                return
            }
            
            Log.d(TAG, "📊 Total de usuarios encontrados: ${snapshot.size()}")
            
            var updatedCount = 0
            var skippedCount = 0
            val batch: WriteBatch = db.batch()
            var batchCount = 0
            val BATCH_SIZE = 500 // Firestore permite máximo 500 operaciones por batch
            
            for (document in snapshot.documents) {
                val userData = document.data
                
                // Solo actualizar si el campo 'address' no existe o está vacío
                val currentAddress = userData?.get("address") as? String ?: ""
                
                if (currentAddress.isEmpty()) {
                    batch.update(document.reference, mapOf(
                        "address" to "",
                        "updatedAt" to com.google.firebase.Timestamp.now()
                    ))
                    batchCount++
                    updatedCount++
                    
                    // Si el batch está lleno, ejecutarlo y crear uno nuevo
                    if (batchCount >= BATCH_SIZE) {
                        batch.commit().await()
                        Log.d(TAG, "✅ Batch de $batchCount usuarios actualizado")
                        batchCount = 0
                    }
                } else {
                    skippedCount++
                    Log.d(TAG, "⏭️  Usuario ${document.id} ya tiene dirección: \"$currentAddress\"")
                }
            }
            
            // Ejecutar el batch final si hay operaciones pendientes
            if (batchCount > 0) {
                batch.commit().await()
                Log.d(TAG, "✅ Batch final de $batchCount usuarios actualizado")
            }
            
            Log.d(TAG, "📈 Resumen de la migración:")
            Log.d(TAG, "   ✅ Usuarios actualizados: $updatedCount")
            Log.d(TAG, "   ⏭️  Usuarios omitidos (ya tenían dirección): $skippedCount")
            Log.d(TAG, "✨ Migración completada exitosamente!")
            
            onComplete(updatedCount, skippedCount)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error durante la migración:", e)
            onError(e)
        }
    }
}

