package com.davidread.habittracker.login.model

data class LoginTextFieldValidationResult(
    val status: Status,
    val loginTextFieldViewState: LoginTextFieldViewState,
) {
    enum class Status { VALID, INVALID }
}
