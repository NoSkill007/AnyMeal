/* --------------------------------------------------------------------
 * Archivo: RecipeDetailResponse.kt
 * Propósito: Define la estructura de los detalles de una receta que vienen del backend.
 *            Este modelo se utiliza para mapear la respuesta detallada de una receta,
 *            incluyendo información general, ingredientes y pasos.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.dto

// Modelo de datos para los detalles completos de una receta.
// Debe coincidir exactamente con la estructura enviada por el backend.
data class RecipeDetailResponse(
    val id: Long, // ID único de la receta
    val title: String, // Título de la receta
    val imageUrl: String, // URL de la imagen de la receta
    val readyInMinutes: String, // Tiempo estimado de preparación en minutos
    val difficulty: String, // Dificultad de la receta
    val category: String, // Categoría de la receta
    val description: String, // Descripción general de la receta
    val ingredients: List<String>, // Lista de ingredientes necesarios
    val steps: List<String> // Lista de pasos para preparar la receta
)
