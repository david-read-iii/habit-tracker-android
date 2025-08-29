package com.davidread.habittracker.login.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.davidread.habittracker.R
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val application: Application) : ViewModel() {

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
            val emailValue = viewState.value.emailTextFieldViewState.value
            val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()
            val (isError, errorMessage) = if (isEmailValid) {
                Pair(false, "")
            } else {
                Pair(true, application.getString(R.string.email_validation_error_message))
            }
            _viewState.update {
                it.copy(
                    emailTextFieldViewState = it.emailTextFieldViewState.copy(
                        isError = isError,
                        errorMessage = errorMessage
                    )
                )
            }

            // TODO: Attempt login if email is valid.
        }

        is LoginViewIntent.ClickSignUpLink -> {
            // TODO: Navigate to sign up screen.
        }
    }
}
