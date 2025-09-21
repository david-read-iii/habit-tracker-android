package com.davidread.habittracker.login.model

data class LoginViewState(
    val emailTextFieldViewState: LoginTextFieldViewState = LoginTextFieldViewState(),
    val passwordTextFieldViewState: LoginTextFieldViewState = LoginTextFieldViewState(),
    val showLoadingDialog: Boolean = false,
    val alertDialogViewState: AlertDialogViewState = AlertDialogViewState()
)

data class LoginTextFieldViewState(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)

data class AlertDialogViewState(
    val showDialog: Boolean = false,
    val message: String? = null
)
