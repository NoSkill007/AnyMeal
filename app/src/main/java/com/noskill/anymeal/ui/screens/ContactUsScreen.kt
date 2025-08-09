/**
 * ContactUsScreen.kt
 *
 * Propósito: Define la pantalla de contacto de la aplicación AnyMeal.
 * Presenta la información de contacto de manera organizada y atractiva,
 * incluyendo correo electrónico, teléfono, dirección física y redes sociales.
 * Implementa una interfaz visual mejorada con tarjetas y elementos interactivos.
 */
package com.noskill.anymeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
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
 * Composable principal que define la pantalla de contacto.
 * Muestra múltiples opciones de contacto organizadas en tarjetas separadas
 * por categorías, con un encabezado visual destacado y una barra de navegación superior.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            // Barra superior con título y botón de navegación para volver
            TopAppBar(
                title = { Text("Contáctanos") },
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
            // Icono principal destacado with fondo circular
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "Contacto",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(20.dp)
            )

            // Texto introductorio para la sección de contacto
            Text(
                text = "Estamos aquí para ayudarte. No dudes en contactarnos a través de los siguientes medios:",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Tarjeta con información de contacto directo (correo, teléfono, dirección)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column {
                    // Elemento para contacto por correo electrónico
                    ContactInfoItem(
                        icon = Icons.Outlined.Email,
                        title = "Correo Electrónico",
                        content = "soporte@anymeal.com",
                        onClick = { /* TODO: Abrir cliente de correo */ }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Elemento para contacto telefónico
                    ContactInfoItem(
                        icon = Icons.Outlined.Phone,
                        title = "Teléfono",
                        content = "+507 6123-4567",
                        onClick = { /* TODO: Abrir marcador de teléfono */ }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Elemento para dirección física
                    ContactInfoItem(
                        icon = Icons.Outlined.LocationOn,
                        title = "Dirección",
                        content = "Calle Ficticia 123, Ciudad de Panamá",
                        onClick = { /* TODO: Abrir mapa */ }
                    )
                }
            }

            // Tarjeta con información de redes sociales
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column {
                    // Elemento para Facebook
                    ContactInfoItem(
                        icon = Icons.Outlined.Link,
                        title = "Facebook",
                        content = "@AnyMealApp",
                        onClick = { /* TODO: Abrir enlace de Facebook */ }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    // Elemento para Instagram
                    ContactInfoItem(
                        icon = Icons.Outlined.Link,
                        title = "Instagram",
                        content = "@anymeal_oficial",
                        onClick = { /* TODO: Abrir enlace de Instagram */ }
                    )
                }
            }
        }
    }
}

/**
 * Composable que representa un elemento individual de información de contacto.
 * Muestra un icono, título y contenido en una fila, con la capacidad de ser clickeable
 * para realizar acciones como abrir enlaces, correo o teléfono.
 *
 * @param icon Icono vectorial que representa visualmente el tipo de contacto
 * @param title Título descriptivo del medio de contacto
 * @param content Información específica de contacto (email, teléfono, etc.)
 * @param onClick Función lambda opcional que se ejecuta al hacer clic en el elemento
 */
@Composable
fun ContactInfoItem(icon: ImageVector, title: String, content: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono representativo del tipo de contacto
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        // Columna con título y contenido del contacto
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
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
