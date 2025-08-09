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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ShoppingListUiState(
    val isLoading: Boolean = true,
    val shoppingList: Map<String, List<ShoppingItem>> = emptyMap(),
    val error: String? = null,
    val successMessage: String? = null
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

                val request = EditItemRequest(name, amount, unit)
                Log.d("ShoppingListVM", "🔵 EDIT_ITEM: Request preparado: $request")

                // Editar directamente en el backend
                Log.d("ShoppingListVM", "🔵 EDIT_ITEM: Enviando edición al backend")
                val editResponse = repository.editItem(itemId, request)
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
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, weekOffset)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val startDate = formatter.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        val endDate = formatter.format(calendar.time)
        return Pair(startDate, endDate)
    }
}