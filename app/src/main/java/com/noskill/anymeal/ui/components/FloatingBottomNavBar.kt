package com.noskill.anymeal.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.models.NavItem
import com.noskill.anymeal.ui.theme.textSecondary

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FloatingBottomNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Este Box se encarga del posicionamiento y el espaciado seguro.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Añade padding para los gestos del sistema
            .padding(bottom = 16.dp), // Espaciado inferior para que flote
        contentAlignment = Alignment.BottomCenter
    ) {
        // Este es el Box que contiene el diseño de la barra
        Box(
            modifier = Modifier
                .shadow(12.dp, RoundedCornerShape(32.dp))
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(32.dp)
                )
                .clip(RoundedCornerShape(32.dp))
        ) {
            Row(
                modifier = Modifier
                    .height(66.dp)
                    .width(IntrinsicSize.Max) // Ajusta el ancho al contenido
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { i, item ->
                    val selected = i == selectedIndex
                    val animWidth by animateDpAsState(
                        targetValue = if (selected) 110.dp else 56.dp,
                        label = "width"
                    )
                    val animIconSize by animateDpAsState(
                        targetValue = if (selected) 30.dp else 24.dp,
                        label = "icon"
                    )
                    val animBgColor by animateColorAsState(
                        targetValue = if (selected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        else
                            Color.Transparent,
                        label = "bg"
                    )
                    Surface(
                        onClick = { onItemSelected(i) },
                        color = Color.Transparent,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .width(animWidth)
                            .padding(horizontal = 2.dp),
                        shadowElevation = 0.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(animBgColor, RoundedCornerShape(24.dp))
                                .padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.textSecondary,
                                modifier = Modifier.size(animIconSize)
                            )
                            AnimatedVisibility(
                                visible = selected,
                                enter = fadeIn(tween(180)) + expandHorizontally(tween(180)),
                                exit = fadeOut(tween(120)) + shrinkHorizontally(tween(120))
                            ) {
                                Text(
                                    item.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(start = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
