package com.davidread.habittracker.common.usecase

import com.davidread.habittracker.common.model.ValidationResult
import javax.inject.Inject

// TODO: Use on Login screen too!
class ValidatePasswordUseCase @Inject constructor() {

    operator fun invoke(password: String) = if (password.length >= 8) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid
    }
}
