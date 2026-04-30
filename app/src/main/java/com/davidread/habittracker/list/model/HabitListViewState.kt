package com.davidread.habittracker.list.model

data class HabitListViewState(
    val checkingInHabitIds: Set<String> = emptySet(),
    val isRefreshing: Boolean = false,
    val habitEditorBottomSheetViewState: HabitEditorBottomSheetViewState = HabitEditorBottomSheetViewState(),
    val deleteHabitDialogViewState: DeleteHabitDialogViewState = DeleteHabitDialogViewState(),
    val showLogoutDialog: Boolean = false
)

data class HabitEditorBottomSheetViewState(
    val showBottomSheet: Boolean = false,
    val title: String = "",
    val textFieldViewState: HabitListTextFieldViewState = HabitListTextFieldViewState(),
    val positiveButtonText: String = "",
    val isSubmitting: Boolean = false,
    val editorState: EditorState = EditorState.Add
)

data class HabitListTextFieldViewState(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)

data class DeleteHabitDialogViewState(
    val showDialog: Boolean = false,
    val habitId: String? = null,
    val isSubmitting: Boolean = false
)

sealed interface EditorState {
    data object Add : EditorState
    data class Edit(val habitId: String) : EditorState
}

data class HabitViewState(
    val id: String,
    val name: String,
    val streak: String,
    val createdAt: String
)
