// ========================================================================
// Archivo MODIFICADO: viewmodel/AchievementsViewModel.kt
// Propósito: Corregido para manejar el estado 'Result' del PlannerViewModel.
// ========================================================================
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

class AchievementsViewModel(
    plannerViewModel: PlannerViewModel,
    favoritesViewModel: FavoritesViewModel
) : ViewModel() {

    val achievements: StateFlow<List<Achievement>> = combine(
        plannerViewModel.planState,
        favoritesViewModel.favoriteRecipeIds
    ) { planResult, favoriteIds -> // El nombre de la variable se cambia a planResult para mayor claridad

        // --- CORRECCIÓN ---
        // Se extraen los datos del plan solo si la carga fue exitosa.
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
            else -> Pair(0, 0) // Valores por defecto
        }

        val totalFavorites = favoriteIds.size

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

// Factory para poder pasarle parámetros al AchievementsViewModel
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
