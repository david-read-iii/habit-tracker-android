package com.davidread.habittracker.login.model

data class LoginViewState(
    val emailTextFieldViewState: EmailTextFieldViewState = EmailTextFieldViewState(),
    val passwordTextFieldViewState: PasswordTextFieldViewState = PasswordTextFieldViewState(),
    val dialogViewState: DialogViewState = DialogViewState()
)

data class EmailTextFieldViewState(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)

data class PasswordTextFieldViewState(val value: String = "")

data class DialogViewState(
    val showDialog: Boolean = false,
    val message: String = ""
)
