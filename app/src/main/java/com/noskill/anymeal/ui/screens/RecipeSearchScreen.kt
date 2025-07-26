package com.noskill.anymeal.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.noskill.anymeal.navigation.Screen // Asegúrate de que esto apunta a navigation.AppNavigation.kt
import com.noskill.anymeal.ui.components.SmallRecipeCard
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel // Importar PlannerViewModel
import com.noskill.anymeal.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
    navController: NavController,
    category: String, // Esto es tu mealTime
    planDateString: String?, // La fecha del plan como String
    favoritesViewModel: FavoritesViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel(),
    plannerViewModel: PlannerViewModel = viewModel() // Asegúrate de que se pasa desde AppNavGraph
) {
    val recipeState by recipeViewModel.recipeListState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    // currentStartDate es necesaria para el refresh del plan en el ViewModel.
    // Asumo que plannerViewModel.currentStartDate es un StateFlow<LocalDate> o similar.
    val currentStartDate by plannerViewModel.currentStartDate.collectAsState() // Asumiendo que PlannerViewModel expone esto

    // Parsear la fecha del plan de String a LocalDate
    val planDate: LocalDate? = remember(planDateString) {
        planDateString?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                Log.e("RecipeSearchScreen", "Error parsing planDate: $it", e)
                null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->

        when(val state = recipeState) {
            is Result.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Result.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
            is Result.Success -> {
                val allRecipes = state.data
                val filteredRecipes = remember(category, allRecipes) {
                    if (category.equals("todas", ignoreCase = true)) {
                        allRecipes
                    } else {
                        allRecipes.filter { it.category.equals(category, ignoreCase = true) }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredRecipes) { recipe ->
                        SmallRecipeCard(
                            recipe = recipe,
                            onClick = {
                                if (planDate != null) {
                                    coroutineScope.launch {
                                        // Convertir LocalDate a java.util.Date para addRecipeToPlan
                                        val dateAsUtilDate = Date.from(planDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

                                        // 1. Añadir la receta al plan a través del ViewModel
                                        plannerViewModel.addRecipeToPlan(
                                            recipe.id,
                                            category, // 'category' es el 'mealTime' que viene de PlanScreen
                                            dateAsUtilDate,
                                            currentStartDate // Pasando el currentStartDate para el refresh del plan semanal
                                        )

                                        // 2. Navegar de vuelta a PlanScreen de forma que force la recomposición
                                        navController.navigate(Screen.Plan.route) {
                                            // Limpia la pila hasta la pantalla de inicio (asumiendo que Plan es la raíz o casi la raíz)
                                            // Esto fuerza a PlanScreen a ser recreada y obtener el estado más reciente.
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true // Evita duplicados de PlanScreen
                                            restoreState = false // Esto es CLAVE: no restaura el estado antiguo, fuerza la carga del nuevo
                                        }
                                    }
                                } else {
                                    Log.e("RecipeSearchScreen", "No se pudo obtener la fecha del plan. No se añadió la receta.")
                                    // Opcional: mostrar un Toast al usuario indicando el error.
                                    navController.popBackStack() // Volver sin añadir si hay un error de fecha
                                }
                            },
                            favoritesViewModel = favoritesViewModel
                        )
                    }
                }
            }
        }
    }
}