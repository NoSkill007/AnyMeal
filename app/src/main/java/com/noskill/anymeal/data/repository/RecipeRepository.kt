// ========================================================================
// Archivo MODIFICADO: data/repository/RecipeRepository.kt
// Propósito: Asegurar que el repositorio pase el query a la ApiService.
// ========================================================================
package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService

class RecipeRepository(private val apiService: ApiService) {
    suspend fun getAllRecipes(query: String?) = apiService.getAllRecipes(query)
    suspend fun getRecipeById(id: Long) = apiService.getRecipeById(id)
}