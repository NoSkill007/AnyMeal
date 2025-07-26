// ========================================================================
// Archivo NUEVO: dto/RecipeDetailResponse.kt
// Propósito: Define la estructura de los detalles de una receta que vienen del backend.
// ========================================================================
package com.noskill.anymeal.dto

// Esta data class debe coincidir exactamente con RecipeDetailResponse.java del backend.
data class RecipeDetailResponse(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val readyInMinutes: String,
    val difficulty: String,
    val category: String,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>
)