package com.davidread.habittracker.login.model

sealed class LoginViewIntent {
    class ChangeEmailValue(val newValue: String) : LoginViewIntent()
    class ChangePasswordValue(val newValue: String) : LoginViewIntent()
    object ClickClearEmailButton : LoginViewIntent()
    object ClickTogglePasswordVisibilityButton : LoginViewIntent()
    object ClickLoginButton : LoginViewIntent()
    object ClickSignUpLink : LoginViewIntent()
    object ClickAlertDialogButton : LoginViewIntent()
}
