package com.davidread.habittracker.signup.model

sealed class SignUpViewIntent {
    class ChangeEmailValue(val newValue: String) : SignUpViewIntent()
    class ChangePasswordValue(val newValue: String) : SignUpViewIntent()
    class ChangeConfirmPasswordValue(val newValue: String) : SignUpViewIntent()
    object ClickSignUpButton : SignUpViewIntent()
    object ClickAlertDialogButton : SignUpViewIntent()
}
