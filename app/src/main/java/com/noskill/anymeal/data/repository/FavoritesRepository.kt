// --- PASO 3: Repositorio para Favoritos (NUEVO) ---
// Archivo: data/repository/FavoritesRepository.kt
// Propósito: Maneja las llamadas de red relacionadas con los favoritos.
package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.FavoriteRequest

class FavoritesRepository(private val apiService: ApiService) {
    suspend fun getFavorites() = apiService.getFavorites()
    suspend fun addFavorite(recipeId: Long) = apiService.addFavorite(FavoriteRequest(recipeId))
    suspend fun removeFavorite(recipeId: Long) = apiService.removeFavorite(recipeId)
}