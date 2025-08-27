package com.davidread.habittracker.common.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    secondary = AmberSecondary,
    onSecondary = Black,
    tertiary = TealTertiary,
    onTertiary = White,
    background = LightGrayBackground,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    error = RedError,
    onError = White
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
