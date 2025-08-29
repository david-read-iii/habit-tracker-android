package com.davidread.habittracker.login.model

data class LoginViewState(
    val emailTextFieldViewState: EmailTextFieldViewState = EmailTextFieldViewState(),
    val passwordTextFieldViewState: PasswordTextFieldViewState = PasswordTextFieldViewState()
)

data class EmailTextFieldViewState(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)

data class PasswordTextFieldViewState(val value: String = "")
