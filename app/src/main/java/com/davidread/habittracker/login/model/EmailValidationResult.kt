package com.davidread.habittracker.login.model

data class EmailValidationResult(
    val status: Status,
    val emailTextFieldViewState: EmailTextFieldViewState,
) {
    enum class Status { VALID, INVALID }
}
