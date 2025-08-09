// ========================================================================
// Archivo: RecipeViewModel.kt
// Propósito: Gestiona el estado y la lógica relacionada con las recetas.
//            Permite obtener la lista de recetas y los detalles de una receta
//            específica, manejando el estado de carga, éxito y error para la UI.
// ========================================================================

package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.repository.RecipeRepository
import com.noskill.anymeal.data.di.NetworkModule
import com.noskill.anymeal.ui.models.RecipeDetailUi
import com.noskill.anymeal.ui.models.RecipePreviewUi
import com.noskill.anymeal.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// RecipeViewModel extiende AndroidViewModel y gestiona el estado de las recetas.
class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio de recetas, inicializada con el ApiService proporcionado por NetworkModule.
    private val recipeRepository = RecipeRepository(NetworkModule.provideApiService(application))

    // StateFlow privado para almacenar el estado de la lista de recetas (cargando, éxito, error).
    private val _recipeListState = MutableStateFlow<Result<List<RecipePreviewUi>>>(Result.Loading)
    // StateFlow público para observar el estado de la lista de recetas desde la UI.
    val recipeListState: StateFlow<Result<List<RecipePreviewUi>>> = _recipeListState

    // StateFlow privado para almacenar el estado del detalle de receta (cargando, éxito, error).
    private val _recipeDetailState = MutableStateFlow<Result<RecipeDetailUi?>>(Result.Loading)
    // StateFlow público para observar el estado del detalle de receta desde la UI.
    val recipeDetailState: StateFlow<Result<RecipeDetailUi?>> = _recipeDetailState

    // Al inicializar el ViewModel, se obtiene la lista de recetas automáticamente.
    init {
        fetchRecipes(null)
    }

    // Método para obtener la lista de recetas desde el repositorio.
    // Actualiza el estado según el resultado de la llamada a la API.
    fun fetchRecipes(query: String?) {
        viewModelScope.launch {
            _recipeListState.value = Result.Loading
            try {
                val response = recipeRepository.getAllRecipes(query)
                if (response.isSuccessful && response.body() != null) {
                    val uiRecipes = response.body()!!.map { dto ->
                        RecipePreviewUi(
                            id = dto.id.toInt(),
                            title = dto.title,
                            time = dto.readyInMinutes,
                            difficulty = dto.difficulty,
                            imageUrl = dto.imageUrl,
                            category = dto.category
                        )
                    }
                    _recipeListState.value = Result.Success(uiRecipes)
                } else {
                    _recipeListState.value = Result.Error("Error al cargar las recetas.")
                }
            } catch (e: Exception) {
                _recipeListState.value = Result.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // Método para obtener los detalles de una receta específica por su ID.
    // Actualiza el estado del detalle de la receta según el resultado de la llamada a la API.
    fun fetchRecipeById(id: Int) {
        viewModelScope.launch {
            _recipeDetailState.value = Result.Loading
            try {
                val response = recipeRepository.getRecipeById(id.toLong())

                // --- CORRECCIÓN: Manejo de errores más detallado ---
                if (response.isSuccessful) {
                    val dto = response.body()
                    if (dto != null) {
                        val uiRecipeDetail = RecipeDetailUi(
                            id = dto.id.toInt(),
                            title = dto.title,
                            time = dto.readyInMinutes,
                            difficulty = dto.difficulty,
                            imageUrl = dto.imageUrl,
                            description = dto.description,
                            ingredients = dto.ingredients,
                            steps = dto.steps,
                            category = dto.category
                        )
                        _recipeDetailState.value = Result.Success(uiRecipeDetail)
                    } else {
                        // Si la llamada fue exitosa (código 200) pero el cuerpo es nulo,
                        // suele ser un error al convertir el JSON al DTO.
                        _recipeDetailState.value = Result.Error("Error de parseo: Revisa que el DTO coincida con la respuesta del backend.")
                    }
                } else {
                    // Si la llamada no fue exitosa, mostramos el código de error.
                    _recipeDetailState.value = Result.Error("Receta no encontrada (Error: ${response.code()})")
                }
            } catch (e: Exception) {
                _recipeDetailState.value = Result.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
