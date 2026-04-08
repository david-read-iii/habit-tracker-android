package com.davidread.habittracker.list.model

// TODO: Remove any of these if they remain unused.
data class HabitListViewState(
    val showLoading: Boolean = false,
    val alertDialogViewState: AlertDialogViewState = AlertDialogViewState(),
    val checkingInHabitIds: Set<String> = emptySet()
)

data class HabitViewState(
    val id: String,
    val name: String,
    val streak: String,
    val createdAt: String
)

data class AlertDialogViewState(
    val showDialog: Boolean = false,
    val message: String? = null
)
