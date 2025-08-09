/* --------------------------------------------------------------------
 * Archivo: PlanDtos.kt
 * Propósito: Define los modelos de datos (DTOs) para las operaciones relacionadas
 *            con los planes semanales, incluyendo la petición de agregar recetas,
 *            la estructura de cada día del plan, la actualización de notas y
 *            la respuesta del plan semanal.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.dto


// Modelo de datos para la petición de agregar una receta al plan semanal.
data class PlanRequest(
    val recipeId: Long, // ID de la receta a agregar
    val date: String,   // Fecha en formato String (día del plan)
    val mealType: String // Tipo de comida (ej: desayuno, almuerzo, cena)
)

// Modelo de datos para la información de un día dentro del plan semanal.
data class DailyPlanDto(
    val id: Long, // ID único del día en el plan
    val planDate: String, // Fecha del día en formato String
    val notes: String?,   // Notas opcionales para el día
    val meals: Map<String, List<PlanEntryDto>> // Mapa de tipo de comida a lista de recetas
)

// Modelo de datos para la petición de actualizar las notas de un plan.
data class NotesRequest(
    val notes: String // Texto de las notas a actualizar
)

// Modelo de datos para la respuesta que contiene el plan semanal completo.
data class PlanResponse(
    val dailyPlans: Map<String, DailyPlanDto> // Mapa de fecha a información del día
)

// Modelo de datos para una entrada individual en el plan (receta asignada a una comida).
data class PlanEntryDto(
    val id: Long, // ID único de la entrada en el plan
    val recipe: RecipePreviewResponse // Información resumida de la receta asignada
)
