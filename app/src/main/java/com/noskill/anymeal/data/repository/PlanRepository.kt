// ========================================================================
// Archivo: PlanRepository.kt
// Propósito: Gestiona las operaciones de red relacionadas con los planes semanales,
//            incluyendo la obtención del plan, la adición y eliminación de recetas,
//            y la actualización de notas.
// ========================================================================

package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.NotesRequest
import com.noskill.anymeal.dto.PlanRequest

// PlanRepository se encarga de interactuar con el ApiService para gestionar
// el plan semanal del usuario y sus recetas asociadas.
class PlanRepository(private val apiService: ApiService) {
    // Obtiene el plan semanal a partir de una fecha específica (formato String).
    suspend fun getWeeklyPlan(startDate: String) = apiService.getWeeklyPlan(startDate)

    // Agrega una receta al plan semanal.
    suspend fun addRecipeToPlan(request: PlanRequest) = apiService.addRecipeToPlan(request)

    // Elimina una entrada del plan semanal por su ID.
    suspend fun deletePlanEntry(entryId: Long) = apiService.deletePlanEntry(entryId)

    // Actualiza las notas de un plan específico por su ID.
    suspend fun updateNotes(planId: Long, notes: String) = apiService.updateNotes(planId, NotesRequest(notes))
}
