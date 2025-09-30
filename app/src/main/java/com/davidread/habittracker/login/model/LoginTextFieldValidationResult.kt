package com.davidread.habittracker.login.model

@Deprecated("Use common ValidationResult")
data class LoginTextFieldValidationResult(
    val status: Status,
    val loginTextFieldViewState: LoginTextFieldViewState,
) {
    enum class Status { VALID, INVALID }
}
