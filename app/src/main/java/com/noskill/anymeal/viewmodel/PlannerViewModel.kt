package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.repository.PlanRepository
import com.noskill.anymeal.di.NetworkModule
import com.noskill.anymeal.dto.DailyPlanDto
import com.noskill.anymeal.dto.PlanRequest
import com.noskill.anymeal.ui.models.DailyPlan
import com.noskill.anymeal.ui.models.PlanEntry
import com.noskill.anymeal.ui.models.RecipePreviewUi
import com.noskill.anymeal.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val planRepository = PlanRepository(NetworkModule.provideApiService(application))

    private val _planState = MutableStateFlow<Result<Map<String, DailyPlan>>>(Result.Loading)
    val planState: StateFlow<Result<Map<String, DailyPlan>>> = _planState.asStateFlow()

    // AÑADIDO: MutableStateFlow para currentStartDate
    private val _currentStartDate = MutableStateFlow(LocalDate.now())
    val currentStartDate: StateFlow<LocalDate> = _currentStartDate.asStateFlow()

    private var dailyPlanIdMap = mutableMapOf<String, Long>()

    fun fetchWeeklyPlan(startDate: LocalDate) {
        viewModelScope.launch {
            _planState.value = Result.Loading
            // AÑADIDO: Actualizar _currentStartDate cada vez que se llama a fetchWeeklyPlan
            _currentStartDate.value = startDate
            try {
                val formattedStartDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val response = planRepository.getWeeklyPlan(formattedStartDate)

                if (response.isSuccessful && response.body() != null) {
                    dailyPlanIdMap.clear()
                    val uiPlanMap = response.body()!!.dailyPlans.mapValues { entry ->
                        dailyPlanIdMap[entry.key] = entry.value.id
                        mapDtoToUiModel(entry.value)
                    }

                    // 🔥 FIX: Forzamos una copia profunda e inmutable del mapa
                    val copiedMap = uiPlanMap.mapValues { planEntry ->
                        val original = planEntry.value
                        original.copy(
                            meals = original.meals.mapValues { mealEntry ->
                                mealEntry.value.toList() // fuerza nueva lista
                            }
                        )
                    }

                    _planState.value = Result.Success(copiedMap.toMap()) // NUEVO map
                } else {
                    _planState.value = Result.Error("Error al cargar el plan: ${response.code()}")
                }
            } catch (e: Exception) {
                _planState.value = Result.Error("Error de conexión al cargar el plan: ${e.message}")
            }
        }
    }

    fun addRecipeToPlan(recipeId: Int, mealTime: String, date: Date, currentStartDate: LocalDate) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply { time = date }
            val localDate = LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            val formattedDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val request = PlanRequest(recipeId.toLong(), formattedDate, mealTime)

            try {
                val response = planRepository.addRecipeToPlan(request)
                if (response.isSuccessful) {
                    // Usamos el _currentStartDate del ViewModel para el refresh
                    // Esto asegura que el plan se refresque con la semana correcta
                    fetchWeeklyPlan(_currentStartDate.value) // Usar el valor actual del StateFlow
                } else {
                    println("Error al añadir receta al plan: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Excepción al añadir receta al plan: ${e.message}")
            }
        }
    }

    fun deletePlanEntry(entryId: Long, currentStartDate: LocalDate) {
        viewModelScope.launch {
            try {
                val response = planRepository.deletePlanEntry(entryId)
                if (response.isSuccessful) {
                    // Usamos el _currentStartDate del ViewModel para el refresh
                    fetchWeeklyPlan(_currentStartDate.value) // Usar el valor actual del StateFlow
                }
            } catch (e: Exception) {
                // Opcional: manejar errores
            }
        }
    }

    fun updateNotes(newNotes: String, dayKey: String, currentStartDate: LocalDate) {
        viewModelScope.launch {
            val planId = dailyPlanIdMap[dayKey]
            if (planId != null) {
                try {
                    val response = planRepository.updateNotes(planId, newNotes)
                    if (response.isSuccessful) {
                        // Usamos el _currentStartDate del ViewModel para el refresh
                        fetchWeeklyPlan(_currentStartDate.value) // Usar el valor actual del StateFlow
                    }
                } catch (e: Exception) {
                    // Opcional: manejar errores
                }
            }
        }
    }

    private fun mapDtoToUiModel(dto: DailyPlanDto): DailyPlan {
        val planDateAsLocalDate = LocalDate.parse(dto.planDate)

        val meals = dto.meals.mapValues { mealEntry ->
            mealEntry.value.map { planEntryDto ->
                PlanEntry(
                    id = planEntryDto.id,
                    backendId = planEntryDto.id,
                    recipe = RecipePreviewUi(
                        id = planEntryDto.recipe.id.toInt(),
                        title = planEntryDto.recipe.title,
                        time = planEntryDto.recipe.readyInMinutes,
                        difficulty = planEntryDto.recipe.difficulty,
                        imageUrl = planEntryDto.recipe.imageUrl,
                        category = planEntryDto.recipe.category
                    )
                )
            }.toList()
        }

        return DailyPlan(notes = dto.notes ?: "", meals = meals, planDate = planDateAsLocalDate)
    }
}