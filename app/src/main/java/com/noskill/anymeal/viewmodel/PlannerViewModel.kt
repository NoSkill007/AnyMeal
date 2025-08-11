/*
 * Archivo: PlannerViewModel.kt
 * Propósito: Define el ViewModel para la gestión del planificador semanal de comidas.
 * Proporciona lógica para obtener, modificar y actualizar el plan semanal, así como para manejar el estado de la UI.
 */
package com.noskill.anymeal.viewmodel

import android.app.Application // Importa la clase Application para contexto global
import androidx.lifecycle.AndroidViewModel // ViewModel con acceso a Application
import androidx.lifecycle.viewModelScope // Alcance de corrutinas para ViewModel
import com.noskill.anymeal.data.repository.PlanRepository // Repositorio para operaciones de plan
import com.noskill.anymeal.data.di.NetworkModule // Proveedor de servicios de red
import com.noskill.anymeal.dto.DailyPlanDto // DTO para el plan diario recibido de la API
import com.noskill.anymeal.dto.PlanRequest // DTO para solicitudes de modificación de plan
import com.noskill.anymeal.ui.models.DailyPlan // Modelo UI para el plan diario
import com.noskill.anymeal.ui.models.PlanEntry // Modelo UI para una entrada de plan
import com.noskill.anymeal.ui.models.RecipePreviewUi // Modelo UI para previsualización de receta
import com.noskill.anymeal.util.Result // Wrapper para estados de resultado (Loading, Success, Error)
import com.noskill.anymeal.util.PlanChangeNotifier // Notificador de cambios en el plan
import kotlinx.coroutines.flow.MutableStateFlow // Flow mutable para estado observable
import kotlinx.coroutines.flow.StateFlow // Flow inmutable para exposición de estado
import kotlinx.coroutines.flow.asStateFlow // Conversión a StateFlow
import kotlinx.coroutines.launch // Lanzador de corrutinas
import java.time.LocalDate // Fecha sin zona horaria
import java.time.format.DateTimeFormatter // Formateador de fechas
import java.util.* // Utilidades de fecha

/**
 * ViewModel principal para la gestión del planificador semanal.
 * Maneja la obtención, actualización y modificación del plan semanal de comidas.
 */
class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorio para operaciones de plan, inicializado con el servicio de red
    private val planRepository = PlanRepository(NetworkModule.provideApiService(application))

    // Estado observable del plan semanal (Loading, Success con datos, Error)
    private val _planState = MutableStateFlow<Result<Map<String, DailyPlan>>>(Result.Loading)
    val planState: StateFlow<Result<Map<String, DailyPlan>>> = _planState.asStateFlow()

    // Estado observable para la fecha de inicio actual del plan semanal
    private val _currentStartDate = MutableStateFlow(LocalDate.now())
    val currentStartDate: StateFlow<LocalDate> = _currentStartDate.asStateFlow()

    // Mapa para asociar claves de día con IDs de plan diario
    private var dailyPlanIdMap = mutableMapOf<String, Long>()

    /**
     * Obtiene el plan semanal a partir de una fecha de inicio.
     * Actualiza el estado del plan y la fecha de inicio actual.
     * Realiza copia profunda e inmutable del mapa para la UI.
     */
    fun fetchWeeklyPlan(startDate: LocalDate) {
        viewModelScope.launch {
            _planState.value = Result.Loading // Indica carga de datos
            _currentStartDate.value = startDate // Actualiza fecha de inicio
            try {
                val formattedStartDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val response = planRepository.getWeeklyPlan(formattedStartDate)

                if (response.isSuccessful && response.body() != null) {
                    dailyPlanIdMap.clear() // Limpia el mapa de IDs
                    val uiPlanMap = response.body()!!.dailyPlans.mapValues { entry ->
                        dailyPlanIdMap[entry.key] = entry.value.id // Asocia clave de día con ID
                        mapDtoToUiModel(entry.value) // Mapea DTO a modelo UI
                    }

                    // Copia profunda e inmutable del mapa para evitar mutaciones accidentales
                    val copiedMap = uiPlanMap.mapValues { planEntry ->
                        val original = planEntry.value
                        original.copy(
                            meals = original.meals.mapValues { mealEntry ->
                                mealEntry.value.toList() // Fuerza nueva lista
                            }
                        )
                    }

                    _planState.value = Result.Success(copiedMap.toMap()) // Actualiza estado con datos
                } else {
                    _planState.value = Result.Error("Error al cargar el plan: ${response.code()}")
                }
            } catch (e: Exception) {
                _planState.value = Result.Error("Error de conexión al cargar el plan: ${e.message}")
            }
        }
    }

    /**
     * Añade una receta al plan en una fecha y momento específico.
     * Refresca el plan semanal tras la operación y notifica el cambio.
     */
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
                    fetchWeeklyPlan(_currentStartDate.value) // Refresca plan con fecha actual
                    // CORREGIDO: Usar la nueva función de notificación específica
                    PlanChangeNotifier.notifyRecipeAdded(localDate)
                    println("✅ Receta agregada al plan y notificación enviada para fecha: $localDate")
                } else {
                    println("Error al añadir receta al plan: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Excepción al añadir receta al plan: ${e.message}")
            }
        }
    }

    /**
     * Elimina una entrada del plan por su ID.
     * Refresca el plan semanal tras la operación y notifica el cambio.
     * CORREGIDO: Ahora recibe la fecha específica de la receta eliminada.
     */
    fun deletePlanEntry(entryId: Long, currentStartDate: LocalDate, recipeDate: LocalDate) {
        viewModelScope.launch {
            try {
                val response = planRepository.deletePlanEntry(entryId)
                if (response.isSuccessful) {
                    fetchWeeklyPlan(_currentStartDate.value) // Refresca plan con fecha actual
                    // CORREGIDO: Usar la nueva función de notificación específica para eliminación
                    PlanChangeNotifier.notifyRecipeRemoved(recipeDate)
                    println("✅ Receta eliminada del plan y notificación enviada para fecha: $recipeDate")
                }
            } catch (e: Exception) {
                // Manejo opcional de errores
                println("Error al eliminar receta del plan: ${e.message}")
            }
        }
    }

    /**
     * Actualiza las notas de un día específico del plan.
     * Refresca el plan semanal tras la operación.
     */
    fun updateNotes(newNotes: String, dayKey: String, currentStartDate: LocalDate) {
        viewModelScope.launch {
            val planId = dailyPlanIdMap[dayKey]
            if (planId != null) {
                try {
                    val response = planRepository.updateNotes(planId, newNotes)
                    if (response.isSuccessful) {
                        fetchWeeklyPlan(_currentStartDate.value) // Refresca plan con fecha actual
                    }
                } catch (e: Exception) {
                    // Manejo opcional de errores
                }
            }
        }
    }

    /**
     * Mapea un DailyPlanDto recibido de la API a un modelo UI DailyPlan.
     * Convierte las entradas y recetas a sus modelos correspondientes.
     */
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