package com.davidread.habittracker.login.usecase

import android.app.Application
import android.util.Log
import com.davidread.habittracker.R
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

// TODO: Simplify this logic somehow?
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
            handleLoginServiceCall(
                viewState = viewState,
                emailValidationResult = emailValidationResult,
                passwordValidationResult = passwordValidationResult
            )
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
        return try {
            val loginResponse = loginRepository.login(
                loginRequest = LoginRequest(
                    email = viewState.emailTextFieldViewState.value,
                    password = viewState.passwordTextFieldViewState.value
                )
            )
            // TODO: Store token somewhere somehow.
            LoginResult(
                viewState = viewState.copy(
                    emailTextFieldViewState = emailValidationResult.loginTextFieldViewState,
                    passwordTextFieldViewState = passwordValidationResult.loginTextFieldViewState
                ),
                navigateToListScreen = true
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Error logging in", exception)
            if (exception is HttpException && exception.code() == 400) {
                LoginResult(
                    viewState = viewState.copy(
                        emailTextFieldViewState = emailValidationResult.loginTextFieldViewState,
                        passwordTextFieldViewState = passwordValidationResult.loginTextFieldViewState,
                        dialogViewState = DialogViewState(
                            showDialog = true,
                            message = application.getString(
                                R.string.login_credentials_incorrect_error_message
                            )
                        )
                    ),
                    navigateToListScreen = false
                )
            } else {
                LoginResult(
                    viewState = viewState.copy(
                        emailTextFieldViewState = emailValidationResult.loginTextFieldViewState,
                        passwordTextFieldViewState = passwordValidationResult.loginTextFieldViewState,
                        dialogViewState = DialogViewState(showDialog = true, message = null)
                    ),
                    navigateToListScreen = false
                )
            }
        }
    }
}
