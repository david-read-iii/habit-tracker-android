package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.DeleteHabitResult
import com.davidread.habittracker.list.repository.HabitListRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test

class DeleteHabitUseCaseTest {

    private val habitListRepository = mockk<HabitListRepository>()
    private val logger = mockk<Logger>()
    private val deleteHabitUseCase = DeleteHabitUseCase(habitListRepository, logger)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        coEvery { habitListRepository.deleteHabit(any()) } returns Result.Success(mockk())

        val result = deleteHabitUseCase(id = "1")

        Assert.assertEquals(DeleteHabitResult.Success, result)
    }

    @Test
    fun test_invoke_error() = runTest {
        coEvery { habitListRepository.deleteHabit(any()) } returns Result.Error(Exception())
        every { logger.e(any(), any(), any()) } returns Unit

        val result = deleteHabitUseCase(id = "1")

        Assert.assertEquals(DeleteHabitResult.Error, result)
    }
}
