package com.davidread.habittracker.list.model

sealed class HabitListViewEffect {
    object NavigateToSettingsScreen : HabitListViewEffect()
    data class ShowSnackbar(val message: String) : HabitListViewEffect()
}
