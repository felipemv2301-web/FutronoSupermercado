/**
 * EJEMPLO: Cómo ejecutar la migración desde MainActivity
 * 
 * INSTRUCCIONES:
 * 1. Copiar el código de UserMigrationHelper.kt a tu proyecto
 * 2. Agregar este código temporalmente en MainActivity
 * 3. Ejecutar la app una vez
 * 4. Remover este código después de verificar que la migración se completó
 */

// EJEMPLO DE USO EN MainActivity.kt:

/*
import com.example.intento1app.utils.UserMigrationHelper
import androidx.compose.runtime.LaunchedEffect

// Dentro de SimpleFutronoApp() o donde corresponda:

LaunchedEffect(Unit) {
    // Verificar si la migración ya se ejecutó
    val prefs = context.getSharedPreferences("migration_prefs", android.content.Context.MODE_PRIVATE)
    val migrationDone = prefs.getBoolean("address_migration_done", false)
    
    if (!migrationDone) {
        android.util.Log.d("Migration", "🚀 Iniciando migración de direcciones...")
        
        UserMigrationHelper.migrateUserAddresses(
            onComplete = { updated, skipped ->
                android.util.Log.d("Migration", "✅ Migración completada:")
                android.util.Log.d("Migration", "   - Usuarios actualizados: $updated")
                android.util.Log.d("Migration", "   - Usuarios omitidos: $skipped")
                
                // Marcar como completada
                prefs.edit().putBoolean("address_migration_done", true).apply()
            },
            onError = { error ->
                android.util.Log.e("Migration", "❌ Error en migración: ${error.message}")
                error.printStackTrace()
            }
        )
    } else {
        android.util.Log.d("Migration", "⏭️  Migración ya ejecutada anteriormente")
    }
}
*/

