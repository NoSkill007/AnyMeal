/* --------------------------------------------------------------------
 * Archivo: AppNavigation.kt
 * Propósito: Define las rutas de navegación de la aplicación mediante la clase sellada Screen.
 *            Cada objeto representa una pantalla y su ruta asociada para el sistema de navegación.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Clase sellada que representa cada pantalla de la app y su ruta de navegación.
sealed class Screen(val route: String) {
    object Splash : Screen("splash") // Pantalla de inicio
    object Auth : Screen("auth") // Pantalla de autenticación
    object Login : Screen("login") // Pantalla de inicio de sesión
    object Register : Screen("register") // Pantalla de registro
    object Home : Screen("home") // Pantalla principal
    object Plan : Screen("plan") // Pantalla de planificación semanal
    object ShoppingList : Screen("shopping_list") // Pantalla de lista de compras
    object Profile : Screen("profile") // Pantalla de perfil de usuario
    object Favorites : Screen("favorites") // Pantalla de favoritos
    object PrivacyPolicy : Screen("privacy_policy") // Pantalla de política de privacidad
    object Faq : Screen("faq") // Pantalla de preguntas frecuentes
    object ContactUs : Screen("ContactUs") // Pantalla de contacto
    object AppVersion : Screen("app_version") // Pantalla de versión de la app
    object Achievements : Screen("achievements") // Pantalla de logros

    object RecipeDetail : Screen("recipe_detail/{recipeId}?source={source}&mealTime={mealTime}") {
        fun createRoute(recipeId: Int, source: String? = null, mealTime: String? = null): String { // recipeId ya no es nullable aquí
            val baseRoute = "recipe_detail/$recipeId" // Siempre pasa un Int
            val sourceParam = source?.let { "?source=$it" } ?: ""
            val encodedMealTime = mealTime?.let { URLEncoder.encode(it, StandardCharsets.UTF_8.toString()) } ?: ""
            val mealTimeParam = if (mealTime != null) "${if (sourceParam.isEmpty()) "?" else "&"}mealTime=$encodedMealTime" else ""
            return "$baseRoute$sourceParam$mealTimeParam"
        }
    }

    // MODIFICADO: Añadido {planDate} como argumento opcional para RecipeSearch
    object RecipeSearch : Screen("recipe_search/{mealTime}?planDate={planDate}") {
        fun createRoute(mealTime: String, planDate: String? = null): String {
            val encodedMealTime = URLEncoder.encode(mealTime, StandardCharsets.UTF_8.toString())
            val encodedPlanDate = planDate?.let { URLEncoder.encode(it, StandardCharsets.UTF_8.toString()) } ?: ""

            var route = "recipe_search/$encodedMealTime"
            if (planDate != null) {
                route += "?planDate=$encodedPlanDate"
            }
            return route
        }
    }

    object EditProfile : Screen("edit_profile")
}
