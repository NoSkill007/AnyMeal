// ========================================================================
// Archivo: ui/screens/LoginScreen.kt
// ========================================================================
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


@Composable
fun LoginScreen(
    navController: NavController,
    onRegisterClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var apiError by remember { mutableStateOf<String?>(null) }

    val authState by authViewModel.authState.collectAsState()

    // Observa el estado de la autenticación para mostrar errores o navegar
    LaunchedEffect(authState) {
        when (val state = authState) {
            is Result.Success -> {
                // Navega a Home si el token no está vacío (es decir, el login fue exitoso)
                if (state.data.token.isNotBlank()) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            }
            is Result.Error -> {
                // Muestra el error de la API
                apiError = state.message
            }
            else -> {
                // Limpia el error en otros estados como Loading o el inicial
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

            // Formulario de Login
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

                AnimatedVisibility(visible = apiError != null) {
                    Text(
                        text = apiError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                PrimaryButton(
                    text = "Ingresar",
                    isLoading = authState is Result.Loading,
                    onClick = {
                        var valid = true
                        // NOTA: El backend usa 'username' para el login, que puede ser el email o el nombre de usuario.
                        emailError = if (email.isBlank()) {
                            valid = false; "Este campo es obligatorio"
                        } else null
                        passwordError = if (password.isBlank()) {
                            valid = false; "La contraseña es obligatoria"
                        } else null

                        if (valid) {
                            authViewModel.login(LoginRequest(username = email, password = password))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divisor y Login Social
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Text("O ingresa con", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                }
                // Asegúrate de tener el componente SocialLoginRow y los íconos necesarios
                SocialLoginRow(
                    onGoogleClick = { /* TODO */ },
                    onFacebookClick = { /* TODO */ },
                    onAppleClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Enlace para registrarse
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