package com.noskill.anymeal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noskill.anymeal.R // Asegúrate de tener un recurso drawable
import com.noskill.anymeal.ui.components.PrimaryButton
import com.noskill.anymeal.ui.components.SecondaryButton
import com.noskill.anymeal.ui.theme.isLight
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        delay(200)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Usamos un color de fondo sólido que respeta el tema
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(durationMillis = 800, delayMillis = 100)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 100))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cooking_illustration),
                        contentDescription = "Ilustración de Cocina",
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .aspectRatio(1f)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Bienvenido a AnyMeal",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Encuentra y guarda tus recetas favoritas en un solo lugar.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(durationMillis = 800, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 300))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    PrimaryButton(
                        text = "Iniciar Sesión",
                        onClick = onLoginClick
                    )
                    SecondaryButton(
                        text = "Crear Cuenta",
                        onClick = onRegisterClick
                    )
                }
            }
        }
    }
}
