package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.CreateHabitResult
import com.davidread.habittracker.list.repository.HabitListRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test

class CreateHabitUseCaseTest {

    private val validateHabitNameUseCase = mockk<ValidateHabitNameUseCase>()
    private val habitListRepository = mockk<HabitListRepository>()
    private val logger = mockk<Logger>()
    private val createHabitUseCase = CreateHabitUseCase(
        validateHabitNameUseCase,
        habitListRepository,
        logger
    )

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        every { validateHabitNameUseCase(any()) } returns ValidationResult.Valid
        coEvery { habitListRepository.createHabit(any()) } returns Result.Success(mockk())

        val result = createHabitUseCase(name = "Exercise")

        Assert.assertEquals(CreateHabitResult.Success, result)
    }

    @Test
    fun test_invoke_invalid_name() = runTest {
        every { validateHabitNameUseCase(any()) } returns ValidationResult.Invalid

        val result = createHabitUseCase(name = "")

        Assert.assertEquals(CreateHabitResult.InvalidHabitName, result)
    }

    @Test
    fun test_invoke_error() = runTest {
        every { validateHabitNameUseCase(any()) } returns ValidationResult.Valid
        coEvery { habitListRepository.createHabit(any()) } returns Result.Error(Exception())
        every { logger.e(any(), any(), any()) } returns Unit

        val result = createHabitUseCase(name = "Exercise")

        Assert.assertEquals(CreateHabitResult.Error, result)
    }
}
