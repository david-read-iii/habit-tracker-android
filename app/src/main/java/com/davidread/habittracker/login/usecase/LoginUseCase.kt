package com.davidread.habittracker.login.usecase

import android.app.Application
import android.util.Log
import com.davidread.habittracker.R
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.login.model.DialogViewState
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
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val loginRepository: LoginRepository
) {

    suspend operator fun invoke(viewState: LoginViewState): LoginResult {
        val emailValidationResult = validateEmailUseCase(viewState.emailTextFieldViewState)
        val passwordValidationResult = validatePasswordUseCase(viewState.passwordTextFieldViewState)

        return if (emailValidationResult.status == Status.VALID && passwordValidationResult.status == Status.VALID) {
            handleLoginServiceCall(viewState, emailValidationResult, passwordValidationResult)
        } else {
            LoginResult(
                viewState = viewState.copy(
                    emailTextFieldViewState = emailValidationResult.loginTextFieldViewState,
                    passwordTextFieldViewState = passwordValidationResult.loginTextFieldViewState
                ),
                navigateToListScreen = false
            )
        }
    }

    private suspend fun handleLoginServiceCall(
        viewState: LoginViewState,
        emailValidationResult: LoginTextFieldValidationResult,
        passwordValidationResult: LoginTextFieldValidationResult
    ): LoginResult {
        val loginServiceResult = loginRepository.login(
            loginRequest = LoginRequest(
                email = viewState.emailTextFieldViewState.value,
                password = viewState.passwordTextFieldViewState.value
            )
        )

        return when (loginServiceResult) {
            is Result.Success -> {
                val token = loginServiceResult.data.token
                // TODO: Store token somewhere somehow.
                LoginResult(
                    viewState = viewState.copy(
                        emailTextFieldViewState = emailValidationResult.loginTextFieldViewState,
                        passwordTextFieldViewState = passwordValidationResult.loginTextFieldViewState
                    ),
                    navigateToListScreen = true
                )
            }

            is Result.Error -> {
                Log.e(TAG, "Error logging in", loginServiceResult.exception)
                val message =
                    if (loginServiceResult.exception is HttpException && loginServiceResult.exception.code() == 400) {
                        application.getString(
                            R.string.login_credentials_incorrect_error_message
                        )
                    } else {
                        null
                    }
                LoginResult(
                    viewState = viewState.copy(
                        emailTextFieldViewState = emailValidationResult.loginTextFieldViewState,
                        passwordTextFieldViewState = passwordValidationResult.loginTextFieldViewState,
                        dialogViewState = DialogViewState(
                            showDialog = true,
                            message = message
                        )
                    ),
                    navigateToListScreen = false
                )
            }
        }
    }
}
