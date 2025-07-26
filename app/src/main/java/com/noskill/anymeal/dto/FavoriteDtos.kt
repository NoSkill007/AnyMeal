// --- PASO 1: DTOs (Data Transfer Objects) ---
// Archivo: dto/FavoriteDtos.kt
// Propósito: Define la clase para la petición de favoritos.
package com.noskill.anymeal.dto

import com.google.gson.annotations.SerializedName

// DTO para la petición de añadir o quitar una receta de favoritos.
data class FavoriteRequest(
    @SerializedName("recipeId") val recipeId: Long
)