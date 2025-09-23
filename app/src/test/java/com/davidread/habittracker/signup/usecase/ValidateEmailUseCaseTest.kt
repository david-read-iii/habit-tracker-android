package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.signup.model.ValidationResult
import org.junit.Assert
import org.junit.Test

class ValidateEmailUseCaseTest {

    private val validateEmailUseCase = ValidateEmailUseCase()

    @Test
    fun test_invoke_valid() {
        Assert.assertEquals(
            ValidationResult.Valid,
            validateEmailUseCase(email = "david.read@gmail.com")
        )
    }

    @Test
    fun test_invoke_invalid() {
        Assert.assertEquals(
            ValidationResult.Invalid,
            validateEmailUseCase(email = "some invalid email")
        )
    }
}
