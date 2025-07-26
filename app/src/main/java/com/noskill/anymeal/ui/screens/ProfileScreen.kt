package com.noskill.anymeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.noskill.anymeal.data.navItems
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.components.FloatingBottomNavBar
import com.noskill.anymeal.util.Result // Aún se usa si planResult es Result
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel
import com.noskill.anymeal.viewmodel.ProfileViewModel // <-- USAR ESTE ViewModel
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogoutConfirmed: () -> Unit,
    // userViewModel: UserViewModel = viewModel(), // <-- ELIMINAR ESTO
    plannerViewModel: PlannerViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel() // <-- USAR ESTE ViewModel para el perfil
) {
    val openDialog = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedIndex = navItems.indexOfFirst { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }.coerceAtLeast(0)

    // Se obtienen los estados del ProfileViewModel
    val profileUiState by profileViewModel.uiState.collectAsState() // <-- AHORA DESDE PROFILEVIEWMODEL
    val user = profileUiState.user // <-- ACCEDER AL USUARIO DESDE PROFILEUISTATE

    // Se obtienen los estados del PlannerViewModel
    val planResult by plannerViewModel.planState.collectAsState()

    // CRUCIAL: Cargar el perfil cada vez que la pantalla se vuelve activa
    // Esto asegura que si EditProfileScreen actualizó datos, ProfileScreen los obtenga.
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    // Se extrae el nombre y el correo del estado del usuario
    val userName = user?.username ?: "Cargando..."
    val userEmail = user?.email ?: ""

    // Se calculan las estadísticas del plan solo si los datos se han cargado correctamente
    val (totalPlannedDays, totalRecipesAdded) = when (val state = planResult) {
        is Result.Success -> {
            val planData = state.data
            val days = planData.size
            val recipes = planData.values.sumOf { dailyPlan ->
                dailyPlan.meals.values.sumOf { it.size }
            }
            Pair(days, recipes)
        }
        else -> Pair(0, 0) // Valores por defecto para los estados de carga o error
    }

    val totalFavorites by favoritesViewModel.favoriteRecipeIds.map { it.size }.collectAsState(initial = 0)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(bottom = 80.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProfileHeader(
                        name = userName,
                        email = userEmail,
                        onEditClick = {
                            navController.navigate(Screen.EditProfile.route)
                        }
                    )

                    AchievementsSummaryCard(
                        navController = navController,
                        totalPlannedDays = totalPlannedDays,
                        totalRecipesAdded = totalRecipesAdded,
                        totalFavorites = totalFavorites
                    )

                    MenuSection(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = onThemeChange
                    )

                    LogoutButton(onClick = { openDialog.value = true })
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

    if (openDialog.value) {
        LogoutConfirmationDialog(
            onDismiss = { openDialog.value = false },
            onConfirm = {
                openDialog.value = false
                onLogoutConfirmed()
            }
        )
    }
}

// --- El resto de los componentes privados de la pantalla se mantienen igual ---

@Composable
private fun ProfileHeader(name: String, email: String, onEditClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto de Perfil",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .padding(12.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Perfil", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun AchievementsSummaryCard(
    navController: NavController,
    totalPlannedDays: Int,
    totalRecipesAdded: Int,
    totalFavorites: Int
) {
    Column {
        Text("Tus Hitos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Card(
            onClick = { navController.navigate(Screen.Achievements.route) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Resumen de Progreso", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Toca para ver todos tus logros", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("• $totalPlannedDays Días Planificados", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("• $totalRecipesAdded Recetas Añadidas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("• $totalFavorites Recetas Favoritas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun MenuSection(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column {
            MenuItem(
                title = "Mis Recetas Favoritas",
                icon = Icons.Outlined.FavoriteBorder,
                onClick = { navController.navigate(Screen.Favorites.route) }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            MenuItem(
                title = "Tema Oscuro",
                icon = Icons.Outlined.DarkMode
            ) {
                Switch(checked = isDarkTheme, onCheckedChange = onThemeChange)
            }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            MenuItem(
                title = "Ayuda y Soporte",
                icon = Icons.Outlined.HelpOutline,
                onClick = { navController.navigate(Screen.Faq.route) }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            MenuItem(
                title = "Información de la App",
                icon = Icons.Outlined.Info,
                onClick = { navController.navigate(Screen.AppVersion.route) }
            )
        }
    }
}

@Composable
private fun MenuItem(
    title: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        if (trailingContent != null) {
            trailingContent()
        } else if (onClick != null) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí, salir", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("¿Cerrar sesión?") },
        text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
        shape = RoundedCornerShape(20.dp),
    )
}