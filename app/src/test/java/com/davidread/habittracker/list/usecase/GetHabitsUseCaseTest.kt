package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.GetHabitsResult
import com.davidread.habittracker.list.model.HabitListResponse
import com.davidread.habittracker.list.repository.HabitListRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test

class GetHabitsUseCaseTest {

    private val habitListRepository = mockk<HabitListRepository>()
    private val logger = mockk<Logger>()
    private val getHabitsUseCase = GetHabitsUseCase(habitListRepository, logger)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        val habits = listOf(mockk<com.davidread.habittracker.list.model.HabitDto>())
        val response = HabitListResponse(habits = habits, nextPage = 2)
        coEvery { habitListRepository.getHabits(any(), any()) } returns Result.Success(response)

        val result = getHabitsUseCase(page = 1, limit = 10)

        Assert.assertEquals(GetHabitsResult.Success(habits, 2), result)
    }

    @Test
    fun test_invoke_null_habits_error() = runTest {
        val response = HabitListResponse(habits = null, nextPage = null)
        coEvery { habitListRepository.getHabits(any(), any()) } returns Result.Success(response)
        every { logger.e(any(), any(), any()) } returns Unit

        val result = getHabitsUseCase(page = 1, limit = 10)

        Assert.assertEquals(GetHabitsResult.Error, result)
    }

    @Test
    fun test_invoke_error() = runTest {
        coEvery { habitListRepository.getHabits(any(), any()) } returns Result.Error(Exception())
        every { logger.e(any(), any(), any()) } returns Unit

        val result = getHabitsUseCase(page = 1, limit = 10)

        Assert.assertEquals(GetHabitsResult.Error, result)
    }
}
