/* --------------------------------------------------------------------
 * Archivo: UIData.kt
 * Propósito: Define listas y configuraciones para la interfaz de usuario (UI),
 *            incluyendo la barra de navegación y las categorías disponibles.
 *            Facilita la gestión centralizada de los elementos visuales
 *            reutilizables en la aplicación.
 * --------------------------------------------------------------------
 */
package com.noskill.anymeal.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.ShoppingCart
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.models.CategoryUi
import com.noskill.anymeal.ui.models.NavItem

// Lista para la barra de navegación con iconos actualizados.
// Cada elemento NavItem representa una sección principal de la app.
val navItems = listOf(
    NavItem(
        label = "Inicio", // Etiqueta para la pantalla principal
        icon = Icons.Outlined.Home, // Icono de inicio
        route = Screen.Home.route // Ruta de navegación
    ),
    NavItem(
        label = "Plan", // Etiqueta para la pantalla de planificación semanal
        icon = Icons.Outlined.ListAlt, // Icono de lista
        route = Screen.Plan.route // Ruta de navegación
    ),
    NavItem(
        label = "Lista", // Etiqueta para la pantalla de lista de compras
        icon = Icons.Outlined.ShoppingCart, // Icono de carrito
        route = Screen.ShoppingList.route // Ruta de navegación
    ),
    NavItem(
        label = "Perfil", // Etiqueta para la pantalla de perfil de usuario
        icon = Icons.Outlined.AccountCircle, // Icono de usuario
        route = Screen.Profile.route // Ruta de navegación
    ),
)

// Lista para las categorías de recetas.
// Cada elemento CategoryUi representa un tipo de comida con su icono.
val categories = listOf(
    CategoryUi("Desayuno", Icons.Filled.FreeBreakfast), // Categoría de desayuno
    CategoryUi("Almuerzo", Icons.Filled.LunchDining),   // Categoría de almuerzo
    CategoryUi("Cena", Icons.Filled.DinnerDining),      // Categoría de cena
    CategoryUi("Snacks", Icons.Filled.Fastfood)         // Categoría de snacks
)
