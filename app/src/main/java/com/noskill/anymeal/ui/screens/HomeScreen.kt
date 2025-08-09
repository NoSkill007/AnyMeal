/**
 * HomeScreen.kt
 *
 * Propósito: Define la pantalla principal (Home) de la aplicación AnyMeal.
 * Muestra recetas recomendadas, permite búsquedas, filtrado por categorías
 * y ofrece acceso rápido a otras funcionalidades a través de la barra de
 * navegación inferior. Funciona como hub central de interacción del usuario.
 */
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

/**
 * Composable principal que define la pantalla de inicio de la aplicación.
 * Gestiona la visualización de recetas, búsquedas, filtros por categorías y
 * estados de carga/error. Integra la barra de navegación inferior flotante.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param favoritesViewModel ViewModel que maneja la lógica y datos de recetas favoritas
 * @param recipeViewModel ViewModel que gestiona la obtención y filtrado de recetas
 * @param userViewModel ViewModel que proporciona la información del usuario actual
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    recipeViewModel: RecipeViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel() // Se añade el UserViewModel
) {
    // Estados observables para las recetas y el usuario
    val recipeState by recipeViewModel.recipeListState.collectAsState()
    val userState by userViewModel.userState.collectAsState()

    // Estados locales para la búsqueda y filtrado
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Efecto que se ejecuta una sola vez al inicio para cargar las recetas iniciales
    LaunchedEffect(Unit) {
        recipeViewModel.fetchRecipes(null)
    }

    // Efecto dedicado exclusivamente a reaccionar a los cambios en la búsqueda con debounce
    LaunchedEffect(searchQuery) {
        // Se introduce una condición para no lanzar una búsqueda vacía la primera vez
        if (searchQuery.isNotEmpty()) {
            delay(500) // Espera 500ms después de la última letra tecleada (debounce)
            recipeViewModel.fetchRecipes(searchQuery)
        } else {
            // Si el usuario borra la búsqueda, volvemos a cargar todas las recetas
            recipeViewModel.fetchRecipes(null)
        }
    }

    // Extracción del nombre de usuario del estado actual
    val userName = when(val state = userState) {
        is Result.Success -> state.data?.username ?: "Chef"
        else -> "Chef" // Valor por defecto si no hay datos o hay error
    }

    // Lógica para determinar el elemento actualmente seleccionado en la navegación inferior
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedIndex = navItems.indexOfFirst { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }.coerceAtLeast(0)

    // Estructura principal de la pantalla con la barra de navegación inferior flotante
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            // Manejo de diferentes estados de la carga de recetas
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
                // Estado de éxito: muestra las recetas según filtros aplicados
                is Result.Success -> {
                    val allRecipes = state.data

                    // Aplica filtros según categoría seleccionada
                    val recipesToShow = remember(selectedCategory, allRecipes) {
                        val category = selectedCategory
                        if (category != null) {
                            if (category == "Sugerencias") {
                                allRecipes.shuffled().take(5) // Muestra 5 recetas aleatorias para sugerencias
                            } else {
                                allRecipes.filter { it.category.equals(category, ignoreCase = true) }
                            }
                        } else {
                            null // No aplica filtro, muestra vista por defecto
                        }
                    }

                    // Lista principal con desplazamiento vertical
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp), // Espacio para la barra de navegación
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cabecera con saludo y búsqueda, siempre visible
                        item {
                            HomeHeader(userName = userName, searchQuery = searchQuery, onSearchQueryChange = { searchQuery = it })
                        }

                        if (recipesToShow != null) {
                            // VISTA FILTRADA: cuando hay una categoría seleccionada
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
                                    onClick = { navController.navigate(Screen.RecipeDetail.createRoute(recipe.id.toInt())) },
                                    favoritesViewModel = favoritesViewModel,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        } else {
                            // VISTA POR DEFECTO: sin filtro de categoría
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
                                    onClick = { navController.navigate(Screen.RecipeDetail.createRoute(recipe.id.toInt())) },
                                    favoritesViewModel = favoritesViewModel,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Barra de navegación inferior flotante alineada en la parte inferior
        FloatingBottomNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                val destination = navItems[index].route
                if (currentDestination?.route != destination) {
                    navController.navigate(destination) {
                        // Configura la navegación para mantener un comportamiento correcto
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

/**
 * Composable que muestra la cabecera de la pantalla de inicio.
 * Incluye un saludo personalizado al usuario y una barra de búsqueda.
 *
 * @param userName Nombre del usuario para mostrar en el saludo
 * @param searchQuery Texto actual de la consulta de búsqueda
 * @param onSearchQueryChange Callback para notificar cambios en la consulta
 */
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

/**
 * Composable que muestra una sección horizontal de categorías para filtrado.
 * Permite seleccionar/deseleccionar categorías para filtrar recetas.
 *
 * @param selectedCategory Categoría actualmente seleccionada o null si no hay filtro
 * @param onCategorySelect Callback que se invoca cuando se selecciona una categoría
 */
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

/**
 * Composable que representa una píldora (chip) de categoría seleccionable.
 * Cambia de apariencia según su estado de selección con animaciones suaves.
 *
 * @param name Nombre de la categoría a mostrar
 * @param icon Icono vectorial que representa visualmente la categoría
 * @param isSelected Indica si esta categoría está actualmente seleccionada
 * @param onClick Callback que se invoca cuando se hace clic en la categoría
 */
@Composable
private fun CategoryPill(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animaciones de color para transiciones suaves entre estados
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

    // Tarjeta con forma de píldora para la categoría
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

/**
 * Composable que muestra la cabecera de resultados filtrados.
 * Incluye el nombre de la categoría, número de resultados y opción para limpiar el filtro.
 *
 * @param category Nombre de la categoría que se está filtrando
 * @param count Número de recetas que coinciden con el filtro
 * @param onClearFilter Callback que se invoca cuando se solicita limpiar el filtro
 */
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
