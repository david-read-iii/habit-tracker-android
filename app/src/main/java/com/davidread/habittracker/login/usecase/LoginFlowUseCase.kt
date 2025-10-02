package com.davidread.habittracker.login.usecase

import com.davidread.habittracker.common.model.SaveAuthenticationTokenResult
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.common.usecase.SaveAuthenticationTokenUseCase
import com.davidread.habittracker.common.usecase.ValidateEmailUseCase
import com.davidread.habittracker.common.usecase.ValidatePasswordUseCase
import com.davidread.habittracker.login.model.LoginFlowResult
import com.davidread.habittracker.login.model.LoginUserResult
import javax.inject.Inject

class LoginFlowUseCase @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val saveAuthenticationTokenUseCase: SaveAuthenticationTokenUseCase
) {

    suspend operator fun invoke(email: String, password: String): LoginFlowResult {
        val emailValidationResult = validateEmailUseCase(email)
        val passwordValidationResult = validatePasswordUseCase(password)

        if (emailValidationResult == ValidationResult.Invalid || passwordValidationResult == ValidationResult.Invalid) {
            return LoginFlowResult.ValidationError(emailValidationResult, passwordValidationResult)
        }

        val loginUserResult = loginUserUseCase(email = email, password = password)

        if (loginUserResult == LoginUserResult.IncorrectLoginCredentials) {
            return LoginFlowResult.IncorrectLoginCredentialsError(
                emailValidationResult,
                passwordValidationResult
            )
        }

        if (loginUserResult == LoginUserResult.NullToken) {
            return LoginFlowResult.NullTokenError(emailValidationResult, passwordValidationResult)
        }

        if (loginUserResult == LoginUserResult.GenericError) {
            return LoginFlowResult.LoginServiceGenericError(
                emailValidationResult,
                passwordValidationResult
            )
        }

        val saveAuthenticationTokenResult =
            saveAuthenticationTokenUseCase(token = (loginUserResult as LoginUserResult.Success).token)

        if (saveAuthenticationTokenResult == SaveAuthenticationTokenResult.Error) {
            return LoginFlowResult.SaveAuthenticationTokenError(
                emailValidationResult,
                passwordValidationResult
            )
        }

        return LoginFlowResult.Success(emailValidationResult, passwordValidationResult)
    }
}
