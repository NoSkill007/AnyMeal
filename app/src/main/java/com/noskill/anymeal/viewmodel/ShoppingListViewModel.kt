// ========================================================================
// Archivo: ShoppingListViewModel.kt
// Propósito: Gestiona el estado y la lógica relacionada con la lista de compras.
//            Permite obtener, agregar, editar, actualizar y eliminar elementos
//            de la lista de compras, así como manejar mensajes de éxito y error.
// ========================================================================

package com.noskill.anymeal.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.repository.ShoppingListRepository
import com.noskill.anymeal.data.di.NetworkModule
import com.noskill.anymeal.dto.AddItemRequest
import com.noskill.anymeal.dto.EditItemRequest
import com.noskill.anymeal.dto.GenerateListRequest
import com.noskill.anymeal.ui.models.ShoppingItem
import com.noskill.anymeal.util.PlanChangeNotifier // AGREGADO: Import del notificador
import com.noskill.anymeal.util.GlobalSyncService // AGREGADO: Import del servicio global
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ShoppingListUiState(
    val isLoading: Boolean = true,
    val shoppingList: Map<String, List<ShoppingItem>> = emptyMap(),
    val error: String? = null,
    val successMessage: String? = null,
    val isAutoSyncing: Boolean = false // Nuevo: indica si está sincronizando automáticamente
)

