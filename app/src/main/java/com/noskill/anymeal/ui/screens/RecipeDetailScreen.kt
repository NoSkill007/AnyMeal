/**
 * RecipeDetailScreen.kt
 *
 * Propósito: Define la pantalla de detalle de receta de la aplicación AnyMeal.
 * Muestra información completa sobre una receta específica, incluyendo imagen,
 * ingredientes, pasos de preparación, categoría, tiempo de cocción y dificultad.
 * Permite al usuario marcar/desmarcar como favorito y añadir la receta a su plan de comidas.
 */
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
package com.noskill.anymeal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noskill.anymeal.R
import com.noskill.anymeal.ui.components.AddToPlanDialog
import com.noskill.anymeal.ui.models.RecipeDetailUi
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel
import com.noskill.anymeal.viewmodel.RecipeViewModel
import java.time.LocalDate
import java.util.Calendar

/**
 * Composable principal que define la pantalla de detalle de receta.
 * Gestiona la carga de datos de la receta, su visualización, favoritos,
 * y la navegación contextual dependiendo del origen de la navegación.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param recipeId Identificador único de la receta a mostrar
 * @param source Origen de la navegación ("plan", "home", "plan_from_search"), utilizado para la lógica de retorno
 * @param mealTime Tiempo de comida para el que se está añadiendo la receta (desayuno, almuerzo, cena, etc.)
 * @param plannerViewModel ViewModel que gestiona los planes de comidas
 * @param favoritesViewModel ViewModel que maneja las recetas favoritas
 * @param recipeViewModel ViewModel que obtiene y gestiona los detalles de recetas
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: Int?,
    source: String?,
    mealTime: String?,
    plannerViewModel: PlannerViewModel,
    favoritesViewModel: FavoritesViewModel,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    // Efecto para cargar los detalles de la receta cuando se inicia la pantalla
    LaunchedEffect(key1 = recipeId) {
        if (recipeId != null) {
            recipeViewModel.fetchRecipeById(recipeId)
        }
    }

    // Estado observable de los detalles de la receta
    val recipeState by recipeViewModel.recipeDetailState.collectAsState()

    // Manejo de diferentes estados de la carga de detalles de receta
    when (val state = recipeState) {
        // Estado de carga: muestra un indicador circular centrado
        is Result.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // Estado de error: muestra el mensaje de error
        is Result.Error -> {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            }
        }
        // Estado de éxito: muestra los detalles de la receta
        is Result.Success -> {
            val recipe = state.data
            if (recipe == null) {
                // Si no se encuentra la receta, muestra una pantalla específica
                RecipeNotFoundScreen(navController)
                return
            }

            // Estado para controlar la visibilidad del diálogo de añadir al plan
            var showAddToPlanDialog by remember { mutableStateOf(false) }
            // Estado de si la receta está marcada como favorita
            val favoriteIds by favoritesViewModel.favoriteRecipeIds.collectAsState()
            val isFavorite = recipe.id in favoriteIds

            // Diálogo para añadir la receta a un plan de comidas
            if (showAddToPlanDialog) {
                AddToPlanDialog(
                    navController = navController,
                    plannerViewModel = plannerViewModel,
                    recipeId = recipe.id,
                    source = source,
                    initialMealTime = if (source == "plan_from_search") mealTime else null,
                    onDismiss = { showAddToPlanDialog = false }
                )
            }

            // Estructura principal de la pantalla con botón flotante para añadir al plan
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { showAddToPlanDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir al Plan")
                    }
                }
            ) { scaffoldPadding ->
                // Estado para controlar el scroll y la visibilidad del título en la barra superior
                val lazyListState = rememberLazyListState()
                val isTitleVisible by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex > 0
                    }
                }

                Box(modifier = Modifier.fillMaxSize().padding(scaffoldPadding)) {
                    // Lista principal con desplazamiento vertical
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el botón flotante
                    ) {
                        // Imagen de cabecera con botón de retorno
                        item {
                            ImageHeader(recipe = recipe, navController = navController, source = source)
                        }
                        // Contenido principal con título, favorito y metadatos
                        item {
                            RecipeHeaderContent(
                                recipe = recipe,
                                isFavorite = isFavorite,
                                onFavoriteClick = {
                                    favoritesViewModel.toggleFavorite(recipe.id)
                                }
                            )
                        }
                        // Pestañas para ingredientes y pasos de preparación
                        item {
                            RecipeTabs(recipe = recipe)
                        }
                    }

                    // Barra superior con título que aparece al hacer scroll
                    AnimatedVisibility(
                        visible = isTitleVisible,
                        enter = fadeIn(animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        TopAppBar(
                            title = { Text(recipe.title, maxLines = 1) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    // Lógica de navegación inteligente según el origen
                                    if (source == "plan_from_search") {
                                        // Si viene del flujo de búsqueda para añadir al plan,
                                        // navega directamente a la pantalla del plan
                                        navController.navigate(com.noskill.anymeal.navigation.Screen.Plan.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                            restoreState = false // Para forzar la recomposición
                                        }
                                    } else {
                                        // Para otros orígenes, simplemente vuelve atrás
                                        navController.popBackStack()
                                    }
                                }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable que muestra la imagen de cabecera de la receta con un botón de retorno.
 * Permite la navegación contextual dependiendo del origen de la navegación.
 *
 * @param recipe Modelo de datos de la receta a mostrar
 * @param navController Controlador de navegación para gestionar la navegación
 * @param source Origen de la navegación para determinar el comportamiento del botón de retorno
 */
