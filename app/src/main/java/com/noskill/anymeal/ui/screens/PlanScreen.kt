/**
 * PlanScreen.kt
 *
 * Propósito: Define la pantalla del planificador de comidas de la aplicación AnyMeal.
 * Permite a los usuarios visualizar y gestionar su plan semanal de alimentación,
 * organizado por días y tipos de comidas (desayuno, almuerzo, cena, snacks).
 * Incluye navegación entre semanas, resumen nutricional y gestión de notas diarias.
 */
package com.noskill.anymeal.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.noskill.anymeal.data.navItems
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.components.*
import com.noskill.anymeal.ui.models.DailyPlan
import com.noskill.anymeal.ui.models.NutritionInfo
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.util.PlanChangeNotifier
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Composable principal que define la pantalla del planificador de comidas.
 * Gestiona la visualización y modificación de planes de comidas semanales,
 * permitiendo al usuario navegar entre semanas, seleccionar días y administrar
 * las recetas asignadas a cada tiempo de comida.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param plannerViewModel ViewModel que gestiona los datos y operaciones de planificación de comidas
 * @param favoritesViewModel ViewModel que maneja las recetas favoritas del usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    navController: NavController,
    plannerViewModel: PlannerViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    // Constantes y estados para la navegación de días y semanas
    val weekDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val planResult by plannerViewModel.planState.collectAsState()

    // Estados para controlar la navegación temporal del planificador
    var weekOffset by remember { mutableStateOf(0) } // Desplazamiento de semanas relativo a la actual
    val calendar = remember { Calendar.getInstance() }
    val todayIndex = remember { (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 } // Índice del día actual en la semana
    var selectedDayIndex by remember { mutableStateOf(todayIndex) } // Día seleccionado actualmente
    var expandedItemKey by remember { mutableStateOf<String?>(null) } // Controla qué sección de comida está expandida
    val daySelectorState = rememberLazyListState() // Estado para el selector de días horizontal
    val coroutineScope = rememberCoroutineScope()

    // Estado para la navegación inferior
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedIndex = navItems.indexOfFirst { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }.coerceAtLeast(0)

    // Variable para controlar si ya se hizo el scroll inicial
    var hasInitialScrolled by remember { mutableStateOf(false) }

    // CORREGIDO: Efecto principal que maneja el scroll automático al entrar/regresar a la pantalla
    LaunchedEffect(currentDestination?.route) {
        if (currentDestination?.route == "plan") {
            Log.d("PlanScreen", "🔄 Entrando/regresando a PlanScreen")

            // Resetear al día actual y semana actual
            weekOffset = 0
            selectedDayIndex = todayIndex

            // Auto-scroll al día actual con un pequeño delay para asegurar que el LazyRow esté listo
            kotlinx.coroutines.delay(100)
            daySelectorState.animateScrollToItem(todayIndex)
            hasInitialScrolled = true

            Log.d("PlanScreen", "✅ Auto-scroll completado - día: $todayIndex")
        }
    }

    // Cálculo de la fecha de inicio de la semana actual basado en el desplazamiento
    val currentStartDate = remember(weekOffset) {
        // CORREGIDO: Usar LocalDate directamente en lugar de Calendar para evitar desfases
        val today = LocalDate.now()
        val startOfCurrentWeek = today.with(java.time.DayOfWeek.MONDAY)
        startOfCurrentWeek.plusWeeks(weekOffset.toLong())
    }

    // Efecto para cargar los datos del plan al cambiar de semana
    LaunchedEffect(currentStartDate) {
        Log.d("PlanScreen", "Fetching weekly plan for: $currentStartDate")
        plannerViewModel.fetchWeeklyPlan(currentStartDate)
    }

    // Auto-scroll cuando el usuario selecciona manualmente un día diferente
    LaunchedEffect(selectedDayIndex) {
        // Solo hacer scroll si ya se completó el scroll inicial y el usuario cambió el día manualmente
        if (hasInitialScrolled && selectedDayIndex != todayIndex) {
            daySelectorState.animateScrollToItem(selectedDayIndex)
            Log.d("PlanScreen", "📍 Scroll manual a día: $selectedDayIndex")
        }
    }

    // NUEVO: Escuchar cambios en el plan y sincronizar automáticamente
    LaunchedEffect(PlanChangeNotifier.planChanged) {
        PlanChangeNotifier.planChanged.collect { event ->
            if (event.modifiedDate != null) {
                Log.d("PlanScreen", "🔔 CAMBIO_EN_EL_PLAN detectado - fecha: ${event.modifiedDate}")

                // Calcular el weekOffset necesario para mostrar la semana que contiene la fecha
                val newWeekOffset = com.noskill.anymeal.util.DateUtils.getWeekOffsetForDate(event.modifiedDate)

                // Calcular el índice del día dentro de esa semana
                val dayOfWeek = event.modifiedDate.dayOfWeek.value
                val newDayIndex = if (dayOfWeek == 7) 6 else dayOfWeek - 1 // Convertir domingo (7) a índice 6

                Log.d("PlanScreen", "🔄 SINCRONIZANDO: weekOffset=$newWeekOffset, dayIndex=$newDayIndex")

                // Actualizar el estado para mostrar la semana y día correctos
                weekOffset = newWeekOffset
                selectedDayIndex = newDayIndex

                // Auto-scroll inmediato al día sincronizado
                daySelectorState.animateScrollToItem(newDayIndex)
            }
        }
    }

    // Cálculo de la fecha seleccionada como LocalDate
    val selectedDateAsLocalDate = remember(weekOffset, selectedDayIndex) {
        // CORREGIDO: Usar LocalDate consistentemente
        val today = LocalDate.now()
        val startOfCurrentWeek = today.with(java.time.DayOfWeek.MONDAY)
        val targetWeekStart = startOfCurrentWeek.plusWeeks(weekOffset.toLong())
        targetWeekStart.plusDays(selectedDayIndex.toLong())
    }

    // Cálculo de textos para mostrar el rango de fechas de la semana y la fecha seleccionada
    val (weekDateRange, selectedDateString) = remember(weekOffset, selectedDayIndex) {
        // CORREGIDO: Usar LocalDate consistenteemente para evitar desfases
        val today = LocalDate.now()
        val startOfCurrentWeek = today.with(java.time.DayOfWeek.MONDAY)
        val targetWeekStart = startOfCurrentWeek.plusWeeks(weekOffset.toLong())
        val targetWeekEnd = targetWeekStart.plusDays(6) // Domingo

        val weekFormat = java.time.format.DateTimeFormatter.ofPattern("d MMM", Locale("es", "ES"))
        val weekRangeStr = "${targetWeekStart.format(weekFormat)} - ${targetWeekEnd.format(weekFormat)}"

        // Calcular la fecha seleccionada
        val selectedDate = targetWeekStart.plusDays(selectedDayIndex.toLong())
        val dayFormat = java.time.format.DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
        val selectedDateStr = selectedDate.format(dayFormat).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        Pair(weekRangeStr, selectedDateStr)
    }

    // Clave para identificar el día seleccionado en los datos del plan
    val dayKey = remember(selectedDateAsLocalDate) {
        selectedDateAsLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    // Estructura principal de la pantalla con la barra de navegación inferior flotante
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            // Manejo de diferentes estados de la carga del plan
            when (val state = planResult) {
                // Estado de carga: muestra un indicador circular centrado
                is Result.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                // Estado de error: muestra el mensaje de error
                is Result.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Estado de éxito: muestra el plan para el día seleccionado
                is Result.Success -> {
                    val planData = state.data
                    // Obtiene el plan para el día seleccionado o crea uno nuevo si no existe
                    val currentPlan = planData[dayKey] ?: DailyPlan(planDate = selectedDateAsLocalDate)

                    // Cálculo simplificado del resumen nutricional basado en el número total de recetas
                    val totalRecipesInDay = currentPlan.meals.values.sumOf { it.size }
                    val nutritionSummary = remember(totalRecipesInDay) {
                        NutritionInfo(
                            calories = totalRecipesInDay * 450,
                            protein = totalRecipesInDay * 30,
                            carbs = totalRecipesInDay * 50,
                            fat = totalRecipesInDay * 15
                        )
                    }

                    // Lista principal con desplazamiento vertical
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp), // Espacio para la barra de navegación
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cabecera con fecha, navegador semanal, resumen nutricional y selector de días
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Título con la fecha seleccionada formateada
                                Text(
                                    text = selectedDateString,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                // Navegador para cambiar entre semanas
                                WeekNavigator(
                                    weekDateRange = weekDateRange,
                                    onPreviousWeek = { weekOffset-- },
                                    onNextWeek = { weekOffset++ }
                                )
                                // Tarjeta con resumen nutricional del día
                                NutritionSummaryCard(nutritionInfo = nutritionSummary)
                                // Selector horizontal de días de la semana
                                DaySelector(
                                    days = weekDays,
                                    selectedDayIndex = selectedDayIndex,
                                    onDaySelected = { index ->
                                        selectedDayIndex = index
                                        // Eliminado el scroll manual redundante - se maneja automáticamente
                                        // por el LaunchedEffect(selectedDayIndex)
                                    },
                                    lazyListState = daySelectorState
                                )
                            }
                        }

                        // Definición de los tipos de comida con sus iconos correspondientes
                        val mealTypes = listOf(
                            "Desayuno" to Icons.Default.FreeBreakfast,
                            "Almuerzo" to Icons.Default.LunchDining,
                            "Cena" to Icons.Default.DinnerDining,
                            "Snacks" to Icons.Default.Fastfood
                        )

                        // Genera una sección expandible para cada tipo de comida
                        items(mealTypes, key = { it.first }) { (title, icon) ->
                            val entriesForMeal = currentPlan.meals[title] ?: emptyList()
                            Log.d("PlanScreen", "Rendering $title: ${entriesForMeal.size} entries")

                            // Sección expandible para un tipo de comida
                            ExpandableMealSection(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                title = title,
                                icon = icon,
                                entries = entriesForMeal,
                                isExpanded = expandedItemKey == title,
                                onHeaderClick = {
                                    expandedItemKey = if (expandedItemKey == title) null else title
                                },
                                // Navegación a la búsqueda de recetas para añadir al plan
                                onAddClick = {
                                    expandedItemKey = title
                                    navController.navigate(
                                        Screen.RecipeSearch.createRoute(
                                            mealTime = title,
                                            planDate = selectedDateAsLocalDate.toString() // Pasa la fecha como String
                                        )
                                    )
                                },
                                // Navegación al detalle de una receta desde el plan
                                onRecipeClick = { recipeId ->
                                    navController.navigate(
                                        Screen.RecipeDetail.createRoute(
                                            recipeId,
                                            source = "plan",
                                            mealTime = title
                                        )
                                    )
                                },
                                // Eliminación de una entrada del plan
                                onDeleteEntry = { entryId ->
                                    // CORREGIDO: Pasar la fecha específica de la receta eliminada
                                    plannerViewModel.deletePlanEntry(entryId, currentStartDate, selectedDateAsLocalDate)
                                },
                                favoritesViewModel = favoritesViewModel
                            )
                        }

                        // Sección para notas diarias
                        item(key = "notes_card") {
                            DailyNotesSection(
                                notes = currentPlan.notes,
                                onNotesChange = { newNotes ->
                                    plannerViewModel.updateNotes(newNotes, dayKey, currentStartDate)
                                },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
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
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}