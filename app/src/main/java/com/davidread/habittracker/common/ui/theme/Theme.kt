package com.davidread.habittracker.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
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

private val DarkColorScheme = darkColorScheme(
    primary = Color.GreenPrimary,
    onPrimary = Color.Black,
    secondary = Color.AmberSecondary,
    onSecondary = Color.Black,
    tertiary = Color.TealTertiary,
    onTertiary = Color.Black,
    background = Color.DarkGrayBackground,
    onBackground = Color.White,
    surface = Color.DarkGraySurface,
    onSurface = Color.White,
    error = Color.RedError,
    onError = Color.White
)

@Composable
fun HabitTrackerTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
