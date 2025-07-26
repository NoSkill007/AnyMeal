// ========================================================================
// Archivo MODIFICADO: viewmodel/FavoritesViewModel.kt
// Propósito: Se elimina la dependencia del archivo Mappers.kt y se integra
// la lógica de conversión directamente aquí.
// ========================================================================
package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.repository.FavoritesRepository
import com.noskill.anymeal.di.NetworkModule
import com.noskill.anymeal.dto.RecipePreviewResponse
import com.noskill.anymeal.ui.models.RecipePreviewUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favoriteRecipes: List<RecipePreviewUi> = emptyList(),
    val errorMessage: String? = null
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = NetworkModule.provideApiService(application)
    private val repository = FavoritesRepository(apiService)

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    val favoriteRecipeIds: StateFlow<Set<Int>> = uiState.map { it.favoriteRecipes.map { recipe -> recipe.id }.toSet() }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, emptySet())

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = repository.getFavorites()
                if (response.isSuccessful && response.body() != null) {
                    // La conversión se hace aquí directamente
                    val favoriteList = response.body()!!.map { it.toUiModel() }
                    _uiState.update { it.copy(isLoading = false, favoriteRecipes = favoriteList) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar favoritos") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val isCurrentlyFavorite = favoriteRecipeIds.value.contains(recipeId)
            try {
                val response = if (isCurrentlyFavorite) {
                    repository.removeFavorite(recipeId.toLong())
                } else {
                    repository.addFavorite(recipeId.toLong())
                }

                if (response.isSuccessful) {
                    loadFavorites()
                } else {
                    _uiState.update { it.copy(errorMessage = "No se pudo actualizar el favorito") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error de conexión al actualizar favorito: ${e.message}") }
            }
        }
    }

    // --- CORRECCIÓN: La función de conversión ahora vive dentro del ViewModel ---
    private fun RecipePreviewResponse.toUiModel(): RecipePreviewUi {
        return RecipePreviewUi(
            id = this.id.toInt(),
            title = this.title,
            imageUrl = this.imageUrl,
            time = this.readyInMinutes,
            difficulty = this.difficulty,
            category = this.category
        )
    }
}
