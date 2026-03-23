package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.ValidationResult
import org.junit.Assert
import org.junit.Test

class ValidateHabitNameUseCaseTest {

    private val validateHabitNameUseCase = ValidateHabitNameUseCase()

    @Test
    fun test_invoke_valid() {
        Assert.assertEquals(
            ValidationResult.Valid,
            validateHabitNameUseCase(name = "Exercise")
        )
    }

    @Test
    fun test_invoke_invalid() {
        Assert.assertEquals(
            ValidationResult.Invalid,
            validateHabitNameUseCase(name = "")
        )
    }

    @Test
    fun test_invoke_blank() {
        Assert.assertEquals(
            ValidationResult.Invalid,
            validateHabitNameUseCase(name = "   ")
        )
    }
}
