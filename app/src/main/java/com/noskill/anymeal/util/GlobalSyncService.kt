/*
 * GlobalSyncService.kt
 *
 * Servicio global que mantiene la sincronización automática activa
 * independientemente de qué pantalla esté visible
 */

package com.noskill.anymeal.util

import android.app.Application
import android.util.Log
import com.noskill.anymeal.data.di.NetworkModule
import com.noskill.anymeal.data.repository.ShoppingListRepository
import com.noskill.anymeal.dto.GenerateListRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Servicio singleton que mantiene la sincronización automática
 * entre el plan y la shopping list activa en todo momento
 */
object GlobalSyncService {

    private var isInitialized = false
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var repository: ShoppingListRepository

    /**
     * Inicializa el servicio global de sincronización
     * Debe llamarse desde Application.onCreate()
     */
    fun initialize(application: Application) {
        if (isInitialized) return

        Log.d("GlobalSyncService", "🌐 INICIALIZANDO servicio global de sincronización")

        // Inicializar repositorio
        val apiService = NetworkModule.provideApiService(application)
        repository = ShoppingListRepository(apiService)

        // Iniciar escucha de notificaciones
        startListening()

        isInitialized = true
        Log.d("GlobalSyncService", "✅ Servicio global de sincronización INICIADO")
    }

    /**
     * Inicia la escucha de notificaciones del plan
     */
    private fun startListening() {
        serviceScope.launch {
            PlanChangeNotifier.planChanged.collect { event ->
                Log.d("GlobalSyncService", "🔔 EVENTO_GLOBAL recibido: ${event.action} en fecha: ${event.modifiedDate}")

                when (event.action) {
                    PlanAction.RECIPE_ADDED -> {
                        Log.d("GlobalSyncService", "🍳 Nueva receta agregada - Sincronizando ingredientes")
                        syncNewRecipeIngredients()
                    }
                    PlanAction.RECIPE_REMOVED -> {
                        Log.d("GlobalSyncService", "🗑️ Receta eliminada - Refrescando lista")
                        refreshCurrentList()
                    }
                    PlanAction.RECIPE_EDITED -> {
                        Log.d("GlobalSyncService", "✏️ Receta editada - Refrescando lista")
                        refreshCurrentList()
                    }
                    else -> {
                        Log.d("GlobalSyncService", "ℹ️ Evento ignorado: ${event.action}")
                    }
                }
            }
        }
    }

    /**
     * Sincroniza ingredientes de nuevas recetas agregadas al plan
     * Estrategia: Forzar regeneración para obtener ingredientes nuevos
     */
    private suspend fun syncNewRecipeIngredients() {
        try {
            Log.d("GlobalSyncService", "🍳 SYNC_NUEVA_RECETA: Obteniendo ingredientes de receta nueva")

            // Paso 1: Guardar lista actual para preservar ítems manuales
            val currentListResponse = repository.getCurrentShoppingList()
            if (!currentListResponse.isSuccessful) {
                Log.e("GlobalSyncService", "❌ Error obteniendo lista actual: ${currentListResponse.code()}")
                return
            }

            val currentItems = currentListResponse.body()?.itemsByCategory ?: emptyMap()
            val manualItems = identifyManualItems(currentItems)

            Log.d("GlobalSyncService", "🟢 Items manuales identificados: ${manualItems.size}")

            // Paso 2: Regenerar lista completa para obtener nuevos ingredientes
            val today = LocalDate.now()
            val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
            val endOfWeek = startOfWeek.plusDays(6)

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val startDate = startOfWeek.format(formatter)
            val endDate = endOfWeek.format(formatter)

            Log.d("GlobalSyncService", "🔄 FORZANDO regeneración para obtener ingredientes nuevos")
            val request = GenerateListRequest(startDate, endDate)
            val generateResponse = repository.generateAndGetListForWeek(request)

            if (!generateResponse.isSuccessful) {
                Log.e("GlobalSyncService", "❌ Error regenerando lista: ${generateResponse.code()}")
                return
            }

            Log.d("GlobalSyncService", "✅ Lista regenerada - nuevos ingredientes obtenidos")

            // Paso 3: Reagregar ítems manuales
            if (manualItems.isNotEmpty()) {
                Log.d("GlobalSyncService", "🔄 Reintegrando ${manualItems.size} ítems manuales")
                for (item in manualItems) {
                    reagregarItemManual(item)
                }
            }

            // Notificar actualización
            ShoppingListUpdateNotifier.notifyListUpdated()
            Log.d("GlobalSyncService", "✅ SYNC_NUEVA_RECETA: Completado exitosamente")

        } catch (e: Exception) {
            Log.e("GlobalSyncService", "❌ SYNC_NUEVA_RECETA: Excepción", e)
        }
    }

