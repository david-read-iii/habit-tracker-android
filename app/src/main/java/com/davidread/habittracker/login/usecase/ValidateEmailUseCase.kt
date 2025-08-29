package com.davidread.habittracker.login.usecase

import android.app.Application
import android.util.Patterns
import com.davidread.habittracker.R
import com.davidread.habittracker.login.model.EmailTextFieldViewState
import com.davidread.habittracker.login.model.EmailValidationResult
import com.davidread.habittracker.login.model.EmailValidationResult.Status
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(private val application: Application) {

    operator fun invoke(viewState: EmailTextFieldViewState): EmailValidationResult {
        return if (Patterns.EMAIL_ADDRESS.matcher(viewState.value).matches()) {
            EmailValidationResult(
                status = Status.VALID,
                emailTextFieldViewState = viewState.copy(
                    isError = false,
                    errorMessage = ""
                )
            )
        } else {
            EmailValidationResult(
                status = Status.INVALID,
                emailTextFieldViewState = viewState.copy(
                    isError = true,
                    errorMessage = application.getString(R.string.email_validation_error_message)
                )
            )
        }
    }
}
