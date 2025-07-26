@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) // <-- Asegúrate de que ExperimentalLayoutApi está aquí
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
import androidx.navigation.NavGraph.Companion.findStartDestination // Importar findStartDestination
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noskill.anymeal.R
import com.noskill.anymeal.ui.components.AddToPlanDialog // Tu componente AddToPlanDialog
import com.noskill.anymeal.ui.models.RecipeDetailUi
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel
import com.noskill.anymeal.viewmodel.RecipeViewModel
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: Int?,
    source: String?, // Puede ser "plan", "home", "plan_from_search"
    mealTime: String?, // <-- AÑADIDO: Nuevo parámetro para recibir el mealTime
    plannerViewModel: PlannerViewModel,
    favoritesViewModel: FavoritesViewModel,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    LaunchedEffect(key1 = recipeId) {
        if (recipeId != null) {
            recipeViewModel.fetchRecipeById(recipeId)
        }
    }

    val recipeState by recipeViewModel.recipeDetailState.collectAsState()

    when (val state = recipeState) {
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
            val recipe = state.data
            if (recipe == null) {
                RecipeNotFoundScreen(navController)
                return
            }

            var showAddToPlanDialog by remember { mutableStateOf(false) }
            val favoriteIds by favoritesViewModel.favoriteRecipeIds.collectAsState()
            val isFavorite = recipe.id in favoriteIds

            // Invocar tu AddToPlanDialog
            if (showAddToPlanDialog) {
                AddToPlanDialog(
                    navController = navController, // Pasar navController
                    plannerViewModel = plannerViewModel, // Pasar plannerViewModel
                    recipeId = recipe.id, // Pasar el ID de la receta actual
                    source = source, // Pasar el source para la lógica de navegación
                    initialMealTime = if (source == "plan_from_search") mealTime else null, // Pasar mealTime si viene de RecipeSearch
                    onDismiss = { showAddToPlanDialog = false }
                )
            }

            Scaffold(
                floatingActionButton = {
                    // El FAB se muestra siempre para añadir al plan
                    FloatingActionButton(onClick = { showAddToPlanDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir al Plan")
                    }
                }
            ) { scaffoldPadding ->
                val lazyListState = rememberLazyListState()
                val isTitleVisible by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex > 0
                    }
                }

                Box(modifier = Modifier.fillMaxSize().padding(scaffoldPadding)) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            ImageHeader(recipe = recipe, navController = navController, source = source) // Pasa source a ImageHeader
                        }
                        item {
                            RecipeHeaderContent(
                                recipe = recipe,
                                isFavorite = isFavorite,
                                onFavoriteClick = {
                                    favoritesViewModel.toggleFavorite(recipe.id)
                                }
                            )
                        }
                        item {
                            RecipeTabs(recipe = recipe)
                        }
                    }

                    AnimatedVisibility(
                        visible = isTitleVisible,
                        enter = fadeIn(animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        TopAppBar(
                            title = { Text(recipe.title, maxLines = 1) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    // Lógica de navegación de regreso inteligente
                                    if (source == "plan_from_search") {
                                        // Si venimos del flujo de añadir al plan desde RecipeSearchScreen,
                                        // navegamos directamente a PlanScreen forzando la recomposición.
                                        navController.navigate(com.noskill.anymeal.navigation.Screen.Plan.route) { // Usar la ruta completa
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                            restoreState = false // CLAVE para forzar refresh
                                        }
                                    } else {
                                        // Para otros orígenes, simplemente popBackStack
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

@Composable
private fun ImageHeader(recipe: RecipeDetailUi, navController: NavController, source: String?) { // Recibe source
    Box(contentAlignment = Alignment.TopStart) {
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
        IconButton(
            onClick = {
                // Lógica de navegación de regreso inteligente para el botón de la imagen
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

@Composable
private fun RecipeHeaderContent(
    recipe: RecipeDetailUi,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
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
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoChip(icon = Icons.Outlined.AccessTime, text = recipe.time)
            InfoChip(icon = Icons.Outlined.RestaurantMenu, text = recipe.difficulty)
            InfoChip(icon = Icons.Outlined.Category, text = recipe.category)
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (recipe.description.isNotBlank()) {
            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun RecipeTabs(recipe: RecipeDetailUi) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Ingredientes", "Preparación")

    Column(modifier = Modifier.padding(top = 16.dp)) {
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
        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            when (selectedTabIndex) {
                0 -> IngredientsContent(ingredients = recipe.ingredients)
                1 -> StepsContent(steps = recipe.steps)
            }
        }
    }
}

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

@Composable
private fun StepsContent(steps: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (steps.isNotEmpty()) {
            steps.forEachIndexed { index, step ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
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

@Composable
private fun RecipeNotFoundScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Receta no disponible",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
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