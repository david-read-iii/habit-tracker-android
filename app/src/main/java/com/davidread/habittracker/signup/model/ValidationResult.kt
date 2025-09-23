package com.davidread.habittracker.signup.model

// TODO: Move to common and use on Login screen too!
sealed class ValidationResult {
    object Valid : ValidationResult()
    object Invalid : ValidationResult()
}
