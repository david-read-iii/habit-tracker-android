package com.davidread.habittracker.signup.model

sealed class SignUpFlowResult(
    open val emailValidationResult: ValidationResult,
    open val passwordValidationResult: ValidationResult,
    open val confirmPasswordValidationResult: ValidationResult
) {
    data class Success(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult,
        override val confirmPasswordValidationResult: ValidationResult
    ) : SignUpFlowResult(
        emailValidationResult,
        passwordValidationResult,
        confirmPasswordValidationResult
    )

    data class ValidationError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult,
        override val confirmPasswordValidationResult: ValidationResult
    ) : SignUpFlowResult(
        emailValidationResult,
        passwordValidationResult,
        confirmPasswordValidationResult

    )

    data class GetTimezoneError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult,
        override val confirmPasswordValidationResult: ValidationResult
    ) : SignUpFlowResult(
        emailValidationResult,
        passwordValidationResult,
        confirmPasswordValidationResult
    )

    data class EmailAlreadyUsedError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult,
        override val confirmPasswordValidationResult: ValidationResult
    ) : SignUpFlowResult(
        emailValidationResult,
        passwordValidationResult,
        confirmPasswordValidationResult
    )

    data class SignUpServiceGenericError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult,
        override val confirmPasswordValidationResult: ValidationResult
    ) : SignUpFlowResult(
        emailValidationResult,
        passwordValidationResult,
        confirmPasswordValidationResult
    )

    data class NullTokenError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult,
        override val confirmPasswordValidationResult: ValidationResult
    ) : SignUpFlowResult(
        emailValidationResult,
        passwordValidationResult,
        confirmPasswordValidationResult
    )

    data class SaveAuthenticationTokenError(
        override val emailValidationResult: ValidationResult,
        override val passwordValidationResult: ValidationResult,
        override val confirmPasswordValidationResult: ValidationResult
    ) : SignUpFlowResult(
        emailValidationResult,
        passwordValidationResult,
        confirmPasswordValidationResult
    )
}
