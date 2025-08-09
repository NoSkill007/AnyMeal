/* --------------------------------------------------------------------
 * Archivo: AppNavGraph.kt
 * Propósito: Define la estructura de navegación principal de la aplicación,
 *            gestionando las rutas y pantallas disponibles mediante Jetpack Compose.
 *            Permite la transición entre pantallas, el manejo de argumentos y la
 *            integración de ViewModels compartidos para funcionalidades globales.
 * --------------------------------------------------------------------*/

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

// La función AppNavGraph configura el grafo de navegación principal de la app.
// Recibe el controlador de navegación, el estado del tema, el callback para cambiar tema y el callback de logout.
@Composable
fun AppNavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogoutConfirmed: () -> Unit
) {
    // ViewModels compartidos entre pantallas principales.
    val plannerViewModel: PlannerViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current

    // Configuración del grafo de navegación con las rutas y pantallas.
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Pantalla de inicio (Splash)
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        // Pantalla de autenticación (selección de login o registro)
        composable(Screen.Auth.route) {
            AuthScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        // Pantalla de login
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        // Pantalla de registro
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        // Pantalla principal (Home)
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                favoritesViewModel = favoritesViewModel
            )
        }
        // Pantalla de planificación semanal
        composable(Screen.Plan.route) {
            PlanScreen(
                navController = navController,
                plannerViewModel = plannerViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
        // Pantalla de búsqueda de recetas, recibe argumentos opcionales
        composable(
            route = Screen.RecipeSearch.route,
            arguments = listOf(
                navArgument("mealTime") { type = NavType.StringType; nullable = true },
                navArgument("planDate") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val mealTime = backStackEntry.arguments?.getString("mealTime") ?: ""
            val planDateString = backStackEntry.arguments?.getString("planDate")
            RecipeSearchScreen(
                navController = navController,
                category = mealTime,
                planDateString = planDateString,
                favoritesViewModel = favoritesViewModel,
                plannerViewModel = plannerViewModel
            )
        }
        // Pantalla de lista de compras
        composable(Screen.ShoppingList.route) {
            ShoppingListScreen(
                navController = navController
            )
        }
        // Pantalla de perfil de usuario
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
        // Pantalla de favoritos
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController, favoritesViewModel)
        }
        // Pantalla de logros
        composable(Screen.Achievements.route) {
            AchievementsScreen(
                navController = navController,
                plannerViewModel = plannerViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
        // Pantalla de edición de perfil
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
        // Pantalla de detalle de receta, recibe argumentos obligatorios y opcionales
        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType },
                navArgument("source") { type = NavType.StringType; nullable = true },
                navArgument("mealTime") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId")
            val source = backStackEntry.arguments?.getString("source")
            val mealTime = backStackEntry.arguments?.getString("mealTime")
            RecipeDetailScreen(
                navController = navController,
                recipeId = if (recipeId == 0) null else recipeId,
                source = source,
                mealTime = mealTime,
                plannerViewModel = plannerViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
        // Pantalla de política de privacidad
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(navController = navController)
        }
        // Pantalla de preguntas frecuentes
        composable(Screen.Faq.route) {
            FaqScreen(navController = navController)
        }
        // Pantalla de contacto
        composable(Screen.ContactUs.route) {
            ContactUsScreen(navController = navController)
        }
        // Pantalla de versión de la app
        composable(Screen.AppVersion.route) {
            AppVersionScreen(navController = navController)
        }
    }
}