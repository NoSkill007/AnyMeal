// ========================================================================
// Archivo: ApiService.kt
// Propósito: Define la interfaz de API de Retrofit para todos los endpoints
//            remotos utilizados en la aplicación, incluyendo autenticación,
//            perfil de usuario, recetas, planes, lista de compras y favoritos.
// ========================================================================
package com.noskill.anymeal.data.network

import com.noskill.anymeal.data.model.User
import com.noskill.anymeal.dto.*
import retrofit2.Response
import retrofit2.http.*

// ApiService declara todos los endpoints HTTP para la API del backend.
// Cada método corresponde a un endpoint REST específico y verbo HTTP.
interface ApiService {
    // Registra un nuevo usuario con los datos proporcionados.
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // Autentica a un usuario con sus credenciales de acceso.
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // Obtiene el perfil del usuario autenticado actualmente.
    @GET("api/v1/user/profile")
    suspend fun getUserProfile(): Response<User>

    // Obtiene una lista de recetas, opcionalmente filtradas por una consulta.
    @GET("api/v1/recipes")
    suspend fun getAllRecipes(@Query("query") query: String?): Response<List<RecipePreviewResponse>>

    // Recupera información detallada de una receta específica por su ID.
    @GET("api/v1/recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: Long): Response<RecipeDetailResponse>

    // Obtiene el plan de comidas semanal a partir de una fecha específica.
    @GET("api/v1/plans")
    suspend fun getWeeklyPlan(@Query("startDate") startDate: String): Response<PlanResponse>

    // Agrega una receta al plan semanal.
    @POST("api/v1/plans")
    suspend fun addRecipeToPlan(@Body request: PlanRequest): Response<Void>

    // Elimina una entrada específica del plan semanal por ID de entrada.
    @DELETE("api/v1/plans/entries/{entryId}")
    suspend fun deletePlanEntry(@Path("entryId") entryId: Long): Response<Void>

    // Actualiza las notas de un plan específico por ID de plan.
    @PUT("api/v1/plans/{planId}/notes")
    suspend fun updateNotes(@Path("planId") planId: Long, @Body request: NotesRequest): Response<Void>

    // Genera y recupera la lista de compras basada en el plan actual.
    @POST("api/v1/shopping-list/generate")
    suspend fun generateAndGetShoppingList(@Body request: GenerateListRequest): Response<ShoppingListResponse>

    // Recupera la lista de compras actual.
    @GET("api/v1/shopping-list")
    suspend fun getCurrentShoppingList(): Response<ShoppingListResponse>

    // Agrega un nuevo elemento a la lista de compras.
    @POST("api/v1/shopping-list")
    suspend fun addItem(@Body request: AddItemRequest): Response<ShoppingItemDto>

    // Actualiza un elemento existente de la lista de compras por ID de elemento.
    @PUT("api/v1/shopping-list/{itemId}")
    suspend fun updateItem(@Path("itemId") itemId: Long, @Body request: UpdateItemRequest): Response<ShoppingItemDto>

    // Edita un elemento existente de la lista de compras por ID de elemento.
    @PUT("api/v1/shopping-list/{itemId}/edit")
    suspend fun editItem(@Path("itemId") itemId: Long, @Body request: EditItemRequest): Response<ShoppingItemDto>

    // Elimina un elemento de la lista de compras por ID de elemento.
    @DELETE("api/v1/shopping-list/{itemId}")
    suspend fun deleteItem(@Path("itemId") itemId: Long): Response<Unit>

    // Limpia todos los elementos marcados como comprados en la lista de compras.
    @POST("api/v1/shopping-list/clear-checked")
    suspend fun clearCheckedItems(): Response<Unit>

    // Actualiza el perfil del usuario actual.
    @PUT("api/v1/user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<MessageResponse>

    // Cambia la contraseña del usuario actual.
    @PUT("api/v1/user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<MessageResponse>

    // Recupera la lista de recetas favoritas del usuario.
    @GET("api/v1/favorites")
    suspend fun getFavorites(): Response<List<RecipePreviewResponse>>

    // Agrega una receta a las favoritas del usuario.
    @POST("api/v1/favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): Response<Void>

    // Elimina una receta de las favoritas del usuario por ID de receta.
    @DELETE("api/v1/favorites/{recipeId}")
    suspend fun removeFavorite(@Path("recipeId") recipeId: Long): Response<Void>

}