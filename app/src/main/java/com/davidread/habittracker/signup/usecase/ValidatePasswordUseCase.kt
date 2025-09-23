package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.signup.model.ValidationResult
import javax.inject.Inject

// TODO: Move to common and use on Login screen too!
class ValidatePasswordUseCase @Inject constructor() {

    operator fun invoke(password: String) = if (password.length >= 8) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid
    }
}
