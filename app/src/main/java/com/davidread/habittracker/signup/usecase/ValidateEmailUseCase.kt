package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.signup.model.ValidationResult
import javax.inject.Inject

// TODO: Move to common and use on Login screen too!
class ValidateEmailUseCase @Inject constructor() {

    private val emailRegex = Regex(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    operator fun invoke(email: String) = if (emailRegex.matches(email)) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid
    }
}
