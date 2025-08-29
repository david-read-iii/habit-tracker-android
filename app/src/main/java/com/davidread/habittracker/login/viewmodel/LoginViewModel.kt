package com.davidread.habittracker.login.viewmodel

import androidx.lifecycle.ViewModel
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.usecase.ValidateEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val validateEmailUseCase: ValidateEmailUseCase) :
    ViewModel() {

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState>
        get() = _viewState

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

        is LoginViewIntent.ClickLoginButton -> {
            val emailValidationResult = validateEmailUseCase(viewState.value.emailTextFieldViewState)
            _viewState.update {
                it.copy(
                    emailTextFieldViewState = emailValidationResult.emailTextFieldViewState
                )
            }

            // TODO: Attempt login if email is valid.
        }

        is LoginViewIntent.ClickSignUpLink -> {
            // TODO: Navigate to sign up screen.
        }
    }
}
