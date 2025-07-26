package com.noskill.anymeal.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.viewmodel.ProfileViewModel
import com.noskill.anymeal.viewmodel.SaveState
import kotlinx.coroutines.delay
import androidx.navigation.NavGraph.Companion.findStartDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val user = uiState.user

    // Estados para los campos de texto
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Este LaunchedEffect ahora solo se encarga de inicializar los campos
    // cuando el 'user' cambia por primera vez o cuando la pantalla se recrea.
    LaunchedEffect(user) {
        user?.let {
            name = it.username
            email = it.email
        }
    }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Al hacer clic en "Atrás", simplemente volvemos.
                        // La lógica de actualización de datos se maneja al guardar.
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Tarjeta para Información Personal
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Información Personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                    Button(
                        onClick = { profileViewModel.updateProfileData(name, email) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = uiState.profileSaveState != SaveState.LOADING
                    ) {
                        AnimatedContent(targetState = uiState.profileSaveState, label = "profileButtonState") { state ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                when (state) {
                                    SaveState.LOADING -> {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Guardando...")
                                    }
                                    SaveState.SUCCESS -> {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("¡Guardado!")
                                    }
                                    else -> Text("Guardar Información")
                                }
                            }
                        }
                    }
                }
            }

            // Tarjeta para Cambiar Contraseña
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Cambiar Contraseña", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(value = oldPassword, onValueChange = { oldPassword = it }, label = { Text("Contraseña Actual") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("Nueva Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = confirmNewPassword, onValueChange = { confirmNewPassword = it }, label = { Text("Confirmar Nueva Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                    Button(
                        onClick = { profileViewModel.changePassword(oldPassword, newPassword, confirmNewPassword) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = uiState.passwordSaveState != SaveState.LOADING
                    ) {
                        AnimatedContent(targetState = uiState.passwordSaveState, label = "passwordButtonState") { state ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                when (state) {
                                    SaveState.LOADING -> {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Cambiando...")
                                    }
                                    SaveState.SUCCESS -> {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("¡Cambiado!")
                                    }
                                    else -> Text("Cambiar Contraseña")
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    // LaunchedEffect para manejar el éxito de guardado y la reautenticación
    LaunchedEffect(uiState.profileSaveState, uiState.passwordSaveState, uiState.requiresReauthentication) {
        // Si la reautenticación es necesaria, esa es la prioridad.
        if (uiState.requiresReauthentication) {
            delay(2000) // Pequeño delay para que el usuario pueda leer el mensaje
            navController.navigate(Screen.Auth.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            profileViewModel.resetReauthenticationFlag()
            profileViewModel.resetSaveStates() // Asegurarse de que los estados de guardado también se reseteen
        }
        // Si no se requiere reautenticación y hubo un éxito al guardar perfil/contraseña
        else if (uiState.profileSaveState == SaveState.SUCCESS || uiState.passwordSaveState == SaveState.SUCCESS) {
            profileViewModel.loadUserProfile() // Recargar el perfil para obtener los datos más recientes
            delay(1500) // Pequeño delay para que el usuario vea el mensaje de éxito
            // MODIFICADO: Navegar a ProfileScreen de forma que ProfileScreen se recree
            // y luego, si desde ProfileScreen vuelven a EditProfileScreen, esta se recree también.
            // Para esto, ProfileScreen también debe ser navegada con restoreState = false
            // o simplemente hacer popBackStack y confiar en que ProfileScreen refresque.
            // La mejor opción es que ProfileScreen se refresque al volver a ella.
            navController.popBackStack() // Volver a ProfileScreen
            profileViewModel.resetSaveStates() // Limpia el estado
        }
    }
}