package com.noskill.anymeal.ui.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.BakeryDining
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.* // Importar todo de runtime
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.viewmodel.PlannerViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

private data class QuickDate(
    val dayName: String,
    val dayNumber: String,
    val date: Date
)

private data class MealType(
    val name: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlanDialog(
    navController: NavController,
    plannerViewModel: PlannerViewModel,
    recipeId: Int,
    source: String?,
    initialMealTime: String?, // MealTime preseleccionado (si viene de RecipeSearchScreen)
    onDismiss: () -> Unit
) {
    var selectedMealTime by remember { mutableStateOf(initialMealTime ?: "Desayuno") }
    val mealTypes = listOf(
        MealType("Desayuno", Icons.Outlined.WbSunny),
        MealType("Almuerzo", Icons.Outlined.LunchDining),
        MealType("Cena", Icons.Outlined.DinnerDining),
        MealType("Snacks", Icons.Outlined.BakeryDining)
    )

    var selectedDate by remember { mutableStateOf(getStartOfToday()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val quickDates = remember { generateQuickDates() }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time,
        initialDisplayMode = DisplayMode.Picker
    )
    val coroutineScope = rememberCoroutineScope()

    // CORRECCIÓN CLAVE: Recoger el StateFlow aquí, fuera de la lambda onClick
    val currentStartDateForRefresh by plannerViewModel.currentStartDate.collectAsState(initial = LocalDate.now())


    val datePickerColors = DatePickerDefaults.colors(
        containerColor = Color.Transparent
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            if (showDatePicker) {
                Column(
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DatePicker(
                        state = datePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = datePickerColors
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDate = Date(it)
                            }
                            showDatePicker = false
                        }) {
                            Text("Aceptar")
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    Text(
                        text = "Añadir al Plan",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 24.dp, start = 24.dp, end = 24.dp).align(Alignment.CenterHorizontally)
                    )

                    Text(
                        "Elige un día",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        items(quickDates) { quickDate ->
                            DateChip(
                                quickDate = quickDate,
                                isSelected = isSameDay(selectedDate, quickDate.date),
                                onClick = { selectedDate = quickDate.date }
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier.height(64.dp).padding(start = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Elegir otra fecha")
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                    Text(
                        "Elige una comida",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                    )
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        mealTypes.forEach { meal ->
                            MealTypeRow(
                                mealType = meal,
                                isSelected = selectedMealTime == meal.name,
                                onSelected = { selectedMealTime = meal.name }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(onClick = {
                            coroutineScope.launch {
                                // Usamos el valor ya recogido
                                plannerViewModel.addRecipeToPlan(
                                    recipeId,
                                    selectedMealTime,
                                    selectedDate,
                                    currentStartDateForRefresh // Usamos el valor del StateFlow
                                )
                                onDismiss()

                                if (source == "plan_from_search" || source == "home") {
                                    navController.navigate(Screen.Plan.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        }) { Text("Confirmar") }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateChip(
    quickDate: QuickDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(200),
        label = "dateChipContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "dateChipContent"
    )

    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(color = containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = quickDate.dayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = quickDate.dayNumber,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun MealTypeRow(
    mealType: MealType,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        onClick = onSelected,
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = mealType.icon,
                contentDescription = mealType.name,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = mealType.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getStartOfToday(): Date {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

private fun generateQuickDates(): List<QuickDate> {
    val calendar = Calendar.getInstance()
    val dates = mutableListOf<QuickDate>()
    val dayNameFormat = SimpleDateFormat("EEE", Locale("es", "ES"))
    val dayNumberFormat = SimpleDateFormat("d", Locale.getDefault())

    val today = getStartOfToday()
    dates.add(QuickDate("Hoy", dayNumberFormat.format(today), today))

    calendar.time = today
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrow = calendar.time
    dates.add(QuickDate("Mañana", dayNumberFormat.format(tomorrow), tomorrow))

    repeat(5) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val date = calendar.time
        val dayName = dayNameFormat.format(date).replaceFirstChar { it.uppercase() }
        val dayNumber = dayNumberFormat.format(date)
        dates.add(QuickDate(dayName, dayNumber, date))
    }
    return dates
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return fmt.format(date1) == fmt.format(date2)
}