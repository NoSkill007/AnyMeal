/**
 * FavoritesViewModel.kt
 *
 * Propósito: Gestiona las recetas favoritas del usuario, proporcionando funcionalidades para
 * cargar, añadir y eliminar recetas de la lista de favoritos. Maneja el estado de la UI
 * relacionado con las recetas favoritas y expone flujos reactivos de datos para la interfaz.
 */
package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.repository.FavoritesRepository
import com.noskill.anymeal.data.di.NetworkModule
import com.noskill.anymeal.dto.RecipePreviewResponse
import com.noskill.anymeal.ui.models.RecipePreviewUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Clase de datos que representa el estado de la UI para la pantalla de favoritos.
 *
 * @property isLoading Indica si se está cargando información desde el repositorio
 * @property favoriteRecipes Lista de recetas marcadas como favoritas por el usuario
 * @property errorMessage Mensaje de error si ocurre algún problema, null si no hay errores
 */
data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favoriteRecipes: List<RecipePreviewUi> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel que gestiona la lógica de las recetas favoritas del usuario.
 * Proporciona métodos para cargar, añadir y eliminar recetas de favoritos.
 *
 * @param application Instancia de la aplicación para acceder al contexto
 */
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Servicio de API para comunicación con el backend
     */
    private val apiService = NetworkModule.provideApiService(application)

    /**
     * Repositorio que encapsula las operaciones relacionadas con favoritos
     */
    private val repository = FavoritesRepository(apiService)

    /**
     * Estado interno mutable de la UI para favoritos
     */
    private val _uiState = MutableStateFlow(FavoritesUiState())

    /**
     * Estado público observable de la UI para la pantalla de favoritos
     */
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    /**
     * Conjunto de IDs de recetas favoritas derivado del estado de la UI.
     * Útil para comprobar rápidamente si una receta está marcada como favorita.
     */
    val favoriteRecipeIds: StateFlow<Set<Int>> = uiState.map { it.favoriteRecipes.map { recipe -> recipe.id }.toSet() }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, emptySet())

    /**
     * Inicialización del ViewModel: carga la lista de favoritos al crear la instancia
     */
    init {
        loadFavorites()
    }

    /**
     * Carga la lista de recetas favoritas desde el repositorio.
     * Actualiza el estado de la UI con los resultados o errores.
     */
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

    /**
     * Alterna el estado de favorito de una receta. Si la receta ya está en favoritos,
     * la elimina; si no está, la añade a la lista de favoritos.
     *
     * @param recipeId Identificador de la receta a alternar su estado de favorito
     */
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

    /**
     * Función de extensión que convierte un objeto RecipePreviewResponse (DTO)
     * a un objeto RecipePreviewUi para mostrar en la interfaz de usuario.
     *
     * @return Objeto RecipePreviewUi con los datos transformados para la UI
     */
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
