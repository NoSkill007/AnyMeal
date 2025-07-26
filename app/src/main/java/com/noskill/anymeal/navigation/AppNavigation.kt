package com.noskill.anymeal.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Plan : Screen("plan")
    object ShoppingList : Screen("shopping_list")
    object Profile : Screen("profile")
    object Favorites : Screen("favorites")
    object PrivacyPolicy : Screen("privacy_policy")
    object Faq : Screen("faq")
    object ContactUs : Screen("ContactUs")
    object AppVersion : Screen("app_version")
    object Achievements : Screen("achievements")

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