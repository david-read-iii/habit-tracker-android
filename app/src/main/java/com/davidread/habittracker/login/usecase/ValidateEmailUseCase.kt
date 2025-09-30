package com.davidread.habittracker.login.usecase

import android.app.Application
import com.davidread.habittracker.R
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult.Status
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import javax.inject.Inject

@Deprecated("Use common ValidateEmailUseCase")
class ValidateEmailUseCase @Inject constructor(private val application: Application) {

    private val emailRegex = Regex(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    operator fun invoke(viewState: LoginTextFieldViewState): LoginTextFieldValidationResult {
        return if (emailRegex.matches(viewState.value)) {
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
