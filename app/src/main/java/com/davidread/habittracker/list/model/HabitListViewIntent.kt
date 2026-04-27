package com.davidread.habittracker.list.model

sealed class HabitListViewIntent {
    object ClickAddHabitButton : HabitListViewIntent()
    object ClickSettingsButton : HabitListViewIntent()
    object PullToRefresh : HabitListViewIntent()
    object RefreshComplete : HabitListViewIntent()
    class ClickHabit(val habitId: String) : HabitListViewIntent()
    class ClickCheckInHabitButton(val habitId: String) : HabitListViewIntent()
    class ClickEditHabitButton(val habitId: String, val currentName: String = "") : HabitListViewIntent()
    class ClickDeleteHabitButton(val habitId: String) : HabitListViewIntent()
    data class ChangeHabitEditorNameValue(val value: String) : HabitListViewIntent()
    object DismissHabitEditorBottomSheet : HabitListViewIntent()
    object SubmitHabitEditorChanges : HabitListViewIntent()
    object DismissDeleteHabitDialog : HabitListViewIntent()
    object ConfirmDeleteHabit : HabitListViewIntent()
}
