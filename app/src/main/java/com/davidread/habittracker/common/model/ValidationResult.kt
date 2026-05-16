package com.davidread.habittracker.common.model

sealed class ValidationResult {
    object Valid : ValidationResult()
    object Invalid : ValidationResult()
}
