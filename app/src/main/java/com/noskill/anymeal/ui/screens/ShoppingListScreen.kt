/**
 * ShoppingListScreen.kt
 * 
 * Propósito: Define la pantalla de lista de compras de la aplicación AnyMeal.
 * Permite al usuario gestionar elementos de su lista de compras, incluyendo añadir,
 * editar, eliminar y marcar como completados. Ofrece funcionalidades de búsqueda,
 * filtrado por semana, y seguimiento del progreso de compra. Los elementos se
 * organizan por categorías para facilitar la experiencia de compra.
 */
package com.noskill.anymeal.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.noskill.anymeal.data.navItems
import com.noskill.anymeal.ui.components.FloatingBottomNavBar
import com.noskill.anymeal.ui.components.WeekNavigator
import com.noskill.anymeal.ui.models.ShoppingItem
import com.noskill.anymeal.viewmodel.ShoppingListUiState
import com.noskill.anymeal.viewmodel.ShoppingListViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable principal que define la pantalla de lista de compras.
 * Gestiona la visualización y manipulación de elementos de compra, incluyendo
 * búsqueda, filtrado por semana, y funcionalidades CRUD (crear, leer, actualizar, eliminar).
 * Implementa diálogos para añadir y editar elementos, y muestra el progreso de compra.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param viewModel ViewModel que maneja la lógica y datos de la lista de compras
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(
    navController: NavController,
    viewModel: ShoppingListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var weekOffset by remember { mutableStateOf(0) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showEditItemDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ShoppingItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Mostrar errores con Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Long
            )
            // Limpiar el error después de mostrarlo
            viewModel.clearError()
        }
    }

    // Mostrar mensajes de éxito con duración corta
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { successMessage ->
            snackbarHostState.showSnackbar(
                message = successMessage,
                duration = SnackbarDuration.Short  // Duración corta para mensajes de éxito
            )
            viewModel.clearError()
        }
    }

    LaunchedEffect(weekOffset) {
        Log.d("ShoppingListVM", "🚀 PANTALLA_INICIADA: weekOffset=$weekOffset")
        if (weekOffset == 0) {
            // Al cargar por primera vez o volver a la pantalla, usar getCurrentList
            Log.d("ShoppingListVM", "🚀 LLAMANDO getCurrentList() - primera carga")
            viewModel.getCurrentList()
        } else {
            // Solo regenerar desde el plan cuando realmente cambiemos de semana
            Log.d("ShoppingListVM", "🚀 LLAMANDO generateListForWeek($weekOffset)")
            viewModel.generateListForWeek(weekOffset)
        }
    }

    val allItems = uiState.shoppingList.values.flatten()
    val checkedItems = allItems.filter { it.isChecked }
    val progress = if (allItems.isNotEmpty()) checkedItems.size.toFloat() / allItems.size.toFloat() else 0f

    val filteredList = remember(searchQuery, uiState.shoppingList) {
        if (searchQuery.isBlank()) {
            uiState.shoppingList
        } else {
            uiState.shoppingList.mapValues { (_, items) ->
                items.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }.filter { it.value.isNotEmpty() }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ShoppingListHeader(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        checkedItems = checkedItems.size,
                        totalItems = allItems.size,
                        progress = progress
                    )
                }

                item {
                    val weekDateRange = remember(weekOffset) { getWeekDateRange(weekOffset) }
                    WeekNavigator(
                        weekDateRange = weekDateRange,
                        onPreviousWeek = { weekOffset-- },
                        onNextWeek = { weekOffset++ },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                when {
                    uiState.isLoading -> item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                    uiState.error != null -> item { Text(uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
                    filteredList.isEmpty() && !uiState.isLoading -> item { EmptyState(modifier = Modifier.padding(top = 60.dp)) }
                    else -> {
                        filteredList.entries.forEach { (category, items) ->
                            stickyHeader {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            items(items, key = { it.id }) { item ->
                                ShoppingListItemRow(
                                    item = item,
                                    onCheck = { viewModel.toggleItemChecked(item.id) },
                                    onEdit = { selectedItem ->
                                        Log.d("ShoppingListScreen", "🔵 EDIT_ITEM_ROW: itemId=${selectedItem.id}, name='${selectedItem.name}'")
                                        itemToEdit = selectedItem
                                        showEditItemDialog = true
                                    },
                                    onDelete = { selectedItem ->
                                        Log.d("ShoppingListScreen", "🔴 DELETE_ITEM_ROW: itemId=${selectedItem.id}, name='${selectedItem.name}'")
                                        viewModel.deleteItem(selectedItem.id)
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        val selectedIndex = 2
        FloatingBottomNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                val destination = navItems[index].route
                if (navController.currentDestination?.route != destination) {
                    navController.navigate(destination) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Botón de eliminar todos los marcados (solo aparece si hay items seleccionados)
                if (checkedItems.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = { viewModel.clearCheckedItems() },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Eliminar todos los marcados"
                        )
                    }
                }


                // Botón de agregar (SIEMPRE visible)
                FloatingActionButton(
                    onClick = { showAddItemDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir Ítem")
                }
            }
        }
    }

    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onConfirm = { name, quantity ->
                viewModel.addItem(name, quantity)
                showAddItemDialog = false
            }
        )
    }

    if (showEditItemDialog && itemToEdit != null) {
        val item = itemToEdit!!
        EditItemDialog(
            item = item,
            onDismiss = {
                showEditItemDialog = false
                itemToEdit = null
            },
            onConfirm = { name, quantity ->
                viewModel.editItem(item.id, name, quantity)
                showEditItemDialog = false
                itemToEdit = null
            }
        )
    }
}

/**
 * Composable que muestra la cabecera de la lista de compras.
 * Incluye una barra de progreso que visualiza la proporción de elementos marcados,
 * así como un campo de búsqueda para filtrar elementos por nombre.
 *
 * @param searchQuery Texto actual en el campo de búsqueda
 * @param onSearchQueryChange Callback invocado cuando cambia el texto de búsqueda
 * @param checkedItems Número de elementos marcados como completados
 * @param totalItems Número total de elementos en la lista
 * @param progress Proporción de elementos completados (entre 0 y 1)
 */
@Composable
private fun ShoppingListHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    checkedItems: Int,
    totalItems: Int,
    progress: Float
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnimation")

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (totalItems > 0) {
            Column {
                Text(
                    text = "Progreso: $checkedItems de $totalItems",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar ingrediente...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}

/**
 * Composable que representa un elemento individual de la lista de compras.
 * Muestra el nombre del elemento, cantidad, checkbox para marcarlo como completado,
 * y botones para editar y eliminar. Aplica estilos visuales diferentes según
 * si el elemento está marcado como completado o no.
 *
 * @param item Modelo de datos del elemento de compra a mostrar
 * @param onCheck Callback invocado cuando se marca/desmarca el elemento
 * @param onEdit Callback invocado cuando se solicita editar el elemento
 * @param onDelete Callback invocado cuando se solicita eliminar el elemento
 * @param modifier Modificador opcional para personalizar el layout
 */
@Composable
private fun ShoppingListItemRow(
    item: ShoppingItem,
    onCheck: () -> Unit,
    onEdit: (ShoppingItem) -> Unit = {},
    onDelete: (ShoppingItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
    val textColor = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isChecked) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onCheck() }
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onCheck)
            ) {
                Text(
                    text = item.name,
                    textDecoration = textDecoration,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (item.quantity.isNotBlank()) {
                    Text(
                        text = item.quantity,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }

            // Botones de acción directos en cada fila
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Editar
                Surface(
                    onClick = {
                        Log.d("ShoppingListScreen", "🔵 EDIT_ITEM_ROW: itemId=${item.id}, name='${item.name}'")
                        onEdit(item)
                    },
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Botón Eliminar
                Surface(
                    onClick = {
                        Log.d("ShoppingListScreen", "🔴 DELETE_ITEM_ROW: itemId=${item.id}, name='${item.name}'")
                        onDelete(item)
                    },
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable que muestra un diálogo para añadir un nuevo elemento a la lista de compras.
 * Contiene campos para el nombre y la cantidad, con validación básica de que el nombre
 * no puede estar vacío.
 *
 * @param onDismiss Callback invocado cuando se cierra el diálogo sin confirmar
 * @param onConfirm Callback invocado con el nombre y cantidad cuando se confirma la adición
 */
@Composable
private fun AddItemDialog(onDismiss: () -> Unit, onConfirm: (name: String, quantity: String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Ítem Manualmente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del ítem") })
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Cantidad (ej: 2 tazas)") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(name, quantity) }, enabled = name.isNotBlank()) { Text("Añadir") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

/**
 * Composable que muestra un diálogo para editar un elemento existente de la lista de compras.
 * Muestra los valores actuales del elemento y permite modificarlos, con validación
 * básica de que el nombre no puede estar vacío.
 *
 * @param item Elemento de la lista de compras a editar
 * @param onDismiss Callback invocado cuando se cierra el diálogo sin confirmar
 * @param onConfirm Callback invocado con el nuevo nombre y cantidad cuando se confirma la edición
 */
@Composable
private fun EditItemDialog(item: ShoppingItem, onDismiss: () -> Unit, onConfirm: (name: String, quantity: String) -> Unit) {
    var name by remember { mutableStateOf(item.name) }
    var quantity by remember { mutableStateOf(item.quantity) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Ítem") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del ítem") })
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Cantidad (ej: 2 tazas)") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(name, quantity) }, enabled = name.isNotBlank()) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

/**
 * Composable que muestra un estado visual cuando la lista de compras está vacía.
 * Incluye un icono, mensaje principal y texto explicativo con sugerencias para el usuario.
 *
 * @param modifier Modificador opcional para personalizar el layout
 */
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Tu lista está vacía", style = MaterialTheme.typography.headlineSmall)
        Text("Genera una lista desde tu plan o añade ítems manualmente.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

/**
 * Función de utilidad que calcula el rango de fechas de la semana a mostrar,
 * basado en el desplazamiento desde la semana actual.
 *
 * @param weekOffset Número de semanas de desplazamiento desde la semana actual
 * @return Cadena de texto formateada con el rango de fechas (ej. "1 Ago - 7 Ago")
 */
private fun getWeekDateRange(weekOffset: Int): String {
    val weekFormat = SimpleDateFormat("d MMM", Locale("es", "ES"))
    val calendar = Calendar.getInstance().apply {
        add(Calendar.WEEK_OF_YEAR, weekOffset)
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }
    val startOfWeek = calendar.time
    calendar.add(Calendar.DAY_OF_YEAR, 6)
    val endOfWeek = calendar.time
    return "${weekFormat.format(startOfWeek)} - ${weekFormat.format(endOfWeek)}"
}
