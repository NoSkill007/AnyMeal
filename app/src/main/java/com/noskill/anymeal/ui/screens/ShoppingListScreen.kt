// --- PASO 5: Pantalla (Screen) SIN HILT ---
// Archivo: ui/screens/ShoppingListScreen.kt
package com.noskill.anymeal.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(
    navController: NavController,
    viewModel: ShoppingListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var weekOffset by remember { mutableStateOf(0) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(weekOffset) {
        viewModel.generateListForWeek(weekOffset)
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
        Scaffold { innerPadding ->
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
            AnimatedContent(
                targetState = checkedItems.isNotEmpty(),
                transitionSpec = { scaleIn() togetherWith scaleOut() },
                label = "fabAnimation"
            ) { hasCheckedItems ->
                if (hasCheckedItems) {
                    FloatingActionButton(
                        onClick = { viewModel.clearCheckedItems() },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Limpiar Ítems Comprados")
                    }
                } else {
                    FloatingActionButton(onClick = { showAddItemDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir Ítem")
                    }
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
}

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

@Composable
private fun ShoppingListItemRow(item: ShoppingItem, onCheck: () -> Unit, modifier: Modifier = Modifier) {
    val textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
    val textColor = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onCheck)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.isChecked, onCheckedChange = { onCheck() })
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, textDecoration = textDecoration, color = textColor)
            if (item.quantity.isNotBlank()) {
                Text(text = item.quantity, style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.7f))
            }
        }
    }
}

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