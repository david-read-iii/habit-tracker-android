package com.davidread.habittracker.common.usecase

import com.davidread.habittracker.common.model.ValidationResult
import javax.inject.Inject

// TODO: Use on Login screen too!
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
