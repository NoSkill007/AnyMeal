package com.noskill.anymeal.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

@Composable
fun BrandTitle(
    first: String = "Any",
    second: String = "Meal",
    firstColor: Color = Color(0xFF90A4AE),
    secondColor: Color = Color(0xFF26A69A),
    fontSize: TextUnit = 32.sp,
    modifier: Modifier = Modifier
) {
    Text(
        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = firstColor,
                    fontWeight = FontWeight.Bold
                )
            ) { append(first) }
            withStyle(
                SpanStyle(
                    color = secondColor,
                    fontWeight = FontWeight.Bold
                )
            ) { append(second) }
        },
        fontSize = fontSize,
        modifier = modifier,
        textAlign = TextAlign.Center,
        letterSpacing = 1.5.sp,
        lineHeight = 38.sp,
    )
}
