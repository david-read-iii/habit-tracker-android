package com.davidread.habittracker.login.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidread.habittracker.R
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.login.model.AlertDialogViewState
import com.davidread.habittracker.login.model.LoginFlowResult
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.usecase.LoginFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginFlowUseCase: LoginFlowUseCase,
    private val application: Application
) : ViewModel() {

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState>
        get() = _viewState

    private val _viewEffect = MutableSharedFlow<LoginViewEffect>()
    val viewEffect: SharedFlow<LoginViewEffect>
        get() = _viewEffect

    fun processIntent(intent: LoginViewIntent) = when (intent) {
        is LoginViewIntent.ChangeEmailValue -> {
            _viewState.update {
                it.copy(
                    emailTextFieldViewState = it.emailTextFieldViewState.copy(
                        value = intent.newValue
                    )
                )
            }
        }

        is LoginViewIntent.ChangePasswordValue -> {
            _viewState.update {
                it.copy(
                    passwordTextFieldViewState = it.passwordTextFieldViewState.copy(
                        value = intent.newValue
                    )
                )
            }
        }

        is LoginViewIntent.ClickLoginButton -> handleLoginButtonClick()

        is LoginViewIntent.ClickSignUpLink -> {
            viewModelScope.launch {
                _viewEffect.emit(LoginViewEffect.NavigateToSignUpScreen)
            }
        }

        is LoginViewIntent.ClickAlertDialogButton -> {
            _viewState.update {
                it.copy(alertDialogViewState = AlertDialogViewState(showDialog = false))
            }
        }
    }

    private fun handleLoginButtonClick() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(showLoadingDialog = true)
            }

            val loginFlowResult = loginFlowUseCase(
                email = viewState.value.emailTextFieldViewState.value,
                password = viewState.value.passwordTextFieldViewState.value
            )

            _viewState.update {
                it.copy(
                    emailTextFieldViewState = loginFlowResult.emailValidationResult.toViewState(
                        oldState = it.emailTextFieldViewState,
                        errorMessage = application.getString(R.string.email_validation_error_message)
                    ),
                    passwordTextFieldViewState = loginFlowResult.passwordValidationResult.toViewState(
                        oldState = it.passwordTextFieldViewState,
                        errorMessage = application.getString(R.string.password_validation_error_message)
                    ),
                    showLoadingDialog = false,
                    alertDialogViewState = loginFlowResult.toAlertDialogViewState()
                )
            }

            if (loginFlowResult is LoginFlowResult.Success) {
                _viewEffect.emit(LoginViewEffect.NavigateToHabitListScreen)
            }
        }
    }

    private fun ValidationResult.toViewState(
        oldState: LoginTextFieldViewState,
        errorMessage: String
    ) = oldState.copy(
        isError = when (this) {
            ValidationResult.Valid -> false
            ValidationResult.Invalid -> true
        },
        errorMessage = when (this) {
            ValidationResult.Valid -> ""
            ValidationResult.Invalid -> errorMessage
        }
    )

    private fun LoginFlowResult.toAlertDialogViewState() = when (this) {
        is LoginFlowResult.Success, is LoginFlowResult.ValidationError -> AlertDialogViewState()

        is LoginFlowResult.LoginServiceGenericError,
        is LoginFlowResult.NullTokenError,
        is LoginFlowResult.SaveAuthenticationTokenError -> AlertDialogViewState(showDialog = true)

        is LoginFlowResult.IncorrectLoginCredentialsError -> AlertDialogViewState(
            showDialog = true,
            message = application.getString(R.string.login_credentials_incorrect_error_message)
        )
    }
}
