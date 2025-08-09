/**
 * FloatingBottomNavBar.kt
 *
 * Este archivo define un componente Composable que implementa una barra de navegación inferior
 * flotante con diseño material y efectos de animación. El componente muestra los ítems de
 * navegación de la aplicación con iconos y etiquetas, destacando visualmente el ítem seleccionado
 * mediante animaciones de tamaño, color y texto.
 */
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

/**
 * Componente que muestra una barra de navegación inferior flotante con animaciones.
 * Proporciona un diseño moderno con elementos interactivos que cambian de tamaño y
 * muestran texto al ser seleccionados.
 *
 * @param items Lista de elementos de navegación (NavItem) a mostrar en la barra
 * @param selectedIndex Índice del elemento actualmente seleccionado
 * @param onItemSelected Función callback que se ejecuta cuando se selecciona un elemento, recibe el índice seleccionado
 * @param modifier Modificador opcional para personalizar el diseño
 */
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
        // Este es el Box que contiene el diseño de la barra con sombra y bordes redondeados
        Box(
            modifier = Modifier
                .shadow(12.dp, RoundedCornerShape(32.dp)) // Añade sombra para efecto flotante
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), // Fondo semi-transparente
                    shape = RoundedCornerShape(32.dp) // Bordes muy redondeados
                )
                .clip(RoundedCornerShape(32.dp)) // Recorta el contenido a la forma redondeada
        ) {
            // Fila que contiene los elementos de navegación
            Row(
                modifier = Modifier
                    .height(66.dp) // Altura fija para la barra
                    .width(IntrinsicSize.Max) // Ajusta el ancho al contenido necesario
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround, // Distribuye los elementos uniformemente
                verticalAlignment = Alignment.CenterVertically // Centra verticalmente los elementos
            ) {
                // Itera sobre cada elemento de navegación
                items.forEachIndexed { i, item ->
                    val selected = i == selectedIndex // Determina si este elemento está seleccionado

                    // Animaciones para diferentes propiedades cuando cambia el estado de selección
                    val animWidth by animateDpAsState(
                        targetValue = if (selected) 110.dp else 56.dp, // Más ancho cuando está seleccionado
                        label = "width"
                    )
                    val animIconSize by animateDpAsState(
                        targetValue = if (selected) 30.dp else 24.dp, // Icono más grande cuando está seleccionado
                        label = "icon"
                    )
                    val animBgColor by animateColorAsState(
                        targetValue = if (selected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) // Fondo con color primario cuando seleccionado
                        else
                            Color.Transparent, // Transparente cuando no está seleccionado
                        label = "bg"
                    )

                    // Superficie clickable para cada elemento de navegación
                    Surface(
                        onClick = { onItemSelected(i) }, // Ejecuta callback al hacer clic
                        color = Color.Transparent, // Sin color de fondo propio
                        shape = RoundedCornerShape(24.dp), // Forma redondeada
                        modifier = Modifier
                            .height(48.dp) // Altura fija para elementos
                            .width(animWidth) // Ancho animado según estado
                            .padding(horizontal = 2.dp),
                        shadowElevation = 0.dp // Sin sombra individual
                    ) {
                        // Fila para organizar el icono y el texto (si está visible)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(animBgColor, RoundedCornerShape(24.dp)) // Fondo animado
                                .padding(horizontal = 8.dp)
                        ) {
                            // Icono del elemento de navegación
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label, // Usa la etiqueta como descripción para accesibilidad
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.textSecondary, // Color según selección
                                modifier = Modifier.size(animIconSize) // Tamaño animado
                            )

                            // Texto que aparece/desaparece con animación cuando el elemento está seleccionado
                            AnimatedVisibility(
                                visible = selected, // Solo visible cuando está seleccionado
                                enter = fadeIn(tween(180)) + expandHorizontally(tween(180)), // Animación de entrada
                                exit = fadeOut(tween(120)) + shrinkHorizontally(tween(120))  // Animación de salida
                            ) {
                                // Texto del elemento de navegación
                                Text(
                                    item.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary, // Color primario para destacar
                                    maxLines = 1, // Una sola línea
                                    softWrap = false, // Sin envolver el texto
                                    modifier = Modifier.padding(start = 7.dp) // Espacio entre el icono y el texto
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
