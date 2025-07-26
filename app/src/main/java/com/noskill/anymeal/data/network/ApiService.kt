// --- PASO 2: Interfaz ApiService Actualizada ---
// Archivo: data/network/ApiService.kt
package com.noskill.anymeal.data.network

import com.noskill.anymeal.data.model.User
import com.noskill.anymeal.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/v1/user/profile")
    suspend fun getUserProfile(): Response<User>

    @GET("api/v1/recipes")
    suspend fun getAllRecipes(@Query("query") query: String?): Response<List<RecipePreviewResponse>>

    @GET("api/v1/recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: Long): Response<RecipeDetailResponse>

    @GET("api/v1/plans")
    suspend fun getWeeklyPlan(@Query("startDate") startDate: String): Response<PlanResponse>

    @POST("api/v1/plans")
    suspend fun addRecipeToPlan(@Body request: PlanRequest): Response<Void>

    @DELETE("api/v1/plans/entries/{entryId}")
    suspend fun deletePlanEntry(@Path("entryId") entryId: Long): Response<Void>

    @PUT("api/v1/plans/{planId}/notes")
    suspend fun updateNotes(@Path("planId") planId: Long, @Body request: NotesRequest): Response<Void>

    @POST("api/v1/shopping-list/generate")
    suspend fun generateAndGetShoppingList(@Body request: GenerateListRequest): Response<ShoppingListResponse>

    @POST("api/v1/shopping-list")
    suspend fun addItem(@Body request: AddItemRequest): Response<ShoppingItemDto>

    @PUT("api/v1/shopping-list/{itemId}")
    suspend fun updateItem(@Path("itemId") itemId: Long, @Body request: UpdateItemRequest): Response<ShoppingItemDto>

    @POST("api/v1/shopping-list/clear-checked")
    suspend fun clearCheckedItems(): Response<Unit>

    @PUT("api/v1/user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<MessageResponse>

    @PUT("api/v1/user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<MessageResponse>

    // --- Endpoints para Favoritos ---
    @GET("api/v1/favorites")
    suspend fun getFavorites(): Response<List<RecipePreviewResponse>>

    @POST("api/v1/favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): Response<Void>

    @DELETE("api/v1/favorites/{recipeId}")
    suspend fun removeFavorite(@Path("recipeId") recipeId: Long): Response<Void>


}