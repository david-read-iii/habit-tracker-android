package com.davidread.habittracker.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {

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
            viewModelScope.launch {
                val loginResult = loginUseCase(viewState = _viewState.value)
                _viewState.update {
                    loginResult.viewState
                }

                if (loginResult.navigateToListScreen) {
                    _viewEffect.emit(LoginViewEffect.NavigateToListScreen)
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
