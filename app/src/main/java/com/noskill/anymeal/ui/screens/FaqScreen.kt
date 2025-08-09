/**
 * FaqScreen.kt
 *
 * Propósito: Define la pantalla de preguntas frecuentes (FAQ) de la aplicación AnyMeal.
 * Presenta una lista de preguntas comunes con sus respuestas en un formato de tarjetas
 * expandibles, facilitando a los usuarios encontrar información sobre el uso de la aplicación.
 * La implementación utiliza animaciones para mejorar la experiencia de usuario al expandir
 * y contraer las respuestas.
 */
package com.noskill.anymeal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Composable principal que define la pantalla de preguntas frecuentes.
 * Muestra una lista de preguntas comunes organizadas en tarjetas expandibles,
 * con un encabezado visual destacado y una barra de navegación superior.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavController) {
    // Lista de pares de preguntas y respuestas frecuentes
    val faqList = listOf(
        "¿Cómo puedo guardar una receta como favorita?" to "Para guardar una receta, simplemente navegue a la vista detallada de la receta y toque el icono del corazón. Se volverá sólido para indicar que la receta ha sido guardada.",
        "¿Puedo crear planes de comidas personalizados?" to "Sí, nuestra aplicación le permite crear planes de comidas personalizados para cualquier día de la semana. Vaya a la sección 'Plan' y añada recetas a los diferentes tiempos de comida.",
        "¿Cómo cambio mi información de perfil?" to "Puede editar su nombre y dirección de correo electrónico y cambiar su contraseña yendo a la pantalla 'Perfil' y tocando el icono de edición junto a su nombre.",
        "¿Qué hago si olvido mi contraseña?" to "Si olvida su contraseña, puede restablecerla desde la pantalla de inicio de sesión. Toque '¿Olvidó su contraseña?' y siga las instrucciones para crear una nueva.",
        "¿La aplicación funciona sin conexión a internet?" to "Algunas funciones básicas, como ver recetas guardadas previamente, pueden funcionar sin conexión. Sin embargo, para buscar nuevas recetas o sincronizar datos, necesitará una conexión a internet activa.",
        "¿Cómo puedo contactar con soporte?" to "Si tiene más preguntas, puede contactar con nuestro equipo de soporte a través de la sección 'Ayuda y Soporte' en la pantalla de perfil."
    )

    // Estructura principal de la pantalla con barra superior
    Scaffold(
        topBar = {
            // Barra superior con título y botón de navegación para volver
            TopAppBar(
                title = { Text("Preguntas Frecuentes") },
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
        // Lista con desplazamiento vertical optimizado para rendimiento
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre tarjetas
        ) {
            item {
                // Encabezado visual de la pantalla con icono y fecha de actualización
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    // Icono destacado con fondo circular
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "Preguntas Frecuentes",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(20.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Fecha de última actualización de las FAQs
                    Text(
                        text = "Última actualización: 17 de julio de 2025",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Genera una tarjeta expandible para cada pregunta y respuesta
            items(faqList) { faq ->
                FaqCard(question = faq.first, answer = faq.second)
            }
        }
    }
}

/**
 * Composable que representa una tarjeta individual de pregunta y respuesta.
 * La tarjeta es interactiva y permite expandir/contraer la respuesta al hacer clic.
 * Implementa animaciones suaves para mejorar la experiencia del usuario.
 *
 * @param question Texto de la pregunta a mostrar
 * @param answer Texto de la respuesta asociada a la pregunta
 */
@Composable
fun FaqCard(question: String, answer: String) {
    // Estado para controlar si la tarjeta está expandida o contraída
    var isExpanded by remember { mutableStateOf(false) }

    // Tarjeta interactiva que contiene la pregunta y respuesta
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Fila superior con la pregunta y el icono de expansión
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Texto de la pregunta
                Text(
                    text = question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                // Icono que cambia según el estado de expansión
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Ocultar respuesta" else "Mostrar respuesta",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Contenido expandible con la respuesta
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column {
                    // Separador visual entre pregunta y respuesta
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    // Texto de la respuesta
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
