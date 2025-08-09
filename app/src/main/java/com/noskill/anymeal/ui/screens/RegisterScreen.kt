/**
 * RegisterScreen.kt
 *
 * Propósito: Define la pantalla de registro de usuarios de la aplicación AnyMeal.
 * Permite a nuevos usuarios crear una cuenta proporcionando nombre de usuario,
 * correo electrónico y contraseña. Implementa validación de datos en tiempo real,
 * manejo de errores y comunicación con el backend a través del AuthViewModel.
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
import androidx.compose.material.icons.filled.Person
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
import com.noskill.anymeal.dto.RegisterRequest
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.components.AuthInputField
import com.noskill.anymeal.ui.components.BrandTitle
import com.noskill.anymeal.ui.components.PrimaryButton
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.util.isValidEmail
import com.noskill.anymeal.viewmodel.AuthViewModel

/**
 * Composable principal que define la pantalla de registro de usuarios.
 * Presenta un formulario para crear una nueva cuenta, valida los datos ingresados,
 * muestra errores de validación y API, y navega a Home tras un registro exitoso.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param onLoginClick Callback que se invoca cuando el usuario desea ir a la pantalla de inicio de sesión
 * @param authViewModel ViewModel que maneja la lógica de autenticación
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    onLoginClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    // Estados para los campos del formulario
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados para los mensajes de error de validación
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var apiError by remember { mutableStateOf<String?>(null) }

    // Estado observable de autenticación del ViewModel
    val authState by authViewModel.authState.collectAsState()

    // Efecto para manejar cambios en el estado de autenticación
    LaunchedEffect(authState) {
        when (val state = authState) {
            // En caso de éxito, navega a la pantalla principal
            is Result.Success -> {
                // Navega a Home si el token no está vacío (registro exitoso)
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

            // Formulario de registro con campos de entrada
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Campo para nombre de usuario
                AuthInputField(
                    value = username,
                    onValueChange = {
                        username = it
                        usernameError = null
                        apiError = null
                    },
                    label = "Nombre de usuario",
                    leadingIcon = Icons.Default.Person,
                    error = usernameError
                )

                // Campo para correo electrónico
                AuthInputField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                        apiError = null
                    },
                    label = "Correo electrónico",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    error = emailError
                )

                // Campo para contraseña con opción para mostrar/ocultar
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

                // Campo para confirmar contraseña con opción para mostrar/ocultar
                AuthInputField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                        apiError = null
                    },
                    label = "Confirmar contraseña",
                    leadingIcon = Icons.Default.Lock,
                    isPasswordToggleEnabled = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    error = confirmPasswordError
                )

                // Mensaje de error de API que aparece/desaparece animadamente
                AnimatedVisibility(visible = apiError != null) {
                    Text(
                        text = apiError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }

                // Botón de registro con estado de carga
                PrimaryButton(
                    text = "Crear Cuenta",
                    isLoading = authState is Result.Loading,
                    onClick = {
                        // Validación de campos antes de enviar
                        val isUsernameValid = username.isNotBlank()
                        val isEmailValid = email.isNotBlank() && isValidEmail(email)
                        val isPasswordValid = password.length >= 6
                        val doPasswordsMatch = password == confirmPassword

                        // Asigna mensajes de error según las validaciones
                        usernameError = if (!isUsernameValid) "El nombre es obligatorio" else null
                        emailError = if (!isEmailValid) "El correo no es válido" else null
                        passwordError = if (!isPasswordValid) "Debe tener al menos 6 caracteres" else null
                        confirmPasswordError = if (!doPasswordsMatch) "Las contraseñas no coinciden" else null

                        // Si todas las validaciones pasan, envía la solicitud de registro
                        if (isUsernameValid && isEmailValid && isPasswordValid && doPasswordsMatch) {
                            authViewModel.register(
                                RegisterRequest(
                                    username = username,
                                    email = email,
                                    password = password
                                )
                            )
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Espaciador flexible que empuja el contenido de abajo hacia la parte inferior
            Spacer(modifier = Modifier.weight(1f))

            // Enlace para navegación a la pantalla de inicio de sesión
            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿Ya tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        "Inicia sesión",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
