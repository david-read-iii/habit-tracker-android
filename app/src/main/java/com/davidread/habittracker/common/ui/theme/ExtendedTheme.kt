package com.davidread.habittracker.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color as ComposeColor

@Immutable
data class HabitTrackerExtendedColors(
    val swipeDeleteContainer: ComposeColor,
    val swipeDeleteContent: ComposeColor,
    val swipeRenameContainer: ComposeColor,
    val swipeRenameContent: ComposeColor,
    val swipeCheckInContainer: ComposeColor,
    val swipeCheckInContent: ComposeColor
)

internal val LightExtendedColors = HabitTrackerExtendedColors(
    swipeDeleteContainer = Color.SwipeDeleteContainer,
    swipeDeleteContent = Color.SwipeDeleteContent,
    swipeRenameContainer = Color.SwipeRenameContainer,
    swipeRenameContent = Color.SwipeRenameContent,
    swipeCheckInContainer = Color.SwipeCheckInContainer,
    swipeCheckInContent = Color.SwipeCheckInContent
)

internal val DarkExtendedColors = HabitTrackerExtendedColors(
    swipeDeleteContainer = Color.SwipeDeleteContainerDark,
    swipeDeleteContent = Color.SwipeDeleteContentDark,
    swipeRenameContainer = Color.SwipeRenameContainerDark,
    swipeRenameContent = Color.SwipeRenameContentDark,
    swipeCheckInContainer = Color.SwipeCheckInContainerDark,
    swipeCheckInContent = Color.SwipeCheckInContentDark
)

internal val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

object HabitTrackerThemeExtras {
    val colors: HabitTrackerExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}