@Composable
private fun ImageHeader(recipe: RecipeDetailUi, navController: NavController, source: String?) {
    Box(contentAlignment = Alignment.TopStart) {
        // Imagen de la receta cargada de forma asíncrona
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(recipe.imageUrl)
                .crossfade(true)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .build(),
            contentDescription = recipe.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        // Botón de retorno con fondo semitransparente
        IconButton(
            onClick = {
                // Lógica de navegación inteligente según el origen
                if (source == "plan_from_search") {
                    navController.navigate(com.noskill.anymeal.navigation.Screen.Plan.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                } else {
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 16.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Composable que muestra la información principal de la receta.
 * Incluye título, botón de favorito, y metadatos como tiempo, dificultad y categoría.
 *
 * @param recipe Modelo de datos de la receta a mostrar
 * @param isFavorite Indica si la receta está marcada como favorita
 * @param onFavoriteClick Callback que se invoca cuando se hace clic en el botón de favorito
 */
@Composable
private fun RecipeHeaderContent(
    recipe: RecipeDetailUi,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Fila con título y botón de favorito
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            // Botón toggle para marcar/desmarcar como favorito
            IconToggleButton(
                checked = isFavorite,
                onCheckedChange = { onFavoriteClick() },
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    modifier = Modifier.size(32.dp),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Fila con flujo adaptativo de chips informativos
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoChip(icon = Icons.Outlined.AccessTime, text = recipe.time)
            InfoChip(icon = Icons.Outlined.RestaurantMenu, text = recipe.difficulty)
            InfoChip(icon = Icons.Outlined.Category, text = recipe.category)
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Descripción de la receta (si existe)
        if (recipe.description.isNotBlank()) {
            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }
    }
}

/**
 * Composable que muestra las pestañas para alternar entre ingredientes y pasos de preparación.
 * Gestiona el estado de la pestaña seleccionada y muestra el contenido correspondiente.
 *
 * @param recipe Modelo de datos de la receta que contiene ingredientes y pasos
 */
@Composable
private fun RecipeTabs(recipe: RecipeDetailUi) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Ingredientes", "Preparación")

    Column(modifier = Modifier.padding(top = 16.dp)) {
        // Barra de pestañas para seleccionar entre ingredientes y preparación
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) }
                )
            }
        }
        // Contenido que cambia según la pestaña seleccionada
        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            when (selectedTabIndex) {
                0 -> IngredientsContent(ingredients = recipe.ingredients)
                1 -> StepsContent(steps = recipe.steps)
            }
        }
    }
}

/**
 * Composable que muestra la lista de ingredientes de la receta.
 * Cada ingrediente se presenta con un formato de viñeta para mejor legibilidad.
 *
 * @param ingredients Lista de ingredientes a mostrar
 */
@Composable
private fun IngredientsContent(ingredients: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (ingredients.isNotEmpty()) {
            ingredients.forEach { ingredient ->
                Text(
                    text = "• $ingredient",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Text(
                "No hay ingredientes listados.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Composable que muestra los pasos de preparación de la receta.
 * Cada paso se presenta numerado y con formato adecuado para seguir instrucciones.
 *
 * @param steps Lista de pasos de preparación a mostrar
 */
@Composable
private fun StepsContent(steps: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (steps.isNotEmpty()) {
            steps.forEachIndexed { index, step ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Número de paso destacado
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    // Descripción del paso
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 22.sp
                    )
                }
            }
        } else {
            Text(
                "No hay pasos de preparación disponibles.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Composable que muestra un chip informativo con icono y texto.
 * Utilizado para mostrar metadatos de la receta como tiempo, dificultad y categoría.
 *
 * @param icon Icono vectorial que representa visualmente el tipo de información
 * @param text Texto descriptivo del valor de la información
 */
@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

/**
 * Composable que muestra una pantalla de error cuando no se encuentra la receta.
 * Proporciona información visual y textual sobre el problema, con opción para volver atrás.
 *
 * @param navController Controlador de navegación para volver a la pantalla anterior
 */
@Composable
private fun RecipeNotFoundScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Botón de retorno en la parte superior
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        // Contenido central con mensaje de error
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de advertencia
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Mensaje de error principal
            Text(
                "Receta no disponible",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Mensaje explicativo
            Text(
                "Parece que esta receta fue eliminada o no está disponible. Puedes volver e intentar con otra.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}