// ShoppingListViewModel extiende AndroidViewModel y gestiona el estado de la lista de compras.
class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del servicio de red y repositorio de lista de compras.
    private val apiService = NetworkModule.provideApiService(application)
    private val repository = ShoppingListRepository(apiService)

    // StateFlow privado para almacenar el estado de la UI de la lista de compras.
    private val _uiState = MutableStateFlow(ShoppingListUiState())
    // StateFlow público que expone el estado de la UI a los observadores.
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()

    // Desplazamiento de semana actual para generar la lista.
    private var currentWeekOffset = 0

    /**
     * NUEVA FUNCIONALIDAD: Regenera automáticamente la lista basada en el plan actual
     * Se llama cuando hay cambios en el plan para mantener sincronización
     */
    fun autoRegenerateFromPlan(weekOffset: Int = currentWeekOffset) {
        Log.d("ShoppingListVM", "🔄 AUTO_REGENERATE: Iniciando regeneración automática para weekOffset=$weekOffset")
        viewModelScope.launch {
            _uiState.update { it.copy(isAutoSyncing = true, error = null) }
            try {
                val (startDate, endDate) = getWeekDateStrings(weekOffset)
                Log.d("ShoppingListVM", "🔄 AUTO_REGENERATE: Generando lista para rango $startDate - $endDate")

                val response = repository.generateAndGetListForWeek(GenerateListRequest(startDate, endDate))
                if (response.isSuccessful && response.body() != null) {
                    val domainModel = response.body()!!.itemsByCategory.mapValues { entry ->
                        entry.value.map { dto ->
                            val quantityStr = listOfNotNull(dto.amount?.toString(), dto.unit).joinToString(" ").trim()
                            ShoppingItem(dto.id, dto.name, quantityStr, dto.category ?: "Otros", dto.isChecked)
                        }
                    }
                    _uiState.update {
                        it.copy(
                            isAutoSyncing = false,
                            isLoading = false,
                            shoppingList = domainModel,
                            successMessage = "Lista actualizada automáticamente"
                        )
                    }
                    Log.d("ShoppingListVM", "🔄 AUTO_REGENERATE: ✅ Lista regenerada exitosamente")
                } else {
                    _uiState.update {
                        it.copy(
                            isAutoSyncing = false,
                            isLoading = false,
                            error = "Error al sincronizar: ${response.code()}"
                        )
                    }
                    Log.e("ShoppingListVM", "🔄 AUTO_REGENERATE: ❌ Error HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAutoSyncing = false,
                        isLoading = false,
                        error = "Error de sincronización: ${e.message}"
                    )
                }
                Log.e("ShoppingListVM", "🔄 AUTO_REGENERATE: ❌ Excepción", e)
            }
        }
    }

    /**
     * NUEVA FUNCIONALIDAD: Actualización inteligente que preserva cambios manuales
     * Se usa para la semana actual donde el usuario puede haber hecho modificaciones
     */
    fun smartUpdate() {
        Log.d("ShoppingListVM", "🧠 SMART_UPDATE: Iniciando actualización inteligente")
        viewModelScope.launch {
            _uiState.update { it.copy(isAutoSyncing = true, error = null) }
            try {
                // Para la semana actual, primero obtenemos la lista actual que preserva cambios manuales
                val response = repository.getCurrentShoppingList()
                if (response.isSuccessful && response.body() != null) {
                    val domainModel = response.body()!!.itemsByCategory.mapValues { entry ->
                        entry.value.map { dto ->
                            val quantityStr = listOfNotNull(dto.amount?.toString(), dto.unit).joinToString(" ").trim()
                            ShoppingItem(dto.id, dto.name, quantityStr, dto.category ?: "Otros", dto.isChecked)
                        }
                    }
                    _uiState.update {
                        it.copy(
                            isAutoSyncing = false,
                            isLoading = false,
                            shoppingList = domainModel,
                            successMessage = "Lista actualizada preservando cambios"
                        )
                    }
                    Log.d("ShoppingListVM", "🧠 SMART_UPDATE: ✅ Actualización inteligente completada")
                } else {
                    _uiState.update {
                        it.copy(
                            isAutoSyncing = false,
                            isLoading = false,
                            error = "Error en actualización inteligente: ${response.code()}"
                        )
                    }
                    Log.e("ShoppingListVM", "🧠 SMART_UPDATE: ❌ Error HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAutoSyncing = false,
                        isLoading = false,
                        error = "Error en actualización inteligente: ${e.message}"
                    )
                }
                Log.e("ShoppingListVM", "🧠 SMART_UPDATE: ❌ Excepción", e)
            }
        }
    }

    // Genera la lista de compras para la semana especificada por el desplazamiento.
    fun generateListForWeek(weekOffset: Int) {
        currentWeekOffset = weekOffset
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val (startDate, endDate) = getWeekDateStrings(weekOffset)
                val response = repository.generateAndGetListForWeek(GenerateListRequest(startDate, endDate))
                if (response.isSuccessful && response.body() != null) {
                    val domainModel = response.body()!!.itemsByCategory.mapValues { entry ->
                        entry.value.map { dto ->
                            val quantityStr = listOfNotNull(dto.amount?.toString(), dto.unit).joinToString(" ").trim()
                            ShoppingItem(dto.id, dto.name, quantityStr, dto.category ?: "Otros", dto.isChecked)
                        }
                    }
                    _uiState.update { it.copy(isLoading = false, shoppingList = domainModel) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error de conexión: ${e.message}") }
            }
        }
    }

    // Alterna el estado de verificación de un ítem en la lista.
    fun toggleItemChecked(itemId: Long) {
        val currentList = _uiState.value.shoppingList
        var itemToUpdate: ShoppingItem? = null

        val updatedMap = currentList.mapValues { (_, items) ->
            items.map { item ->
                if (item.id == itemId) {
                    itemToUpdate = item.copy(isChecked = !item.isChecked)
                    itemToUpdate!!
                } else item
            }
        }
        _uiState.update { it.copy(shoppingList = updatedMap) }

        itemToUpdate?.let { updatedItem ->
            viewModelScope.launch {
                try {
                    repository.updateItem(updatedItem.id, updatedItem.isChecked)
                } catch (e: Exception) {
                    _uiState.update { it.copy(shoppingList = currentList, error = "Error de sincronización") }
                }
            }
        }
    }

    // Limpia los ítems marcados como comprados en la lista.
    fun clearCheckedItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.clearCheckedItems()
                if(response.isSuccessful) {
                    generateListForWeek(currentWeekOffset)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error al limpiar") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error de conexión") }
            }
        }
    }

    // Agrega un nuevo ítem a la lista de compras.
    fun addItem(name: String, quantity: String) {
        Log.d("ShoppingListVM", "🟢 ADD_ITEM: Iniciando - name='$name', quantity='$quantity'")
        viewModelScope.launch {
            try {
                val parts = quantity.split(" ")
                val amount = parts.firstOrNull()?.toDoubleOrNull()
                val unit = if (parts.size > 1) parts.drop(1).joinToString(" ") else null

                val request = AddItemRequest(name, amount, unit, "Añadido manualmente")
                Log.d("ShoppingListVM", "🟢 ADD_ITEM: Request creado - $request")

                val response = repository.addItem(request)
                Log.d("ShoppingListVM", "🟢 ADD_ITEM: Response recibido - isSuccessful=${response.isSuccessful}, code=${response.code()}")

                if (response.isSuccessful) {
                    Log.d("ShoppingListVM", "🟢 ADD_ITEM: ✅ Éxito! Llamando getCurrentList()")
                    getCurrentList()
                    _uiState.update { it.copy(successMessage = "Ítem '$name' añadido correctamente") }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Datos inválidos. Verifica el nombre y cantidad."
                        401 -> "Sesión expirada. Inicia sesión nuevamente."
                        500 -> "Error del servidor. Intenta más tarde."
                        else -> "No se pudo añadir el ítem (${response.code()})"
                    }
                    Log.e("ShoppingListVM", "🟢 ADD_ITEM: ❌ Error HTTP - $errorMsg")
                    _uiState.update { it.copy(error = errorMsg) }
                }
            } catch (e: Exception) {
                Log.e("ShoppingListVM", "🟢 ADD_ITEM: ❌ Excepción - ${e.localizedMessage}", e)
                _uiState.update { it.copy(error = "Error de conexión: ${e.localizedMessage}") }
            }
        }
    }

    // Limpia los mensajes de error y éxito en el estado de la UI.
    fun clearError() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    // Deselecciona un ítem en la lista (lo desmarca como comprado).
    fun unselectItem(itemId: Long) {
        val currentList = _uiState.value.shoppingList
        val updatedMap = currentList.mapValues { (_, items) ->
            items.map { item ->
                if (item.id == itemId) {
                    item.copy(isChecked = false)
                } else item
            }
        }
        _uiState.update { it.copy(shoppingList = updatedMap) }
    }

    // Elimina un ítem de la lista de compras.
    fun deleteItem(itemId: Long) {
        Log.d("ShoppingListVM", "🔴 DELETE_ITEM: Iniciando - itemId=$itemId")
        viewModelScope.launch {
            try {
                // Eliminar directamente del backend sin desmarcar primero
                Log.d("ShoppingListVM", "🔴 DELETE_ITEM: Eliminando del backend")
                val deleteResponse = repository.deleteItem(itemId)
                Log.d("ShoppingListVM", "🔴 DELETE_ITEM: Response delete: isSuccessful=${deleteResponse.isSuccessful}, code=${deleteResponse.code()}")

                if (deleteResponse.isSuccessful) {
                    Log.d("ShoppingListVM", "🔴 DELETE_ITEM: ✅ Éxito! Llamando getCurrentList()")
                    getCurrentList()
                    _uiState.update { it.copy(successMessage = "Ítem eliminado correctamente") }
                } else {
                    val errorMsg = when (deleteResponse.code()) {
                        404 -> "El ítem no existe o ya fue eliminado."
                        401 -> "Sesión expirada. Inicia sesión nuevamente."
                        403 -> "No tienes permisos para eliminar este ítem."
                        500 -> "Error del servidor. Intenta más tarde."
                        else -> "No se pudo eliminar el ítem (${deleteResponse.code()})"
                    }
                    Log.e("ShoppingListVM", "🔴 DELETE_ITEM: ❌ Error HTTP - $errorMsg")
                    _uiState.update { it.copy(error = errorMsg) }
                }
            } catch (e: Exception) {
                Log.e("ShoppingListVM", "🔴 DELETE_ITEM: ❌ Excepción - ${e.localizedMessage}", e)
                _uiState.update { it.copy(error = "Error de conexión al eliminar: ${e.localizedMessage}") }
            }
        }
    }

    // Edita un ítem existente en la lista de compras.
    fun editItem(itemId: Long, name: String, quantity: String) {
        Log.d("ShoppingListVM", "🔵 EDIT_ITEM: Iniciando - itemId=$itemId, name='$name', quantity='$quantity'")
        viewModelScope.launch {
            try {
                // Preparar los datos para editar
                val parts = quantity.split(" ")
                val amount = parts.firstOrNull()?.toDoubleOrNull()
                val unit = if (parts.size > 1) parts.drop(1).joinToString(" ") else null

                // CORREGIDO: Usar nombres de parámetros explícitos para evitar confusión
                val request = EditItemRequest(
                    customName = name,
                    amount = amount,
                    unit = unit
                )
                Log.d("ShoppingListVM", "🔵 EDIT_ITEM: Request preparado: $request")

                // Editar directamente en el backend con timeout
                Log.d("ShoppingListVM", "🔵 EDIT_ITEM: Enviando edición al backend")

                // CORREGIDO: Agregar timeout y mejor manejo
                val editResponse = kotlinx.coroutines.withTimeoutOrNull(10000) { // 10 segundos timeout
                    repository.editItem(itemId, request)
                }

                if (editResponse == null) {
                    Log.e("ShoppingListVM", "🔵 EDIT_ITEM: ❌ TIMEOUT - El servidor no respondió en 10 segundos")
                    _uiState.update { it.copy(error = "Timeout: El servidor no responde. Intenta más tarde.") }
                    return@launch
                }

                Log.d("ShoppingListVM", "🔵 EDIT_ITEM: Response edit: isSuccessful=${editResponse.isSuccessful}, code=${editResponse.code()}")

                if (editResponse.isSuccessful) {
                    Log.d("ShoppingListVM", "🔵 EDIT_ITEM: ✅ Éxito! Llamando getCurrentList()")
                    getCurrentList()
                    _uiState.update { it.copy(successMessage = "Ítem editado correctamente") }
                } else {
                    val errorMsg = when (editResponse.code()) {
                        400 -> "Datos inválidos. Verifica el nombre y cantidad."
                        401 -> "Sesión expirada. Inicia sesión nuevamente."
                        403 -> "No tienes permisos para editar este ítem."
                        404 -> "El ítem no existe o ya fue eliminado."
                        500 -> "Error del servidor. Intenta más tarde."
                        else -> "No se pudo editar el ítem (${editResponse.code()})"
                    }
                    Log.e("ShoppingListVM", "🔵 EDIT_ITEM: ❌ Error HTTP - $errorMsg")
                    _uiState.update { it.copy(error = errorMsg) }
                }
            } catch (e: Exception) {
                Log.e("ShoppingListVM", "🔵 EDIT_ITEM: ❌ Excepción - ${e.localizedMessage}", e)
                _uiState.update { it.copy(error = "Error de conexión al editar: ${e.localizedMessage}") }
            }
        }
    }

    // Obtiene la lista de compras actual del usuario.
    fun getCurrentList() {
        Log.d("ShoppingListVM", "📋 GET_CURRENT_LIST: Iniciando")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = repository.getCurrentShoppingList()
                Log.d("ShoppingListVM", "📋 GET_CURRENT_LIST: Response recibido - isSuccessful=${response.isSuccessful}, code=${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val itemCount = response.body()!!.itemsByCategory.values.sumOf { it.size }
                    Log.d("ShoppingListVM", "📋 GET_CURRENT_LIST: ✅ Éxito! Items encontrados: $itemCount")

                    val domainModel = response.body()!!.itemsByCategory.mapValues { entry ->
                        entry.value.map { dto ->
                            val quantityStr = listOfNotNull(dto.amount?.toString(), dto.unit).joinToString(" ").trim()
                            ShoppingItem(dto.id, dto.name, quantityStr, dto.category ?: "Otros", dto.isChecked)
                        }
                    }
                    _uiState.update { it.copy(isLoading = false, shoppingList = domainModel) }
                } else {
                    Log.e("ShoppingListVM", "📋 GET_CURRENT_LIST: ❌ Error HTTP - code=${response.code()}")
                    _uiState.update { it.copy(isLoading = false, error = "Error: ${response.code()}") }
                }
            } catch (e: Exception) {
                Log.e("ShoppingListVM", "📋 GET_CURRENT_LIST: ❌ Excepción - ${e.localizedMessage}", e)
                _uiState.update { it.copy(isLoading = false, error = "Error de conexión: ${e.message}") }
            }
        }
    }


    // Función privada que obtiene las fechas de inicio y fin de la semana para el desplazamiento dado.
    private fun getWeekDateStrings(weekOffset: Int): Pair<String, String> {
        // CORREGIDO: Usar LocalDate para consistencia con PlanScreen
        val today = java.time.LocalDate.now()
        val startOfCurrentWeek = today.with(java.time.DayOfWeek.MONDAY)
        val targetWeekStart = startOfCurrentWeek.plusWeeks(weekOffset.toLong())
        val targetWeekEnd = targetWeekStart.plusDays(6) // Domingo

        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startDate = targetWeekStart.format(formatter)
        val endDate = targetWeekEnd.format(formatter)

        Log.d("ShoppingListVM", "🗓️ FECHAS_CALCULADAS: weekOffset=$weekOffset, start=$startDate, end=$endDate")
        return Pair(startDate, endDate)
    }

    /**
     * NUEVA FUNCIONALIDAD: Inicia la escucha de notificaciones de plan
     * Se ejecuta al inicializar el ViewModel para mantener sincronización constante
     */
    init {
        // Escuchar actualizaciones globales de la shopping list
        viewModelScope.launch {
            GlobalSyncService.ShoppingListUpdateNotifier.listUpdated.collect { timestamp ->
                Log.d("ShoppingListVM", "🔔 LISTA_ACTUALIZADA_GLOBALMENTE recibida - timestamp: $timestamp")

                // Recargar la lista actual cuando fue actualizada externamente
                getCurrentList()
            }
        }

        Log.d("ShoppingListVM", "🎯 ShoppingListViewModel INICIALIZADO - Escuchando actualizaciones globales")
    }
}