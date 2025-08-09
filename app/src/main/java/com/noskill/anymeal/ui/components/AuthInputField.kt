/**
 * AuthInputField.kt
 *
 * Este archivo define un componente Composable personalizado para campos de entrada
 * utilizados en pantallas de autenticación (como inicio de sesión y registro).
 * Proporciona una interfaz uniforme con soporte para campos de texto normales y contraseñas,
 * incluyendo la funcionalidad de mostrar/ocultar contraseñas.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Componente de campo de entrada personalizado para pantallas de autenticación.
 *
 * @param value Texto actual del campo de entrada
 * @param onValueChange Callback invocado cuando el usuario modifica el texto
 * @param label Etiqueta descriptiva que se muestra en el campo
 * @param leadingIcon Icono que se muestra al inicio del campo
 * @param modifier Modificador opcional para personalizar el diseño
 * @param isPasswordToggleEnabled Si es true, muestra un botón para alternar visibilidad de contraseña
 * @param keyboardOptions Opciones para configurar el teclado (tipo, capitalización, etc.)
 * @param keyboardActions Acciones personalizadas para eventos del teclado
 * @param error Mensaje de error a mostrar (null si no hay error)
 */
@Composable
fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPasswordToggleEnabled: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    error: String? = null
) {
    // Estado para controlar la visibilidad de la contraseña
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            // Configura el icono principal a la izquierda del campo
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null // No necesita descripción para accesibilidad al ser decorativo
                )
            },
            // Configura el icono de toggle de visibilidad de contraseña si está habilitado
            trailingIcon = {
                if (isPasswordToggleEnabled) {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible)
                                Icons.Default.Visibility // Icono de ojo abierto cuando la contraseña es visible
                            else
                                Icons.Default.VisibilityOff, // Icono de ojo tachado cuando la contraseña está oculta
                            contentDescription = "Toggle password visibility"
                        )
                    }
                }
            },
            // Aplica transformación visual para ocultar la contraseña cuando corresponde
            visualTransformation = if (isPasswordToggleEnabled && !isPasswordVisible)
                PasswordVisualTransformation() // Oculta el texto con asteriscos o puntos
            else
                VisualTransformation.None, // Muestra el texto normal
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true, // Restringe el campo a una sola línea
            isError = error != null, // Activa el estado de error cuando hay un mensaje de error
            // Personalización de colores para diferentes estados del campo
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorContainerColor = Color.Transparent
            )
        )
        // Muestra mensaje de error debajo del campo si existe
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
