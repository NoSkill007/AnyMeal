/**
 * AddToPlanDialog.kt
 *
 * Propósito: Define un diálogo interactivo que permite al usuario añadir una receta
 * a su plan de comidas. Ofrece opciones para seleccionar el día (con selección rápida
 * o calendario completo) y el tipo de comida (desayuno, almuerzo, cena, snacks).
 * Gestiona la comunicación con el ViewModel correspondiente y maneja la navegación
 * contextual según el origen de la solicitud.
 */
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
import com.noskill.anymeal.util.PlanChangeNotifier
import com.noskill.anymeal.viewmodel.PlannerViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

/**
 * Clase de datos que representa una fecha para selección rápida.
 * Muestra un día de la semana y su número correspondiente.
 *
 * @property dayName Nombre corto del día ("Hoy", "Mañana", "Lun", etc.)
 * @property dayNumber Número del día del mes
 * @property date Objeto Date completo para usar en operaciones
 */
private data class QuickDate(
    val dayName: String,
    val dayNumber: String,
    val date: Date
)

/**
 * Clase de datos que representa un tipo de comida con su nombre e icono.
 *
 * @property name Nombre del tipo de comida (Desayuno, Almuerzo, etc.)
 * @property icon Icono vectorial que representa visualmente el tipo de comida
 */
private data class MealType(
    val name: String,
    val icon: ImageVector
)

/**
 * Composable principal que muestra un diálogo para añadir una receta al plan de comidas.
 * Permite seleccionar día y tipo de comida, y maneja la navegación dependiendo del
 * origen desde donde se abrió el diálogo.
 *
 * @param navController Controlador de navegación para gestionar la navegación posterior
 * @param plannerViewModel ViewModel que gestiona las operaciones con planes de comidas
 * @param recipeId Identificador de la receta que se quiere añadir al plan
 * @param source Origen de la navegación ("plan_from_search", "home", etc.) para determinar el comportamiento de retorno
 * @param initialMealTime Tipo de comida preseleccionado (si viene de la pantalla de búsqueda de recetas)
 * @param onDismiss Callback que se invoca cuando se cierra el diálogo
 */
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
    // Estado para el tipo de comida seleccionado, con valor por defecto basado en initialMealTime
    var selectedMealTime by remember { mutableStateOf(initialMealTime ?: "Desayuno") }

    // Lista de tipos de comida disponibles con sus iconos
    val mealTypes = listOf(
        MealType("Desayuno", Icons.Outlined.WbSunny),
        MealType("Almuerzo", Icons.Outlined.LunchDining),
        MealType("Cena", Icons.Outlined.DinnerDining),
        MealType("Snacks", Icons.Outlined.BakeryDining)
    )

    // Estados para la fecha seleccionada y la visibilidad del selector de fecha
    var selectedDate by remember { mutableStateOf(getStartOfToday()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Genera las fechas para selección rápida (hoy, mañana, próximos días)
    val quickDates = remember { generateQuickDates() }

    // Estado para el selector de fecha completo
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time,
        initialDisplayMode = DisplayMode.Picker
    )

    val coroutineScope = rememberCoroutineScope()

    // Obtiene la fecha de inicio actual del plan para refrescar correctamente
    val currentStartDateForRefresh by plannerViewModel.currentStartDate.collectAsState(initial = LocalDate.now())

    // Colores para el selector de fecha
    val datePickerColors = DatePickerDefaults.colors(
        containerColor = Color.Transparent
    )

    // Diálogo principal
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            // Muestra el selector de fecha completo o la interfaz principal
            if (showDatePicker) {
                // Sección de selector de fecha completo
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
                    // Botones para confirmar o cancelar la selección de fecha
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
                // Interfaz principal del diálogo
                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    // Título del diálogo
                    Text(
                        text = "Añadir al Plan",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 24.dp, start = 24.dp, end = 24.dp).align(Alignment.CenterHorizontally)
                    )

                    // Sección de selección de día
                    Text(
                        "Elige un día",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                    )
                    // Fila horizontal de fechas rápidas (hoy, mañana, etc.)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        // Muestra los chips de fecha rápida
                        items(quickDates) { quickDate ->
                            DateChip(
                                quickDate = quickDate,
                                isSelected = isSameDay(selectedDate, quickDate.date),
                                onClick = { selectedDate = quickDate.date }
                            )
                        }
                        // Botón para abrir el selector de fecha completo
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

                    // Separador visual entre secciones
                    HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                    // Sección de selección de comida
                    Text(
                        "Elige una comida",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                    )
                    // Lista de tipos de comida
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

                    // Botones de acción (cancelar/confirmar)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(onClick = {
                            coroutineScope.launch {
                                // Añade la receta al plan con los valores seleccionados
                                plannerViewModel.addRecipeToPlan(
                                    recipeId,
                                    selectedMealTime,
                                    selectedDate,
                                    currentStartDateForRefresh // Usar para refrescar correctamente
                                )

                                // CRÍTICO: Notificar que el plan ha cambiado CON LA FECHA ESPECÍFICA
                                val selectedLocalDate = selectedDate.toInstant()
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                                PlanChangeNotifier.notifyPlanChanged(selectedLocalDate)

                                onDismiss()

                                // REMOVIDO: No navegar automáticamente, dejar que el usuario decida dónde ir
                            }
                        }) { Text("Confirmar") }
                    }
                }
            }
        }
    }
}

