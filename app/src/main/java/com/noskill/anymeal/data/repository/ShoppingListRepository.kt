// ========================================================================
// Archivo: ShoppingListRepository.kt
// Propósito: Gestiona las operaciones de red relacionadas con la lista de compras,
//            permitiendo generar la lista semanal, obtener la lista actual,
//            agregar, editar, actualizar, eliminar y limpiar elementos.
// ========================================================================

package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.* // Import corregido

// ShoppingListRepository se encarga de interactuar con el ApiService para gestionar
// la lista de compras del usuario y sus elementos asociados.
class ShoppingListRepository(private val apiService: ApiService) {
    // Genera y obtiene la lista de compras para la semana según el plan.
    suspend fun generateAndGetListForWeek(request: GenerateListRequest) = apiService.generateAndGetShoppingList(request)

    // Obtiene la lista de compras actual del usuario.
    suspend fun getCurrentShoppingList() = apiService.getCurrentShoppingList()

    // Agrega un nuevo elemento a la lista de compras.
    suspend fun addItem(request: AddItemRequest) = apiService.addItem(request)

    // Actualiza el estado (marcado/no marcado) de un elemento de la lista por su ID.
    suspend fun updateItem(itemId: Long, isChecked: Boolean) = apiService.updateItem(itemId, UpdateItemRequest(isChecked))

    // Edita los detalles de un elemento de la lista por su ID.
    suspend fun editItem(itemId: Long, request: EditItemRequest) = apiService.editItem(itemId, request)

    // Elimina un elemento de la lista de compras por su ID.
    suspend fun deleteItem(itemId: Long) = apiService.deleteItem(itemId)

    // Limpia todos los elementos marcados como comprados en la lista de compras.
    suspend fun clearCheckedItems() = apiService.clearCheckedItems()
}
