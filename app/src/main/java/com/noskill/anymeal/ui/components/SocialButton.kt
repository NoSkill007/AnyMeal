package com.noskill.anymeal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.theme.isLight

@Composable
fun SocialButton(
    onClick: () -> Unit,
    iconResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val isDark = !MaterialTheme.colorScheme.isLight

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(52.dp),
        shape = CircleShape,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            // Hacemos el contenedor del botón transparente para que se vea nuestro fondo
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        // Usamos un Box para aplicar el fondo con color sólido
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Usamos un color sólido en lugar de un degradado
                    color = if (isDark) {
                        // Un fondo sólido y sutil para el modo oscuro
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    } else {
                        // Sin fondo en modo claro
                        Color.Transparent
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
