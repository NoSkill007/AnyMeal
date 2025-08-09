/**
 * UiModels.kt
 *
 * Propósito: Define los modelos de datos utilizados exclusivamente por la capa de UI
 * de la aplicación. Estos modelos presentan la información en un formato optimizado para
 * su visualización en los componentes de la interfaz de usuario, separando la representación
 * visual de los modelos de dominio o de red.
 */
package com.noskill.anymeal.ui.models

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Modelo de datos para la vista previa de una receta en la UI.
 * Contiene información resumida que se muestra en las listas, tarjetas y grids de recetas.
 *
 * @property id Identificador único de la receta (tipo Int para compatibilidad con la UI)
 * @property title Título de la receta
 * @property time Tiempo de preparación en formato legible (ej. "30 min")
 * @property difficulty Nivel de dificultad de la receta (ej. "Fácil", "Medio", "Difícil")
 * @property imageUrl URL de la imagen de la receta, null si no tiene imagen
 * @property category Categoría a la que pertenece la receta (ej. "Desayuno", "Vegetariano")
 */
data class RecipePreviewUi(
    val id: Int, // <-- CORREGIDO A INT
    val title: String,
    val time: String,
    val difficulty: String,
    val imageUrl: String? = null,
    val category: String
)

/**
 * Modelo de datos que representa una categoría de recetas en la UI.
 * Utilizado para mostrar opciones de filtrado y navegación por categorías.
 *
 * @property name Nombre de la categoría (ej. "Vegetariano", "Postres")
 * @property icon Icono vectorial que representa visualmente la categoría
 */
data class CategoryUi(
    val name: String,
    val icon: ImageVector
)

/**
 * Modelo de datos que representa un elemento de navegación en la UI.
 * Utilizado para construir menús, barras de navegación y drawers.
 *
 * @property label Texto visible que describe la sección de navegación
 * @property icon Icono vectorial que representa visualmente la sección
 * @property route Ruta de navegación asociada a este elemento (define la pantalla destino)
 */
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Modelo de datos para la vista detallada de una receta en la UI.
 * Contiene toda la información necesaria para mostrar la pantalla de detalle de una receta.
 *
 * @property id Identificador único de la receta
 * @property title Título de la receta
 * @property time Tiempo de preparación en formato legible
 * @property difficulty Nivel de dificultad de la receta
 * @property imageUrl URL de la imagen de la receta, null si no tiene imagen
 * @property description Descripción general o introducción a la receta
 * @property ingredients Lista de ingredientes necesarios para la receta
 * @property steps Lista de pasos para la preparación de la receta
 * @property category Categoría a la que pertenece la receta
 */
data class RecipeDetailUi(
    val id: Int, // <-- Ya era Int, se mantiene
    val title: String,
    val time: String,
    val difficulty: String,
    val imageUrl: String? = null,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val category: String
)