    /**
     * Solo refresca la lista actual sin regenerar (para eliminaciones/ediciones)
     */
    private suspend fun refreshCurrentList() {
        try {
            Log.d("GlobalSyncService", "🔄 REFRESH: Solo actualizando vista actual")

            val currentListResponse = repository.getCurrentShoppingList()
            if (currentListResponse.isSuccessful) {
                Log.d("GlobalSyncService", "✅ REFRESH: Lista refrescada preservando cambios")
                ShoppingListUpdateNotifier.notifyListUpdated()
            } else {
                Log.e("GlobalSyncService", "❌ REFRESH: Error ${currentListResponse.code()}")
            }
        } catch (e: Exception) {
            Log.e("GlobalSyncService", "❌ REFRESH: Excepción", e)
        }
    }

    /**
     * Identifica ítems que fueron agregados manualmente por el usuario
     */
    private fun identifyManualItems(itemsByCategory: Map<String, List<Any>>): List<ManualItem> {
        val manualItems = mutableListOf<ManualItem>()

        itemsByCategory.forEach { (category, items) ->
            items.forEach { item ->
                val itemMap = item as? Map<*, *>
                val unit = itemMap?.get("unit") as? String
                val name = itemMap?.get("name") as? String
                val amount = itemMap?.get("amount")

                // Identificar ítems manuales por patrones específicos
                val isManual = unit?.contains("Comprar") == true ||
                              unit?.contains("cartón") == true ||
                              unit?.contains("docena") == true ||
                              unit?.contains("kg") == true ||
                              unit?.contains("pack") == true

                if (isManual && name != null) {
                    manualItems.add(ManualItem(name, amount, unit, category))
                    Log.d("GlobalSyncService", "🟢 MANUAL identificado: $name ($unit)")
                }
            }
        }

        return manualItems
    }

    /**
     * Reagrega un ítem manual usando la API de agregar ítem
     */
    private suspend fun reagregarItemManual(item: ManualItem) {
        try {
            val parts = item.unit?.split(" ") ?: listOf()
            val amountValue = item.amount as? Double
            val unitValue = if (parts.size > 1) parts.drop(1).joinToString(" ") else item.unit

            val request = com.noskill.anymeal.dto.AddItemRequest(
                customName = item.name,
                amount = amountValue,
                unit = unitValue,
                category = "Añadido manualmente"
            )

            Log.d("GlobalSyncService", "🔄 Reagregando ítem manual: ${item.name}")
            val response = repository.addItem(request)

            if (response.isSuccessful) {
                Log.d("GlobalSyncService", "✅ Ítem manual reagregado: ${item.name}")
            } else {
                Log.e("GlobalSyncService", "❌ Error reagregando ítem manual: ${response.code()}")
            }

        } catch (e: Exception) {
            Log.e("GlobalSyncService", "❌ Excepción reagregando ítem manual", e)
        }
    }

    /**
     * Data class para representar ítems manuales
     */
    data class ManualItem(
        val name: String,
        val amount: Any?,
        val unit: String?,
        val category: String
    )

    /**
     * Notificador específico para actualizaciones de la shopping list
     * Permite que el UI se entere cuando la lista fue actualizada externamente
     */
    object ShoppingListUpdateNotifier {

        private val _listUpdated = MutableSharedFlow<Long>(
            replay = 0,
            extraBufferCapacity = 10
        )

        val listUpdated: SharedFlow<Long> = _listUpdated.asSharedFlow()

        suspend fun notifyListUpdated() {
            val timestamp = System.currentTimeMillis()
            _listUpdated.emit(timestamp)
            Log.d("ShoppingListUpdateNotifier", "🔔 Lista actualizada - timestamp: $timestamp")
        }
    }
}
