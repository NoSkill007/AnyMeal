package com.noskill.anymeal.ui.components

import android.util.Log // Importar Log para depuración
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.models.PlanEntry
import com.noskill.anymeal.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableMealSection(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    entries: List<PlanEntry>,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    onAddClick: () -> Unit,
    onRecipeClick: (Int) -> Unit,
    onDeleteEntry: (Long) -> Unit, // Acepta el ID del backend (Long)
    favoritesViewModel: FavoritesViewModel
) {
    val favoriteIds by favoritesViewModel.favoriteRecipeIds.collectAsState()

    // --- LOGGING PARA DEPURACIÓN EN ExpandableMealSection ---
    Log.d("ExpandableMealSection", "Recomposing: $title, isExpanded: $isExpanded, Entries count: ${entries.size}")
    entries.forEach { entry ->
        Log.d("ExpandableMealSection", "  Entry in list for $title: ${entry.recipe.title} (ID: ${entry.backendId})")
    }
    // --- FIN LOGGING ---

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ListItem(
                modifier = Modifier.clickable { onHeaderClick() },
                headlineContent = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                supportingContent = {
                    Text(text = if (entries.isEmpty()) "Sin recetas" else "${entries.size} receta(s)")
                },
                leadingContent = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                CircleShape
                            )
                            .padding(8.dp)
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Contraer" else "Expandir"
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    if (entries.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Añade tu primera receta a esta sección.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            entries.forEachIndexed { index, entry ->
                                key("${entry.backendId}-$index") {
                                    RecipeItem(
                                        entry = entry,
                                        onClick = onRecipeClick,
                                        onDelete = { onDeleteEntry(entry.backendId) },
                                        isFavorite = entry.recipe.id in favoriteIds,
                                        onFavoriteClick = { favoritesViewModel.toggleFavorite(entry.recipe.id) }
                                    )
                                }
                            }
                        }
                    }


                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Añadir receta")
                    }
                }
            }
        }
    }
}
