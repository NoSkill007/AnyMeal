/**
 * AchievementsViewModel.kt
 *
 * Propósito: Gestiona la lógica de los logros de usuario dentro de la aplicación, realizando un seguimiento
 * de diversas actividades como la planificación de comidas y el marcado de recetas como favoritas.
 * Este ViewModel combina datos de PlannerViewModel y FavoritesViewModel para determinar qué logros
 * ha desbloqueado el usuario.
 */
package com.noskill.anymeal.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.ui.models.Achievement
import com.noskill.anymeal.util.Result
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel que gestiona la lógica de logros dentro de la aplicación.
 *
 * @param plannerViewModel ViewModel que contiene información sobre los planes de comidas del usuario.
 * @param favoritesViewModel ViewModel que contiene las recetas favoritas del usuario.
 */
class AchievementsViewModel(
    plannerViewModel: PlannerViewModel,
    favoritesViewModel: FavoritesViewModel
) : ViewModel() {

    /**
     * StateFlow que emite la lista de logros y su estado (desbloqueado o no).
     * Combina datos de los planes de comidas y recetas favoritas para determinar
     * el estado de cada logro.
     */
    val achievements: StateFlow<List<Achievement>> = combine(
        plannerViewModel.planState,
        favoritesViewModel.favoriteRecipeIds
    ) { planResult, favoriteIds ->

        // Extrae datos del plan solo si la carga fue exitosa.
        // Si está cargando o hay un error, se usan valores por defecto (0).
        val (totalPlannedDays, totalRecipesAdded) = when (planResult) {
            is Result.Success -> {
                val planData = planResult.data
                val days = planData.size
                val recipes = planData.values.sumOf { dailyPlan ->
                    dailyPlan.meals.values.sumOf { it.size }
                }
                Pair(days, recipes)
            }
            else -> Pair(0, 0) // Valores por defecto para estados de carga o error
        }

        val totalFavorites = favoriteIds.size

        // Lista de logros disponibles en la aplicación con su estado de desbloqueo
        listOf(
            Achievement(
                title = "Primer Plan",
                description = "Crea tu primer plan de comidas para un día.",
                icon = Icons.Outlined.Flag,
                isUnlocked = totalPlannedDays >= 1
            ),
            Achievement(
                title = "Planificador Semanal",
                description = "Planifica comidas para 7 días diferentes.",
                icon = Icons.Outlined.DateRange,
                isUnlocked = totalPlannedDays >= 7
            ),
            Achievement(
                title = "Chef Organizado",
                description = "Añade 10 recetas a tus planes.",
                icon = Icons.Outlined.Restaurant,
                isUnlocked = totalRecipesAdded >= 10
            ),
            Achievement(
                title = "Coleccionista de Sabores",
                description = "Guarda 5 recetas en tus favoritos.",
                icon = Icons.Outlined.FavoriteBorder,
                isUnlocked = totalFavorites >= 5
            ),
            Achievement(
                title = "Amante de la Cocina",
                description = "Guarda 15 recetas en tus favoritos.",
                icon = Icons.Outlined.Favorite,
                isUnlocked = totalFavorites >= 15
            ),
            Achievement(
                title = "Maestro Culinario",
                description = "Añade 50 recetas a tus planes.",
                icon = Icons.Outlined.EmojiEvents,
                isUnlocked = totalRecipesAdded >= 50
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

/**
 * Factory para la creación de instancias de AchievementsViewModel.
 * Permite inyectar las dependencias necesarias (otros ViewModels) al crear el ViewModel.
 *
 * @param plannerViewModel ViewModel con la información de planes de comidas.
 * @param favoritesViewModel ViewModel con la información de recetas favoritas.
 */
class AchievementsViewModelFactory(
    private val plannerViewModel: PlannerViewModel,
    private val favoritesViewModel: FavoritesViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AchievementsViewModel(plannerViewModel, favoritesViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}