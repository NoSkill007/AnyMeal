/**
 * AppVersionScreen.kt
 *
 * Propósito: Define la pantalla "Sobre la App" que muestra información sobre la versión,
 * desarrolladores y detalles curiosos de la aplicación AnyMeal. Presenta una interfaz
 * atractiva y organizada en tarjetas con un diseño limpio y moderno, facilitando al usuario
 * conocer los datos relevantes sobre el desarrollo y la versión actual de la aplicación.
 */
package com.noskill.anymeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Composable principal que define la pantalla "Sobre la App".
 * Muestra información sobre la versión de la aplicación, los desarrolladores,
 * inspiración del proyecto y detalles curiosos, todo organizado en una interfaz
 * visualmente atractiva con tarjetas y secciones bien definidas.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppVersionScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            // Barra superior con título y botón para volver atrás
            TopAppBar(
                title = { Text("Sobre la App") },
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
        // Contenido principal con scroll vertical
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Ícono principal de la pantalla (círculo con icono de información)
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Información",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(20.dp)
            )

            // Sección de título y versión de la aplicación
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AnyMeal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Versión 1.0.0",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Tarjeta con detalles sobre desarrolladores, inspiración y errores conocidos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    // Detalle sobre los desarrolladores
                    AppVersionDetail(
                        icon = Icons.Default.Code,
                        title = "Código por:",
                        content = "Un desarrollador que vive a base de café y sueños de código limpio. (¡Saludos a Kenet!)"
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Detalle sobre la inspiración del proyecto
                    AppVersionDetail(
                        icon = Icons.Default.EmojiObjects,
                        title = "Inspiración:",
                        content = "Las ganas de comer rico sin pensar demasiado y evitar el '¿qué comemos hoy?' eterno."
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Detalle sobre errores conocidos (con toque de humor)
                    AppVersionDetail(
                        icon = Icons.Default.BugReport,
                        title = "Errores Conocidos:",
                        content = "A veces, la app sugiere ensalada cuando quieres pizza. Estamos mejorando la telepatía."
                    )
                }
            }

            // Tarjeta con dato curioso sobre el desarrollo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dato Curioso del Desarrollo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "La primera versión de esta app solo tenía recetas de tostadas. ¡Hemos avanzado mucho!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mensaje de agradecimiento al final
            Text(
                text = "¡Gracias por ser parte de esta aventura!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Composable que muestra un detalle individual en la pantalla de información.
 * Cada detalle consiste en un icono, un título y un contenido descriptivo,
 * organizados en una fila con formato consistente.
 *
 * @param icon Icono vectorial que representa visualmente el tipo de información
 * @param title Título descriptivo corto para la sección de información
 * @param content Texto descriptivo detallado con la información principal
 */
@Composable
fun AppVersionDetail(icon: ImageVector, title: String, content: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono representativo del tipo de detalle
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        // Columna con título y contenido del detalle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
