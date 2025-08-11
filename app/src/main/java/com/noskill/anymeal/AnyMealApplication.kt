/*
 * AnyMealApplication.kt
 *
 * Clase Application personalizada que inicializa servicios globales
 * de la aplicación, incluyendo el sistema de sincronización automática
 */

package com.noskill.anymeal

import android.app.Application
import android.util.Log
import com.noskill.anymeal.util.GlobalSyncService

/**
 * Clase Application personalizada para AnyMeal
 * Se ejecuta cuando la aplicación se inicia por primera vez
 */
class AnyMealApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("AnyMealApplication", "🚀 APLICACIÓN INICIANDO - Configurando servicios globales")

        // Inicializar el servicio global de sincronización
        GlobalSyncService.initialize(this)

        Log.d("AnyMealApplication", "✅ Servicios globales configurados correctamente")
    }
}