/**
 * Composable que muestra un chip circular con información de fecha.
 * Cambia de color cuando está seleccionado para indicar visualmente la elección.
 *
 * @param quickDate Datos de la fecha a mostrar (nombre del día y número)
 * @param isSelected Indica si este chip está actualmente seleccionado
 * @param onClick Callback invocado cuando se hace clic en el chip
 */
@Composable
private fun DateChip(
    quickDate: QuickDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animaciones de color para transición suave entre estados
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

    // Chip circular con información de fecha
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
            // Nombre del día (Hoy, Mañana, Lun, etc.)
            Text(
                text = quickDate.dayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Número del día
            Text(
                text = quickDate.dayNumber,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

/**
 * Composable que muestra una fila para seleccionar un tipo de comida.
 * Incluye un icono, el nombre del tipo de comida y cambia de apariencia cuando está seleccionado.
 *
 * @param mealType Datos del tipo de comida (nombre e icono)
 * @param isSelected Indica si este tipo de comida está actualmente seleccionado
 * @param onSelected Callback invocado cuando se selecciona este tipo de comida
 */
@Composable
private fun MealTypeRow(
    mealType: MealType,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    // Fila seleccionable con estilo diferente según selección
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
            // Icono del tipo de comida
            Icon(
                imageVector = mealType.icon,
                contentDescription = mealType.name,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Nombre del tipo de comida
            Text(
                text = mealType.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Función de utilidad que obtiene la fecha actual al inicio del día (00:00:00).
 * Útil para comparaciones de fechas sin considerar la hora.
 *
 * @return Objeto Date configurado al inicio del día actual
 */
private fun getStartOfToday(): Date {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

/**
 * Función de utilidad que genera una lista de fechas para selección rápida.
 * Incluye "Hoy", "Mañana" y los próximos 5 días con sus nombres abreviados.
 *
 * @return Lista de objetos QuickDate para mostrar en la interfaz
 */
private fun generateQuickDates(): List<QuickDate> {
    val calendar = Calendar.getInstance()
    val dates = mutableListOf<QuickDate>()
    val dayNameFormat = SimpleDateFormat("EEE", Locale("es", "ES"))
    val dayNumberFormat = SimpleDateFormat("d", Locale.getDefault())

    // Añade "Hoy"
    val today = getStartOfToday()
    dates.add(QuickDate("Hoy", dayNumberFormat.format(today), today))

    // Añade "Mañana"
    calendar.time = today
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrow = calendar.time
    dates.add(QuickDate("Mañana", dayNumberFormat.format(tomorrow), tomorrow))

    // Añade los próximos 5 días
    repeat(5) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val date = calendar.time
        val dayName = dayNameFormat.format(date).replaceFirstChar { it.uppercase() }
        val dayNumber = dayNumberFormat.format(date)
        dates.add(QuickDate(dayName, dayNumber, date))
    }
    return dates
}

/**
 * Función de utilidad que compara si dos fechas corresponden al mismo día.
 * Ignora la hora, minutos y segundos en la comparación.
 *
 * @param date1 Primera fecha a comparar
 * @param date2 Segunda fecha a comparar
 * @return true si ambas fechas representan el mismo día, false en caso contrario
 */
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return fmt.format(date1) == fmt.format(date2)
}