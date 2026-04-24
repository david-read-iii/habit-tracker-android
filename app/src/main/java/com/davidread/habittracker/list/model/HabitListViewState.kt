package com.davidread.habittracker.list.model

// TODO: Remove any of these if they remain unused.
data class HabitListViewState(
    val showLoading: Boolean = false,
    val alertDialogViewState: AlertDialogViewState = AlertDialogViewState(),
    val checkingInHabitIds: Set<String> = emptySet(),
    val addHabitViewState: AddHabitViewState = AddHabitViewState()
)

data class AddHabitViewState(
    val showBottomSheet: Boolean = false,
    val textFieldViewState: HabitListTextFieldViewState = HabitListTextFieldViewState(),
    val isCreatingHabit: Boolean = false
)

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
