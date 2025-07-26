package com.noskill.anymeal.dto

import java.time.LocalDate // Mantén esta importación si la usas en otros DTOs o lógica

data class PlanRequest(
    val recipeId: Long,
    val date: String, // Debe ser String
    val mealType: String
)
data class DailyPlanDto(
    val id: Long,
    val planDate: String, // Debe ser String
    val notes: String?,
    val meals: Map<String, List<PlanEntryDto>>
)

// DTO para la petición de actualizar las notas.
data class NotesRequest(
    val notes: String
)

// DTOs para la respuesta del plan semanal.
data class PlanResponse(
    val dailyPlans: Map<String, DailyPlanDto>
)

data class PlanEntryDto(
    val id: Long,
    val recipe: RecipePreviewResponse
)
