/* --------------------------------------------------------------------
 * Archivo: AchievementsScreen.kt (REDiseñado)
 * Descripción: Se implementa una TopAppBar para una navegación
 * consistente, siguiendo el estilo de EditProfileScreen.
 * --------------------------------------------------------------------
 */
@file:OptIn(ExperimentalMaterial3Api::class)
package com.noskill.anymeal.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.noskill.anymeal.ui.models.Achievement
import com.noskill.anymeal.viewmodel.AchievementsViewModel
import com.noskill.anymeal.viewmodel.AchievementsViewModelFactory
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel

@Composable
fun AchievementsScreen(
    navController: NavController,
    plannerViewModel: PlannerViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    val factory = AchievementsViewModelFactory(plannerViewModel, favoritesViewModel)
    val achievementsViewModel: AchievementsViewModel = viewModel(factory = factory)

    val achievements by achievementsViewModel.achievements.collectAsState()
    val unlockedCount = achievements.count { it.isUnlocked }

    // CAMBIO: Se utiliza un Scaffold con una TopAppBar estándar.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus Hitos", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // El resumen ahora es el primer elemento de la lista.
                Column {
                    Text(
                        "¡Sigue así!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Has desbloqueado $unlockedCount de ${achievements.size} logros.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(achievements) { achievement ->
                AchievementItem(achievement = achievement)
            }
        }
    }
}

@Composable
private fun AchievementItem(achievement: Achievement) {
    val alpha by animateFloatAsState(targetValue = if (achievement.isUnlocked) 1f else 0.5f, label = "alphaAnim")
    val iconColor by animateColorAsState(targetValue = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, label = "iconColorAnim")

    Card(
        modifier = Modifier.fillMaxWidth().alpha(alpha),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
