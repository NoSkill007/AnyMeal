/* --------------------------------------------------------------------
 * Archivo: RegisterScreen.kt (ACTUALIZADO Y FUNCIONAL)
 * Descripción: UI rediseñada e integrada con AuthViewModel para
 * realizar el registro de usuarios contra el backend.
 * --------------------------------------------------------------------
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

@Composable
fun RegisterScreen(
    navController: NavController,
    onLoginClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var apiError by remember { mutableStateOf<String?>(null) }

    val authState by authViewModel.authState.collectAsState()

    // Observa el estado de la autenticación para mostrar errores o navegar
    LaunchedEffect(authState) {
        when (val state = authState) {
            is Result.Success -> {
                // Navega a Home si el token no está vacío (registro exitoso)
                if (state.data.token.isNotBlank()) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            }
            is Result.Error -> {
                // Muestra el error proveniente de la API
                apiError = state.message
            }
            else -> {
                // Limpia el error en otros estados (Loading, inicial)
                apiError = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo y Título
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                    // Asegúrate de tener un recurso drawable llamado 'logo' o similar
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Logo AnyMeal",
                        modifier = Modifier.size(60.dp)
                    )
                }
                BrandTitle()
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Formulario de Registro
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

                // Muestra el error de la API si existe
                AnimatedVisibility(visible = apiError != null) {
                    Text(
                        text = apiError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }

                PrimaryButton(
                    text = "Crear Cuenta",
                    isLoading = authState is Result.Loading,
                    onClick = {
                        val isUsernameValid = username.isNotBlank()
                        val isEmailValid = email.isNotBlank() && isValidEmail(email)
                        val isPasswordValid = password.length >= 6
                        val doPasswordsMatch = password == confirmPassword

                        usernameError = if (!isUsernameValid) "El nombre es obligatorio" else null
                        emailError = if (!isEmailValid) "El correo no es válido" else null
                        passwordError = if (!isPasswordValid) "Debe tener al menos 6 caracteres" else null
                        confirmPasswordError = if (!doPasswordsMatch) "Las contraseñas no coinciden" else null

                        if (isUsernameValid && isEmailValid && isPasswordValid && doPasswordsMatch) {
                            // Llama al ViewModel para registrar al usuario
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

            Spacer(modifier = Modifier.weight(1f))

            // Enlace para iniciar sesión
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
