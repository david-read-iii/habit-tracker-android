package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.ValidationResult
import javax.inject.Inject

class ValidateHabitNameUseCase @Inject constructor() {

    operator fun invoke(name: String) = if (name.isNotBlank()) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid
    }
}
