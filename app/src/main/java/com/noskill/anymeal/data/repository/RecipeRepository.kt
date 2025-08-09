// ========================================================================
// Archivo: RecipeRepository.kt
// Propósito: Gestiona las operaciones de red relacionadas con las recetas,
//            permitiendo obtener todas las recetas (con filtro opcional) y
//            obtener el detalle de una receta específica por su ID.
// ========================================================================

package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService

// RecipeRepository se encarga de interactuar con el ApiService para obtener
// la información de recetas y detalles de recetas individuales.
class RecipeRepository(private val apiService: ApiService) {
    // Obtiene todas las recetas, opcionalmente filtradas por un parámetro de búsqueda (query).
    suspend fun getAllRecipes(query: String?) = apiService.getAllRecipes(query)

    // Obtiene el detalle de una receta específica por su ID.
    suspend fun getRecipeById(id: Long) = apiService.getRecipeById(id)
}
