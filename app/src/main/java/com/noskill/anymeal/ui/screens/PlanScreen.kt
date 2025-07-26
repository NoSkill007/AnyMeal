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
import com.noskill.anymeal.navigation.Screen // Asegúrate de que esto apunta a navigation.AppNavigation.kt
import com.noskill.anymeal.ui.components.*
import com.noskill.anymeal.ui.models.DailyPlan
import com.noskill.anymeal.ui.models.NutritionInfo
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    navController: NavController,
    plannerViewModel: PlannerViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val weekDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val planResult by plannerViewModel.planState.collectAsState()

    var weekOffset by remember { mutableStateOf(0) }
    val calendar = remember { Calendar.getInstance() }
    val todayIndex = remember { (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 }
    var selectedDayIndex by remember { mutableStateOf(todayIndex) }
    var expandedItemKey by remember { mutableStateOf<String?>(null) }
    val daySelectorState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedIndex = navItems.indexOfFirst { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }.coerceAtLeast(0)

    val currentStartDate = remember(weekOffset) {
        val weekCalendar = Calendar.getInstance()
        weekCalendar.add(Calendar.WEEK_OF_YEAR, weekOffset)
        weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        LocalDate.of(
            weekCalendar.get(Calendar.YEAR),
            weekCalendar.get(Calendar.MONTH) + 1,
            weekCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    LaunchedEffect(currentStartDate) {
        Log.d("PlanScreen", "Fetching weekly plan for: $currentStartDate")
        plannerViewModel.fetchWeeklyPlan(currentStartDate)
    }

    LaunchedEffect(Unit) {
        daySelectorState.animateScrollToItem(todayIndex)
    }

    // Calculamos la fecha seleccionada como LocalDate aquí
    val selectedDateAsLocalDate = remember(weekOffset, selectedDayIndex) {
        val weekCalendar = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, weekOffset)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        weekCalendar.add(Calendar.DAY_OF_YEAR, selectedDayIndex)
        weekCalendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    val (weekDateRange, selectedDateString) = remember(weekOffset, selectedDayIndex) {
        val weekCalendar = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, weekOffset)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val startOfWeekUtilDate = weekCalendar.time
        weekCalendar.add(Calendar.DAY_OF_YEAR, 6)
        val endOfWeekUtilDate = weekCalendar.time
        val weekFormat = SimpleDateFormat("d MMM", Locale("es", "ES"))
        val weekRangeStr = "${weekFormat.format(startOfWeekUtilDate)} - ${weekFormat.format(endOfWeekUtilDate)}"

        weekCalendar.time = startOfWeekUtilDate
        weekCalendar.add(Calendar.DAY_OF_YEAR, selectedDayIndex)
        val currentUtilDate = weekCalendar.time

        val dayFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
        val selectedDateStr = dayFormat.format(currentUtilDate).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        Pair(weekRangeStr, selectedDateStr)
    }

    val dayKey = remember(selectedDateAsLocalDate) {
        selectedDateAsLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            when (val state = planResult) {
                is Result.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is Result.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is Result.Success -> {
                    val planData = state.data
                    val currentPlan = planData[dayKey] ?: DailyPlan(planDate = selectedDateAsLocalDate)

                    val totalRecipesInDay = currentPlan.meals.values.sumOf { it.size }
                    val nutritionSummary = remember(totalRecipesInDay) {
                        NutritionInfo(
                            calories = totalRecipesInDay * 450,
                            protein = totalRecipesInDay * 30,
                            carbs = totalRecipesInDay * 50,
                            fat = totalRecipesInDay * 15
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = selectedDateString,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                WeekNavigator(
                                    weekDateRange = weekDateRange,
                                    onPreviousWeek = { weekOffset-- },
                                    onNextWeek = { weekOffset++ }
                                )
                                NutritionSummaryCard(nutritionInfo = nutritionSummary)
                                DaySelector(
                                    days = weekDays,
                                    selectedDayIndex = selectedDayIndex,
                                    onDaySelected = { index ->
                                        selectedDayIndex = index
                                        coroutineScope.launch {
                                            daySelectorState.animateScrollToItem(index)
                                        }
                                    },
                                    lazyListState = daySelectorState
                                )
                            }
                        }

                        val mealTypes = listOf(
                            "Desayuno" to Icons.Default.FreeBreakfast,
                            "Almuerzo" to Icons.Default.LunchDining,
                            "Cena" to Icons.Default.DinnerDining,
                            "Snacks" to Icons.Default.Fastfood
                        )

                        items(mealTypes, key = { it.first }) { (title, icon) ->
                            val entriesForMeal = currentPlan.meals[title] ?: emptyList()
                            Log.d("PlanScreen", "Rendering $title: ${entriesForMeal.size} entries")

                            ExpandableMealSection(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                title = title,
                                icon = icon,
                                entries = entriesForMeal,
                                isExpanded = expandedItemKey == title,
                                onHeaderClick = {
                                    expandedItemKey = if (expandedItemKey == title) null else title
                                },
                                // MODIFICADO: Pasar selectedDateAsLocalDate como String al navegar
                                onAddClick = {
                                    expandedItemKey = title
                                    navController.navigate(
                                        Screen.RecipeSearch.createRoute(
                                            mealTime = title,
                                            planDate = selectedDateAsLocalDate.toString() // Pasa la fecha
                                        )
                                    )
                                },
                                onRecipeClick = { recipeId ->
                                    // Pasa el mealTime a RecipeDetailScreen también, si es relevante para esa pantalla.
                                    // Aunque para este flujo no lo necesita para la adición, solo si lo muestra.
                                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId, source = "plan", mealTime = title))
                                },
                                onDeleteEntry = { entryId ->
                                    plannerViewModel.deletePlanEntry(entryId, currentStartDate)
                                },
                                favoritesViewModel = favoritesViewModel
                            )
                        }

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

        FloatingBottomNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                val destination = navItems[index].route
                if (currentDestination?.route != destination) {
                    navController.navigate(destination) {
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