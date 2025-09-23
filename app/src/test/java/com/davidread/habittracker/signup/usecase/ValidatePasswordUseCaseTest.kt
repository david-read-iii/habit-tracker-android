package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.signup.model.ValidationResult
import org.junit.Assert
import org.junit.Test

class ValidatePasswordUseCaseTest {

    private val validatePasswordUseCase = ValidatePasswordUseCase()

    @Test
    fun test_invoke_valid() {
        Assert.assertEquals(
            ValidationResult.Valid,
            validatePasswordUseCase(password = "password123")
        )
    }

    @Test
    fun test_invoke_invalid() {
        Assert.assertEquals(
            ValidationResult.Invalid,
            validatePasswordUseCase(password = "short")
        )
    }
}
