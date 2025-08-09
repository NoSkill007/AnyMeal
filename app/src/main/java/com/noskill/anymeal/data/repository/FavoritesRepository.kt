// ========================================================================
// Archivo: FavoritesRepository.kt
// Propósito: Maneja las llamadas de red relacionadas con los favoritos del usuario.
// ========================================================================

// --- PASO 3: Repositorio para Favoritos (NUEVO) ---
// Archivo: data/repository/FavoritesRepository.kt
// Propósito: Maneja las llamadas de red relacionadas con los favoritos.
package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.FavoriteRequest

// FavoritesRepository se encarga de interactuar con el ApiService para gestionar
// las recetas favoritas del usuario (obtener, agregar y eliminar favoritos).
class FavoritesRepository(private val apiService: ApiService) {
    // Obtiene la lista de recetas favoritas del usuario.
    suspend fun getFavorites() = apiService.getFavorites()

    // Agrega una receta a la lista de favoritos del usuario.
    suspend fun addFavorite(recipeId: Long) = apiService.addFavorite(FavoriteRequest(recipeId))

    // Elimina una receta de la lista de favoritos del usuario por su ID.
    suspend fun removeFavorite(recipeId: Long) = apiService.removeFavorite(recipeId)
}