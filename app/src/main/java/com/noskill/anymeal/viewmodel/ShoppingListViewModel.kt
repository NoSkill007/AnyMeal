// --- PASO 4: ViewModel SIN HILT ---
// Archivo: viewmodel/ShoppingListViewModel.kt
package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.repository.ShoppingListRepository
import com.noskill.anymeal.di.NetworkModule
import com.noskill.anymeal.dto.AddItemRequest
import com.noskill.anymeal.dto.GenerateListRequest
import com.noskill.anymeal.ui.models.ShoppingItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ShoppingListUiState(
    val isLoading: Boolean = true,
    val shoppingList: Map<String, List<ShoppingItem>> = emptyMap(),
    val error: String? = null
)

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = NetworkModule.provideApiService(application)
    private val repository = ShoppingListRepository(apiService)

    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()

    private var currentWeekOffset = 0

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

    fun addItem(name: String, quantity: String) {
        viewModelScope.launch {
            val parts = quantity.split(" ")
            val amount = parts.firstOrNull()?.toDoubleOrNull()
            val unit = if (parts.size > 1) parts.drop(1).joinToString(" ") else null

            val request = AddItemRequest(name, amount, unit, "Añadido manualmente")
            try {
                val response = repository.addItem(request)
                if(response.isSuccessful) {
                    generateListForWeek(currentWeekOffset) // Recargar la semana actual
                } else {
                    _uiState.update { it.copy(error = "No se pudo añadir el ítem") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexión") }
            }
        }
    }


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