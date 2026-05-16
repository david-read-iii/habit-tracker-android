package com.davidread.habittracker.signup.model

data class SignUpViewState(
    val emailTextFieldViewState: SignUpTextFieldViewState = SignUpTextFieldViewState(),
    val passwordTextFieldViewState: SignUpTextFieldViewState = SignUpTextFieldViewState(),
    val confirmPasswordTextFieldViewState: SignUpTextFieldViewState = SignUpTextFieldViewState(),
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val showLoading: Boolean = false,
    val alertDialogViewState: AlertDialogViewState = AlertDialogViewState()
)

data class SignUpTextFieldViewState(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)

data class AlertDialogViewState(
    val showDialog: Boolean = false,
    val message: String? = null
)
