package com.davidread.habittracker.login.usecase

import android.app.Application
import android.util.Patterns
import com.davidread.habittracker.R
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult.Status
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(private val application: Application) {

    operator fun invoke(viewState: LoginTextFieldViewState): LoginTextFieldValidationResult {
        return if (Patterns.EMAIL_ADDRESS.matcher(viewState.value).matches()) {
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
                    errorMessage = application.getString(R.string.email_validation_error_message)
                )
            )
        }
    }
}
