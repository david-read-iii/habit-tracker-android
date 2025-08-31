package com.davidread.habittracker.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.usecase.LoginUseCase
import com.davidread.habittracker.login.usecase.ValidateEmailUseCase
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
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val loginUseCase: LoginUseCase
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

        is LoginViewIntent.ClickLoginButton -> {
            val emailValidationResult =
                validateEmailUseCase(_viewState.value.emailTextFieldViewState)
            _viewState.update {
                it.copy(
                    emailTextFieldViewState = emailValidationResult.emailTextFieldViewState
                )
            }

            viewModelScope.launch {
                val loginResult = loginUseCase(
                    loginRequest = LoginRequest(
                        email = _viewState.value.emailTextFieldViewState.value,
                        password = _viewState.value.passwordTextFieldViewState.value
                    )
                )

                if (loginResult.navigateToListScreen) {
                    _viewEffect.emit(LoginViewEffect.NavigateToListScreen)
                } else if (loginResult.showErrorDialog) {
                    // TODO: Show error dialog on UI.
                }
            }
        }

        is LoginViewIntent.ClickSignUpLink -> {
            viewModelScope.launch {
                _viewEffect.emit(LoginViewEffect.NavigateToSignUpScreen)
            }
        }
    }
}
