package com.noskill.anymeal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.noskill.anymeal.data.local.SessionManager
import com.noskill.anymeal.ui.screens.*
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel
import com.noskill.anymeal.viewmodel.ProfileViewModel
import com.noskill.anymeal.viewmodel.ShoppingListViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogoutConfirmed: () -> Unit
) {
    // ViewModels que se comparten entre varias pantallas
    val plannerViewModel: PlannerViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                favoritesViewModel = favoritesViewModel
            )
        }
        composable(Screen.Plan.route) {
            PlanScreen(
                navController = navController,
                plannerViewModel = plannerViewModel,
                favoritesViewModel = favoritesViewModel
            )

        }
        composable(
            // MODIFICADO: Añadido planDate a la ruta y argumentos
            route = Screen.RecipeSearch.route, // "recipe_search/{mealTime}?planDate={planDate}"
            arguments = listOf(
                navArgument("mealTime") { type = NavType.StringType; nullable = true },
                navArgument("planDate") { type = NavType.StringType; nullable = true } // Nuevo argumento
            )
        ) { backStackEntry ->
            val mealTime = backStackEntry.arguments?.getString("mealTime") ?: ""
            val planDateString = backStackEntry.arguments?.getString("planDate") // Leer la fecha como String

            RecipeSearchScreen(
                navController = navController,
                category = mealTime,
                planDateString = planDateString, // Pasar la fecha
                favoritesViewModel = favoritesViewModel,
                plannerViewModel = plannerViewModel // PASAR EL PLANNERVIEWMODEL AQUÍ
            )
        }
        composable(Screen.ShoppingList.route) {
            ShoppingListScreen(
                navController = navController
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onLogoutConfirmed = {
                    val sessionManager = SessionManager(context)
                    sessionManager.clearAuthToken()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                },
                plannerViewModel = plannerViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController, favoritesViewModel)
        }
        composable(Screen.Achievements.route) {
            AchievementsScreen(
                navController = navController,
                plannerViewModel = plannerViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType }, // <-- Quitar 'nullable = true'
                navArgument("source") { type = NavType.StringType; nullable = true },
                navArgument("mealTime") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") // Esto seguirá siendo Int o 0 si no se pasa
            val source = backStackEntry.arguments?.getString("source")
            val mealTime = backStackEntry.arguments?.getString("mealTime")

            RecipeDetailScreen(
                navController = navController,
                recipeId = if (recipeId == 0) null else recipeId, // Convertir 0 a null si es tu valor por defecto para "no ID"
                source = source,
                mealTime = mealTime,
                plannerViewModel = plannerViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(navController = navController)
        }
        composable(Screen.Faq.route) {
            FaqScreen(navController = navController)
        }
        composable(Screen.ContactUs.route) {
            ContactUsScreen(navController = navController)
        }
        composable(Screen.AppVersion.route) {
            AppVersionScreen(navController = navController)
        }
    }
}