package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.UpdateHabitResult
import com.davidread.habittracker.list.repository.HabitListRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test

class UpdateHabitUseCaseTest {

    private val validateHabitNameUseCase = mockk<ValidateHabitNameUseCase>()
    private val habitListRepository = mockk<HabitListRepository>()
    private val logger = mockk<Logger>()
    private val updateHabitUseCase = UpdateHabitUseCase(
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
        coEvery { habitListRepository.updateHabit(any(), any()) } returns Result.Success(mockk())

        val result = updateHabitUseCase(id = "1", name = "New Name")

        Assert.assertEquals(UpdateHabitResult.Success, result)
    }

    @Test
    fun test_invoke_invalid_name() = runTest {
        every { validateHabitNameUseCase(any()) } returns ValidationResult.Invalid

        val result = updateHabitUseCase(id = "1", name = "")

        Assert.assertEquals(UpdateHabitResult.InvalidHabitName, result)
    }

    @Test
    fun test_invoke_error() = runTest {
        every { validateHabitNameUseCase(any()) } returns ValidationResult.Valid
        coEvery { habitListRepository.updateHabit(any(), any()) } returns Result.Error(Exception())
        every { logger.e(any(), any(), any()) } returns Unit

        val result = updateHabitUseCase(id = "1", name = "New Name")

        Assert.assertEquals(UpdateHabitResult.Error, result)
    }

    @Test
    fun test_invoke_null_id_returnsError_andSkipsValidationAndRepository() = runTest {
        every { logger.e(any(), any(), any()) } returns Unit

        val result = updateHabitUseCase(id = null, name = "New Name")

        Assert.assertEquals(UpdateHabitResult.Error, result)
        verify(exactly = 1) { logger.e(any(), any(), any()) }
        verify(exactly = 0) { validateHabitNameUseCase(any()) }
        coVerify(exactly = 0) { habitListRepository.updateHabit(any(), any()) }
    }
}
