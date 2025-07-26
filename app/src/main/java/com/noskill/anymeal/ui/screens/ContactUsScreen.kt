/* --------------------------------------------------------------------
 * Archivo: ContactUsScreen.kt (REDiseñado)
 * Descripción: Se mejora la UI con un diseño más limpio, organizado
 * en tarjetas y visualmente más atractivo.
 * --------------------------------------------------------------------
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Elemento visual principal
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

            Text(
                text = "Estamos aquí para ayudarte. No dudes en contactarnos a través de los siguientes medios:",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Tarjeta para contacto directo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column {
                    ContactInfoItem(
                        icon = Icons.Outlined.Email,
                        title = "Correo Electrónico",
                        content = "soporte@anymeal.com",
                        onClick = { /* TODO: Abrir cliente de correo */ }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    ContactInfoItem(
                        icon = Icons.Outlined.Phone,
                        title = "Teléfono",
                        content = "+507 6123-4567",
                        onClick = { /* TODO: Abrir marcador de teléfono */ }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    ContactInfoItem(
                        icon = Icons.Outlined.LocationOn,
                        title = "Dirección",
                        content = "Calle Ficticia 123, Ciudad de Panamá",
                        onClick = { /* TODO: Abrir mapa */ }
                    )
                }
            }

            // Tarjeta para redes sociales
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column {
                    ContactInfoItem(
                        icon = Icons.Outlined.Link,
                        title = "Facebook",
                        content = "@AnyMealApp",
                        onClick = { /* TODO: Abrir enlace de Facebook */ }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
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

@Composable
fun ContactInfoItem(icon: ImageVector, title: String, content: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
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
