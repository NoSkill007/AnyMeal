// --- PASO 3: Repositorio de la Lista de Compras ---
// Archivo: data/repository/ShoppingListRepository.kt
package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.* // Import corregido

class ShoppingListRepository(private val apiService: ApiService) {

    suspend fun generateAndGetListForWeek(request: GenerateListRequest) = apiService.generateAndGetShoppingList(request)
    suspend fun addItem(request: AddItemRequest) = apiService.addItem(request)
    suspend fun updateItem(itemId: Long, isChecked: Boolean) = apiService.updateItem(itemId, UpdateItemRequest(isChecked))
    suspend fun clearCheckedItems() = apiService.clearCheckedItems()
}