/* --------------------------------------------------------------------
 * Archivo: PrivacyPolicyScreen.kt (REDiseñado)
 * Descripción: Se mejora la UI con un diseño más limpio, organizado
 * en una tarjeta y visualmente más atractivo.
 * --------------------------------------------------------------------
 */
package com.noskill.anymeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Política de Privacidad") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Policy,
                contentDescription = "Política de Privacidad",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(20.dp)
            )

            Text(
                text = "Última actualización: 17 de julio de 2025",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PrivacyPolicySection(
                        title = "1. Introducción",
                        content = "Bienvenido a AnyMeal. Nos comprometemos a proteger su privacidad. Esta Política de Privacidad explica cómo recopilamos, usamos, divulgamos y protegemos su información cuando utiliza nuestra aplicación móvil."
                    )
                    PrivacyPolicySection(
                        title = "2. Información que Recopilamos",
                        content = "Podemos recopilar información personal que usted nos proporciona directamente, como su nombre y correo electrónico. También podemos recopilar datos de uso de forma automática, como el tipo de dispositivo y la forma en que interactúa con la aplicación."
                    )
                    PrivacyPolicySection(
                        title = "3. Uso de su Información",
                        content = "Utilizamos la información recopilada para operar y mantener la aplicación, personalizar su experiencia, mejorar nuestros servicios y comunicarnos con usted."
                    )
                    PrivacyPolicySection(
                        title = "4. Compartir su Información",
                        content = "No vendemos, comercializamos ni alquilamos su información personal a terceros. Podemos compartir información con proveedores de servicios de confianza que nos ayudan en la operación de nuestra aplicación."
                    )
                    PrivacyPolicySection(
                        title = "5. Seguridad de Datos",
                        content = "Implementamos medidas de seguridad razonables para proteger su información. Sin embargo, ninguna transmisión de datos por Internet es 100% segura."
                    )
                    PrivacyPolicySection(
                        title = "6. Sus Derechos",
                        content = "Usted tiene derecho a acceder, corregir o solicitar la eliminación de su información personal. Para ejercer estos derechos, contáctenos a través de los canales proporcionados."
                    )
                    PrivacyPolicySection(
                        title = "7. Cambios a esta Política",
                        content = "Nos reservamos el derecho de actualizar esta Política de Privacidad en cualquier momento. Le notificaremos sobre cualquier cambio publicando la nueva política en esta página."
                    )
                    PrivacyPolicySection(
                        title = "8. Contacto",
                        content = "Si tiene alguna pregunta sobre esta Política de Privacidad, puede contactarnos a través de kenet.garcia@email.com."
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacyPolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
