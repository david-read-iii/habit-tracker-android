package com.davidread.habittracker.list.model

// TODO: Remove any of these if they remain unused.
data class HabitListViewState(
    val showLoading: Boolean = false,
    val alertDialogViewState: AlertDialogViewState = AlertDialogViewState(),
    val checkingInHabitIds: Set<String> = emptySet(),
    val habitEditorBottomSheetViewState: HabitEditorBottomSheetViewState = HabitEditorBottomSheetViewState()
)

data class HabitEditorBottomSheetViewState(
    val showBottomSheet: Boolean = false,
    val title: String = "",
    val textFieldViewState: HabitListTextFieldViewState = HabitListTextFieldViewState(),
    val positiveButtonText: String = "",
    val isEditingHabit: Boolean = false,
    val mode: HabitEditorMode = HabitEditorMode.Add,
    val editingHabitId: String? = null
)

enum class HabitEditorMode {
    Add,
    Edit
}

data class HabitViewState(
    val id: String,
    val name: String,
    val streak: String,
    val createdAt: String
)

data class HabitListTextFieldViewState(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)

data class AlertDialogViewState(
    val showDialog: Boolean = false,
    val message: String? = null
)
