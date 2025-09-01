package com.davidread.habittracker.login.usecase

import android.app.Application
import com.davidread.habittracker.R
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult.Status
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor(private val application: Application) {

    operator fun invoke(viewState: LoginTextFieldViewState): LoginTextFieldValidationResult {
        return if (viewState.value.length >= 8) {
            LoginTextFieldValidationResult(
                status = Status.VALID,
                loginTextFieldViewState = viewState.copy(
                    isError = false,
                    errorMessage = ""
                )
            )
        } else {
            LoginTextFieldValidationResult(
                status = Status.INVALID,
                loginTextFieldViewState = viewState.copy(
                    isError = true,
                    errorMessage = application.getString(R.string.password_validation_error_message)
                )
            )
        }
    }
}
