package com.davidread.habittracker.list.model

sealed class HabitListViewEffect {
    object NavigateToSettingsScreen : HabitListViewEffect()
    object NavigateToLoginScreen : HabitListViewEffect()
    data class ShowSnackbar(val message: String) : HabitListViewEffect()
    data class AnnounceForAccessibility(val message: String) : HabitListViewEffect()
}
