package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.common.model.ValidationResult
import javax.inject.Inject

class ValidateConfirmPasswordUseCase @Inject constructor() {

    operator fun invoke(password: String, confirmPassword: String) =
        if (password == confirmPassword) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid
        }
}
