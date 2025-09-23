package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.signup.model.GetTimezoneResult
import com.davidread.habittracker.signup.model.SaveAuthenticationTokenResult
import com.davidread.habittracker.signup.model.SignUpFlowResult
import com.davidread.habittracker.signup.model.SignUpUserResult
import com.davidread.habittracker.signup.model.ValidationResult
import javax.inject.Inject

class SignUpFlowUseCase @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase,
    private val getTimezoneUseCase: GetTimezoneUseCase,
    private val signUpUserUseCase: SignUpUserUseCase,
    private val saveAuthenticationTokenUseCase: SaveAuthenticationTokenUseCase
) {

    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): SignUpFlowResult {
        val emailValidationResult = validateEmailUseCase(email = email)
        val passwordValidationResult = validatePasswordUseCase(password = password)
        val confirmPasswordValidationResult = validateConfirmPasswordUseCase(
            password = password,
            confirmPassword = confirmPassword
        )

        if (emailValidationResult == ValidationResult.Invalid || passwordValidationResult == ValidationResult.Invalid || confirmPasswordValidationResult == ValidationResult.Invalid) {
            return SignUpFlowResult.ValidationError(
                emailValidationResult,
                passwordValidationResult,
                confirmPasswordValidationResult
            )
        }

        val timezoneResult = getTimezoneUseCase()

        if (timezoneResult == GetTimezoneResult.Error) {
            return SignUpFlowResult.GetTimezoneError(
                emailValidationResult,
                passwordValidationResult,
                confirmPasswordValidationResult
            )
        }

        val signUpUserResult = signUpUserUseCase(
            email = email,
            password = password,
            timezone = (timezoneResult as GetTimezoneResult.Success).timezone
        )

        if (signUpUserResult == SignUpUserResult.EmailAlreadyUsed) {
            return SignUpFlowResult.EmailAlreadyUsedError(
                emailValidationResult,
                passwordValidationResult,
                confirmPasswordValidationResult
            )
        }

        if (signUpUserResult == SignUpUserResult.NullToken) {
            return SignUpFlowResult.NullTokenError(
                emailValidationResult,
                passwordValidationResult,
                confirmPasswordValidationResult
            )
        }

        if (signUpUserResult == SignUpUserResult.GenericError) {
            return SignUpFlowResult.SignUpServiceGenericError(
                emailValidationResult,
                passwordValidationResult,
                confirmPasswordValidationResult
            )
        }

        val saveAuthenticationTokenResult =
            saveAuthenticationTokenUseCase(token = (signUpUserResult as SignUpUserResult.Success).token)

        if (saveAuthenticationTokenResult == SaveAuthenticationTokenResult.Error) {
            return SignUpFlowResult.SaveAuthenticationTokenError(
                emailValidationResult,
                passwordValidationResult,
                confirmPasswordValidationResult
            )
        }

        return SignUpFlowResult.Success(
            emailValidationResult,
            passwordValidationResult,
            confirmPasswordValidationResult
        )
    }
}
