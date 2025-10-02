package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.common.model.ValidationResult
import org.junit.Assert
import org.junit.Test

class ValidateConfirmPasswordUseCaseTest {

    private val validateConfirmPasswordUseCase = ValidateConfirmPasswordUseCase()

    @Test
    fun test_invoke_valid() {
        Assert.assertEquals(
            ValidationResult.Valid,
            validateConfirmPasswordUseCase(
                password = "password123",
                confirmPassword = "password123"
            )
        )
    }

    @Test
    fun test_invoke_invalid() {
        Assert.assertEquals(
            ValidationResult.Invalid,
            validateConfirmPasswordUseCase(
                password = "password123",
                confirmPassword = "password that does not match"
            )
        )
    }
}
