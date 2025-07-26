// ========================================================================
// Archivo NUEVO: dto/RecipePreviewResponse.kt
// Propósito: Define la estructura de la lista de recetas que viene del backend.
// ========================================================================
package com.noskill.anymeal.dto

// Esta data class debe coincidir exactamente con RecipePreviewResponse.java del backend.
data class RecipePreviewResponse(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val readyInMinutes: String,
    val difficulty: String,
    val category: String
)