package com.davidread.habittracker.settings.model

data class SettingsViewState(
    val resetTimezoneConfirmationDialogViewState: ResetTimezoneConfirmationDialogViewState = ResetTimezoneConfirmationDialogViewState(),
    val showLogoutDialog: Boolean = false
)

data class ResetTimezoneConfirmationDialogViewState(
    val showDialog: Boolean = false,
    val isSubmitting: Boolean = false
)
