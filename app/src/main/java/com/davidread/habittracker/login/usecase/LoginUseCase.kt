package com.davidread.habittracker.login.usecase

import android.app.Application
import com.davidread.habittracker.R
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.login.model.AlertDialogViewState
import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult.Status
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.repository.LoginRepository
import retrofit2.HttpException
import javax.inject.Inject

private const val TAG = "LoginUseCase"

class LoginUseCase @Inject constructor(
    private val application: Application,
    private val logger: Logger,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val loginRepository: LoginRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository
) {

    suspend operator fun invoke(viewState: LoginViewState): LoginResult {
        val emailValidationResult = validateEmailUseCase(viewState.emailTextFieldViewState)
        val passwordValidationResult = validatePasswordUseCase(viewState.passwordTextFieldViewState)

        if (emailValidationResult.status == Status.INVALID || passwordValidationResult.status == Status.INVALID) {
            return getErrorLoginResult(
                viewState = viewState,
                emailValidationResult = emailValidationResult,
                passwordValidationResult = passwordValidationResult,
                showErrorDialog = false
            )
        }

        val loginServiceResult = loginRepository.login(
            loginRequest = LoginRequest(
                email = viewState.emailTextFieldViewState.value,
                password = viewState.passwordTextFieldViewState.value
            )
        )

        if (loginServiceResult is Result.Error) {
            logger.e(TAG, "Error logging in", loginServiceResult.exception)
            return getErrorLoginResult(
                viewState = viewState,
                emailValidationResult = emailValidationResult,
                passwordValidationResult = passwordValidationResult,
                showErrorDialog = true,
                exception = loginServiceResult.exception
            )
        }

        val token = (loginServiceResult as Result.Success).data.token

        if (token == null) {
            logger.e(TAG, "Service returned null token")
            return getErrorLoginResult(
                viewState = viewState,
                emailValidationResult = emailValidationResult,
                passwordValidationResult = passwordValidationResult,
                showErrorDialog = true
            )
        }

        val tokenSaveResult = authenticationTokenRepository.saveAuthenticationToken(token)

        if (tokenSaveResult is Result.Error) {
            return getErrorLoginResult(
                viewState = viewState,
                emailValidationResult = emailValidationResult,
                passwordValidationResult = passwordValidationResult,
                showErrorDialog = true,
                exception = tokenSaveResult.exception
            )
        }

        return LoginResult(
            viewState = viewState.copy(
                emailTextFieldViewState = emailValidationResult.loginTextFieldViewState,
                passwordTextFieldViewState = passwordValidationResult.loginTextFieldViewState
            ),
            navigateToListScreen = true
        )
    }

    private fun getErrorLoginResult(
        viewState: LoginViewState,
        emailValidationResult: LoginTextFieldValidationResult,
        passwordValidationResult: LoginTextFieldValidationResult,
        showErrorDialog: Boolean,
        exception: Exception? = null
    ) = LoginResult(
        viewState = viewState.copy(
            emailTextFieldViewState = emailValidationResult.loginTextFieldViewState,
            passwordTextFieldViewState = passwordValidationResult.loginTextFieldViewState,
            alertDialogViewState = if (showErrorDialog) {
                val message = if (exception is HttpException && exception.code() == 400) {
                    application.getString(
                        R.string.login_credentials_incorrect_error_message
                    )
                } else {
                    null
                }
                AlertDialogViewState(showDialog = true, message = message)
            } else {
                AlertDialogViewState(showDialog = false, message = null)
            }
        ),
        navigateToListScreen = false
    )
}
