/**
 * PlanEntry.kt
 *
 * Propósito: Define el modelo de datos para representar una entrada individual dentro
 * de un plan de comidas. Cada entrada vincula una receta específica a un plan diario,
 * manteniendo identificadores locales y del backend para la sincronización de datos.
 */
package com.noskill.anymeal.ui.models

/**
 * Modelo de datos que representa una entrada individual en un plan de comidas.
 * Cada PlanEntry conecta una receta específica con un momento de comida en un plan diario.
 *
 * @property id Identificador local de la entrada en el plan (usado para operaciones en la app)
 * @property backendId Identificador remoto en el servidor (usado para sincronización)
 * @property recipe Información de vista previa de la receta vinculada a esta entrada del plan
 */
data class PlanEntry(
        val id: Long, // Debe ser Long
        val backendId: Long,
        val recipe: RecipePreviewUi
)