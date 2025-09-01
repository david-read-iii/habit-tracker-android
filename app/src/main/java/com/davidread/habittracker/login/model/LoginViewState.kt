package com.davidread.habittracker.login.model

data class LoginViewState(
    val emailTextFieldViewState: LoginTextFieldViewState = LoginTextFieldViewState(),
    val passwordTextFieldViewState: LoginTextFieldViewState = LoginTextFieldViewState(),
    val dialogViewState: DialogViewState = DialogViewState()
)

data class LoginTextFieldViewState(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)

data class PasswordTextFieldViewState(val value: String = "")

data class DialogViewState(
    val showDialog: Boolean = false,
    val message: String? = null
)
