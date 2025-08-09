/**
 * FavoritesScreen.kt
 *
 * Propósito: Define la pantalla de recetas favoritas de la aplicación AnyMeal.
 * Muestra la colección de recetas que el usuario ha marcado como favoritas,
 * permitiendo una rápida visualización y acceso a las mismas. Implementa
 * estados visuales para cargas, errores y situación de lista vacía.
 */
package com.noskill.anymeal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.components.SmallRecipeCard
import com.noskill.anymeal.viewmodel.FavoritesViewModel

/**
 * Composable principal que define la pantalla de recetas favoritas.
 * Muestra la lista de recetas marcadas como favoritas por el usuario,
 * con estados visuales específicos para diferentes situaciones (carga, error, vacío).
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param favoritesViewModel ViewModel que maneja la lógica y datos de recetas favoritas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    // Obtenemos el estado actual de favoritos desde el ViewModel
    val uiState by favoritesViewModel.uiState.collectAsState()

    // Estructura principal de la pantalla con barra superior
    Scaffold(
        topBar = {
            // Barra superior con título y botón de navegación para volver
            TopAppBar(
                title = { Text("Mis Recetas Favoritas", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        // Manejo de diferentes estados de la UI
        when {
            // Estado de carga: muestra un indicador circular centrado
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            // Estado de error: muestra el mensaje de error
            uiState.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
            // Estado de lista vacía: muestra una UI específica para cuando no hay favoritos
            uiState.favoriteRecipes.isEmpty() -> {
                EmptyFavoritesState(modifier = Modifier.padding(innerPadding))
            }
            // Estado normal: muestra la lista de recetas favoritas
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.favoriteRecipes, key = { it.id }) { recipe ->
                        SmallRecipeCard(
                            recipe = recipe,
                            onClick = {
                                // Navegación a la pantalla de detalle de receta
                                // Se convierte el ID de Long a Int antes de pasarlo a la función de navegación
                                navController.navigate(Screen.RecipeDetail.createRoute(recipe.id.toInt()))
                            },
                            favoritesViewModel = favoritesViewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable que muestra un estado visual específico para cuando no hay recetas favoritas.
 * Presenta un mensaje informativo y un ícono ilustrativo que invita al usuario a explorar
 * y marcar recetas como favoritas.
 *
 * @param modifier Modificador opcional para personalizar el layout
 */
@Composable
private fun EmptyFavoritesState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icono ilustrativo para el estado vacío
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "No hay favoritos",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Mensaje principal
            Text(
                "Tu rincón de favoritos está vacío",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Mensaje secundario con sugerencia
            Text(
                "Explora y marca las recetas que más te gusten para verlas aquí.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}