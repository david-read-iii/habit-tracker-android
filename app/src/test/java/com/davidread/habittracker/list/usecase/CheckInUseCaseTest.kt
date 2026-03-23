package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.CheckInResult
import com.davidread.habittracker.list.repository.HabitListRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException

class CheckInUseCaseTest {

    private val habitListRepository = mockk<HabitListRepository>()
    private val logger = mockk<Logger>()
    private val checkInUseCase = CheckInUseCase(habitListRepository, logger)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        coEvery { habitListRepository.checkIn(any()) } returns Result.Success(mockk())

        val result = checkInUseCase(habitId = "1")

        Assert.assertEquals(CheckInResult.Success, result)
    }

    @Test
    fun test_invoke_already_checked_in_error() = runTest {
        val httpException = mockk<HttpException>()
        coEvery { httpException.code() } returns 400
        coEvery { habitListRepository.checkIn(any()) } returns Result.Error(httpException)
        every { logger.e(any(), any(), any()) } returns Unit

        val result = checkInUseCase(habitId = "1")

        Assert.assertEquals(CheckInResult.AlreadyCheckedInError, result)
    }

    @Test
    fun test_invoke_generic_error() = runTest {
        coEvery { habitListRepository.checkIn(any()) } returns Result.Error(Exception())
        every { logger.e(any(), any(), any()) } returns Unit

        val result = checkInUseCase(habitId = "1")

        Assert.assertEquals(CheckInResult.GenericError, result)
    }
}
