// ========================================================================
// Archivo: ui/screens/HomeScreen.kt (COMPLETO Y CORREGIDO)
// Propósito: Muestra la pantalla principal, obtiene recetas y perfil de usuario
// del backend y permite la búsqueda.
// ========================================================================
package com.noskill.anymeal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.noskill.anymeal.data.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.components.*
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.RecipeViewModel
import com.noskill.anymeal.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    recipeViewModel: RecipeViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel() // Se añade el UserViewModel
) {
    val recipeState by recipeViewModel.recipeListState.collectAsState()
    val userState by userViewModel.userState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // --- LÓGICA DE BÚSQUEDA MEJORADA ---
    // Este efecto ahora tiene una clave 'Unit' para ejecutarse solo una vez al inicio.
    LaunchedEffect(Unit) {
        recipeViewModel.fetchRecipes(null)
    }

    // Este segundo efecto se dedica exclusivamente a reaccionar a los cambios en la búsqueda.
    LaunchedEffect(searchQuery) {
        // Se introduce una condición para no lanzar una búsqueda vacía la primera vez.
        if (searchQuery.isNotEmpty()) {
            delay(500) // Espera 500ms después de la última letra tecleada
            recipeViewModel.fetchRecipes(searchQuery)
        } else {
            // Si el usuario borra la búsqueda, volvemos a cargar todas las recetas.
            recipeViewModel.fetchRecipes(null)
        }
    }

    // Extrae el nombre de usuario del estado
    val userName = when(val state = userState) {
        is Result.Success -> state.data?.username ?: "Chef"
        else -> "Chef"
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedIndex = navItems.indexOfFirst { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }.coerceAtLeast(0)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->

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
                    val allRecipes = state.data

                    val recipesToShow = remember(selectedCategory, allRecipes) {
                        val category = selectedCategory
                        if (category != null) {
                            if (category == "Sugerencias") {
                                allRecipes.shuffled().take(5)
                            } else {
                                allRecipes.filter { it.category.equals(category, ignoreCase = true) }
                            }
                        } else {
                            null
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            HomeHeader(userName = userName, searchQuery = searchQuery, onSearchQueryChange = { searchQuery = it })
                        }

                        if (recipesToShow != null) {
                            // VISTA FILTRADA
                            item {
                                AnimatedVisibility(visible = selectedCategory != "Sugerencias") {
                                    CategorySection(
                                        selectedCategory = selectedCategory,
                                        onCategorySelect = { category ->
                                            selectedCategory = if (selectedCategory == category) null else category
                                        }
                                    )
                                }
                            }
                            item {
                                FilterResultsHeader(
                                    category = selectedCategory ?: "Sugerencias",
                                    count = recipesToShow.size,
                                    onClearFilter = { selectedCategory = null }
                                )
                            }
                            items(recipesToShow) { recipe ->
                                SmallRecipeCard(
                                    recipe = recipe,
                                    onClick = { navController.navigate(Screen.RecipeDetail.createRoute(recipe.id.toInt())) }, // CORRECCIÓN
                                    favoritesViewModel = favoritesViewModel,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        } else {
                            // VISTA POR DEFECTO
                            item {
                                SuggestionOfDayCard(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = { selectedCategory = "Sugerencias" }
                                )
                            }
                            item {
                                CategorySection(
                                    selectedCategory = selectedCategory,
                                    onCategorySelect = { category ->
                                        selectedCategory = if (selectedCategory == category) null else category
                                    }
                                )
                            }
                            item {
                                SectionTitle(
                                    "Recetas para ti",
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                                )
                            }
                            items(allRecipes) { recipe ->
                                SmallRecipeCard(
                                    recipe = recipe,
                                    onClick = { navController.navigate(Screen.RecipeDetail.createRoute(recipe.id.toInt())) }, // CORRECCIÓN
                                    favoritesViewModel = favoritesViewModel,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingBottomNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                val destination = navItems[index].route
                if (currentDestination?.route != destination) {
                    navController.navigate(destination) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "👋 ¡Hola, $userName!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        SearchBar(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CategorySection(
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { cat ->
            CategoryPill(
                name = cat.name,
                icon = cat.icon,
                isSelected = cat.name == selectedCategory,
                onClick = { onCategorySelect(cat.name) }
            )
        }
    }
}

@Composable
private fun CategoryPill(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(300),
        label = "containerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "contentColor"
    )
    val border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun FilterResultsHeader(
    category: String,
    count: Int,
    onClearFilter: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Resultados para '$category' ($count)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onClearFilter) {
            Icon(Icons.Default.Clear, contentDescription = "Limpiar filtro", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Limpiar")
        }
    }
}
