package com.davidread.habittracker.login.model

data class LoginViewState(
    val emailTextFieldViewState: LoginTextFieldViewState = LoginTextFieldViewState(),
    val passwordTextFieldViewState: LoginTextFieldViewState = LoginTextFieldViewState()
)

data class LoginTextFieldViewState(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)
