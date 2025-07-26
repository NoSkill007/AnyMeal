package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.R // Asegúrate de tener estos recursos

@Composable
fun SocialLoginRow(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onAppleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
    ) {
        // NOTA: Necesitarás añadir estos íconos a tu carpeta res/drawable.
        // Puedes encontrar versiones SVG de alta calidad en sitios como "icons.getbootstrap.com"
        // o "simpleicons.org".
        SocialButton(
            onClick = onGoogleClick,
            iconResId = R.drawable.ic_google,
            contentDescription = "Login with Google"
        )
        SocialButton(
            onClick = onFacebookClick,
            iconResId = R.drawable.ic_facebook,
            contentDescription = "Login with Facebook"
        )
        SocialButton(
            onClick = onAppleClick,
            iconResId = R.drawable.ic_apple,
            contentDescription = "Login with Apple"
        )
    }
}
