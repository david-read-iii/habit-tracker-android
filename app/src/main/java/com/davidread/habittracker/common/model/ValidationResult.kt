package com.davidread.habittracker.common.model

// TODO: Use on Login screen too!
sealed class ValidationResult {
    object Valid : ValidationResult()
    object Invalid : ValidationResult()
}
