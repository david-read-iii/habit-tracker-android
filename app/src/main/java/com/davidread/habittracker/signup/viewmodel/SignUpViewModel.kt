package com.davidread.habittracker.signup.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidread.habittracker.R
import com.davidread.habittracker.signup.model.AlertDialogViewState
import com.davidread.habittracker.signup.model.SignUpFlowResult
import com.davidread.habittracker.signup.model.SignUpTextFieldViewState
import com.davidread.habittracker.signup.model.SignUpViewEffect
import com.davidread.habittracker.signup.model.SignUpViewIntent
import com.davidread.habittracker.signup.model.SignUpViewState
import com.davidread.habittracker.signup.model.ValidationResult
import com.davidread.habittracker.signup.usecase.SignUpFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpFlowUseCase: SignUpFlowUseCase,
    private val application: Application
) : ViewModel() {

    private val _viewState = MutableStateFlow(SignUpViewState())
    val viewState: StateFlow<SignUpViewState>
        get() = _viewState

    private val _viewEffect = MutableSharedFlow<SignUpViewEffect>()
    val viewEffect: SharedFlow<SignUpViewEffect>
        get() = _viewEffect

    fun processIntent(intent: SignUpViewIntent) = when (intent) {
        is SignUpViewIntent.ChangeEmailValue -> {
            _viewState.update {
                it.copy(
                    emailTextFieldViewState = it.emailTextFieldViewState.copy(
                        value = intent.newValue
                    )
                )
            }
        }

        is SignUpViewIntent.ChangePasswordValue -> {
            _viewState.update {
                it.copy(
                    passwordTextFieldViewState = it.passwordTextFieldViewState.copy(
                        value = intent.newValue
                    )
                )
            }
        }

        is SignUpViewIntent.ChangeConfirmPasswordValue -> {
            _viewState.update {
                it.copy(
                    confirmPasswordTextFieldViewState = it.confirmPasswordTextFieldViewState.copy(
                        value = intent.newValue
                    )
                )
            }
        }

        is SignUpViewIntent.ClickSignUpButton -> handleClickSignUpButton()

        is SignUpViewIntent.ClickAlertDialogButton -> {
            _viewState.update {
                it.copy(alertDialogViewState = AlertDialogViewState(showDialog = false))
            }
        }
    }

    private fun handleClickSignUpButton() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(showLoadingDialog = true)
            }

            val signUpFlowResult = signUpFlowUseCase(
                _viewState.value.emailTextFieldViewState.value,
                _viewState.value.passwordTextFieldViewState.value,
                _viewState.value.confirmPasswordTextFieldViewState.value
            )

            _viewState.update {
                it.copy(
                    emailTextFieldViewState = signUpFlowResult.emailValidationResult.toViewState(
                        oldState = it.emailTextFieldViewState,
                        errorMessage = application.getString(R.string.email_validation_error_message)
                    ),
                    passwordTextFieldViewState = signUpFlowResult.passwordValidationResult.toViewState(
                        oldState = it.passwordTextFieldViewState,
                        errorMessage = application.getString(R.string.password_validation_error_message)
                    ),
                    confirmPasswordTextFieldViewState = signUpFlowResult.confirmPasswordValidationResult.toViewState(
                        oldState = it.confirmPasswordTextFieldViewState,
                        errorMessage = application.getString(R.string.confirm_password_validation_error_message)
                    ),
                    showLoadingDialog = false,
                    alertDialogViewState = signUpFlowResult.toAlertDialogViewState()
                )
            }

            if (signUpFlowResult is SignUpFlowResult.Success) {
                _viewEffect.emit(SignUpViewEffect.NavigateToHabitListScreen)
            }
        }
    }

    private fun ValidationResult.toViewState(
        oldState: SignUpTextFieldViewState,
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

    private fun SignUpFlowResult.toAlertDialogViewState() = when (this) {
        is SignUpFlowResult.Success, is SignUpFlowResult.ValidationError -> AlertDialogViewState()

        is SignUpFlowResult.GetTimezoneError,
        is SignUpFlowResult.SignUpServiceGenericError,
        is SignUpFlowResult.NullTokenError,
        is SignUpFlowResult.SaveAuthenticationTokenError -> AlertDialogViewState(showDialog = true)

        is SignUpFlowResult.EmailAlreadyUsedError -> AlertDialogViewState(
            showDialog = true,
            message = application.getString(R.string.email_already_used_error_message)
        )
    }
}
