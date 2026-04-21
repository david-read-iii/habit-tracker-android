package com.davidread.habittracker.list.model

// TODO: Remove any of these if they remain unused.
sealed class HabitListViewIntent {
    object ClickAddHabitButton : HabitListViewIntent()
    object ClickSettingsButton : HabitListViewIntent()
    object PullToRefresh : HabitListViewIntent()
    class ClickHabit(val habitId: String) : HabitListViewIntent()
    class ClickCheckInHabitButton(val habitId: String) : HabitListViewIntent()
    class ClickEditHabitButton(val habitId: String) : HabitListViewIntent()
    class ClickDeleteHabitButton(val habitId: String) : HabitListViewIntent()
    object ClickAlertDialogButton : HabitListViewIntent()
}
