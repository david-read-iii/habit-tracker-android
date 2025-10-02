package com.davidread.habittracker.login.model

import com.davidread.habittracker.common.model.ValidationResult

sealed class LoginFlowResult(
    open val emailValidationResult: ValidationResult,
    open val passwordValidationResult: ValidationResult,
) {
    data class Success(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult
    ) : LoginFlowResult(emailValidationResult, passwordValidationResult)

    data class ValidationError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult
    ) : LoginFlowResult(emailValidationResult, passwordValidationResult)

    data class IncorrectLoginCredentialsError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult
    ) : LoginFlowResult(emailValidationResult, passwordValidationResult)

    data class LoginServiceGenericError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult
    ) : LoginFlowResult(emailValidationResult, passwordValidationResult)

    data class NullTokenError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult
    ) : LoginFlowResult(emailValidationResult, passwordValidationResult)

    data class SaveAuthenticationTokenError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult
    ) : LoginFlowResult(emailValidationResult, passwordValidationResult)
}
