package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.NotesRequest
import com.noskill.anymeal.dto.PlanRequest
// import java.time.LocalDate // Ya no es necesario importar LocalDate aquí si no se usa directamente

class PlanRepository(private val apiService: ApiService) {
    // ¡CAMBIO AQUÍ! El parámetro startDate ahora es de tipo String
    // Ya no necesitas .toString() aquí porque el ViewModel ya lo formatea a String
    suspend fun getWeeklyPlan(startDate: String) = apiService.getWeeklyPlan(startDate)

    suspend fun addRecipeToPlan(request: PlanRequest) = apiService.addRecipeToPlan(request)
    suspend fun deletePlanEntry(entryId: Long) = apiService.deletePlanEntry(entryId)
    suspend fun updateNotes(planId: Long, notes: String) = apiService.updateNotes(planId, NotesRequest(notes))
}
