package com.davidread.habittracker.common.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Color.GreenPrimary,
    onPrimary = Color.White,
    secondary = Color.AmberSecondary,
    onSecondary = Color.Black,
    tertiary = Color.TealTertiary,
    onTertiary = Color.White,
    background = Color.LightGrayBackground,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color.RedError,
    onError = Color.White
)

@Composable
fun HabitTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
