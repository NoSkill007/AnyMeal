/* --------------------------------------------------------------------
 * Archivo: RecipePreviewResponse.kt
 * Propósito: Define la estructura de los datos resumidos de una receta que vienen del backend,
 *            utilizada para mostrar listas de recetas en la interfaz de usuario.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.dto

// Modelo de datos para la respuesta resumida de una receta.
// Debe coincidir exactamente con la estructura enviada por el backend.
data class RecipePreviewResponse(
    val id: Long, // ID único de la receta
    val title: String, // Título de la receta
    val imageUrl: String, // URL de la imagen de la receta
    val readyInMinutes: String, // Tiempo estimado de preparación en minutos
    val difficulty: String, // Dificultad de la receta
    val category: String // Categoría de la receta
)
