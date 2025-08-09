/**
 * LoginScreen.kt
 *
 * Propósito: Define la pantalla de inicio de sesión de la aplicación AnyMeal.
 * Permite a los usuarios autenticarse mediante correo electrónico/nombre de usuario
 * y contraseña, o a través de opciones de login social. Gestiona la validación
 * de entradas, estados de carga y manejo de errores de autenticación.
 */
package com.noskill.anymeal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.noskill.anymeal.R
import com.noskill.anymeal.dto.LoginRequest
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.components.AuthInputField
import com.noskill.anymeal.ui.components.BrandTitle
import com.noskill.anymeal.ui.components.PrimaryButton
import com.noskill.anymeal.ui.components.SocialLoginRow
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.util.isValidEmail
import com.noskill.anymeal.viewmodel.AuthViewModel

/**
 * Composable principal que define la pantalla de inicio de sesión.
 * Presenta un formulario para ingresar credenciales, muestra errores de validación
 * y de API, gestiona el estado de la autenticación y proporciona navegación para
 * registro de nuevos usuarios.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param onRegisterClick Callback que se invoca cuando el usuario desea registrarse
 * @param authViewModel ViewModel que maneja la lógica de autenticación
 */
@Composable
fun LoginScreen(
    navController: NavController,
    onRegisterClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    // Estados para los campos del formulario y mensajes de error
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var apiError by remember { mutableStateOf<String?>(null) }

    // Estado observable de autenticación del ViewModel
    val authState by authViewModel.authState.collectAsState()

    // Efecto para manejar cambios en el estado de autenticación
    LaunchedEffect(authState) {
        when (val state = authState) {
            // En caso de éxito, navega a la pantalla principal
            is Result.Success -> {
                // Navega a Home si el token no está vacío (es decir, el login fue exitoso)
                if (state.data.token.isNotBlank()) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            }
            // En caso de error, muestra el mensaje de la API
            is Result.Error -> {
                apiError = state.message
            }
            // En otros estados (Loading o inicial), limpia el error
            else -> {
                apiError = null
            }
        }
    }

    // Contenedor principal de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Columna principal con scroll vertical para adaptarse a pantallas pequeñas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Sección de logo y título de la aplicación
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo con efecto de fondo circular radial
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.0f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Logo AnyMeal",
                        modifier = Modifier.size(60.dp)
                    )
                }
                BrandTitle()
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Formulario de inicio de sesión
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Campo de entrada para email o nombre de usuario
                AuthInputField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                        apiError = null
                    },
                    label = "Correo electrónico o Usuario",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    error = emailError
                )

                // Campo de entrada para contraseña con opción para mostrar/ocultar
                AuthInputField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                        apiError = null
                    },
                    label = "Contraseña",
                    leadingIcon = Icons.Default.Lock,
                    isPasswordToggleEnabled = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    error = passwordError
                )

                // Mensaje de error de API que aparece/desaparece animadamente
                AnimatedVisibility(visible = apiError != null) {
                    Text(
                        text = apiError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Botón de inicio de sesión con estado de carga
                PrimaryButton(
                    text = "Ingresar",
                    isLoading = authState is Result.Loading,
                    onClick = {
                        var valid = true
                        // Validación de campos antes de enviar
                        emailError = if (email.isBlank()) {
                            valid = false; "Este campo es obligatorio"
                        } else null
                        passwordError = if (password.isBlank()) {
                            valid = false; "La contraseña es obligatoria"
                        } else null

                        // Si la validación es exitosa, intenta iniciar sesión
                        if (valid) {
                            authViewModel.login(LoginRequest(username = email, password = password))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección para opciones alternativas de inicio de sesión (social)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Divisor visual con texto
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Text("O ingresa con", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                }
                // Fila de botones para inicio de sesión social
                SocialLoginRow(
                    onGoogleClick = { /* TODO: Implementar login con Google */ },
                    onFacebookClick = { /* TODO: Implementar login con Facebook */ },
                    onAppleClick = { /* TODO: Implementar login con Apple */ }
                )
            }

            // Espaciador flexible que empuja el contenido de abajo hacia la parte inferior
            Spacer(modifier = Modifier.weight(1f))

            // Enlace para navegación a la pantalla de registro
            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿No tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onRegisterClick) {
                    Text(
                        "Regístrate aquí",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}