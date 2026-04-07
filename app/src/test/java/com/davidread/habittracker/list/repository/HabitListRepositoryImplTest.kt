package com.davidread.habittracker.list.repository

import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.list.database.HabitDao
import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResponse
import com.davidread.habittracker.list.model.CreateHabitResponse
import com.davidread.habittracker.list.model.DeleteHabitResponse
import com.davidread.habittracker.list.model.UpdateHabitResponse
import com.davidread.habittracker.list.service.HabitListService
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class HabitListRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val habitTrackerDatabase = mockk<HabitTrackerDatabase>()
    private val habitListService = mockk<HabitListService>()

    private val habitListRepository =
        HabitListRepositoryImpl(habitTrackerDatabase, habitListService)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_getHabits_returnsFlow() {
        val habitDao = mockk<HabitDao>()
        every { habitTrackerDatabase.habitDao() } returns habitDao
        every { habitDao.pagingSource() } returns mockk()

        val result = habitListRepository.getHabits()

        Assert.assertNotNull(result)
    }

    @Test
    fun test_createHabit_success() = runTest {
        val createHabitResponse = mockk<CreateHabitResponse>()
        coEvery { habitListService.createHabit(any()) } returns createHabitResponse

        Assert.assertEquals(
            Result.Success(createHabitResponse),
            habitListRepository.createHabit(mockk())
        )
    }

    @Test
    fun test_createHabit_error() = runTest {
        val exception = mockk<Exception>()
        coEvery { habitListService.createHabit(any()) } throws exception

        Assert.assertEquals(Result.Error(exception), habitListRepository.createHabit(mockk()))
    }

    @Test
    fun test_deleteHabit_success() = runTest {
        val deleteHabitResponse = mockk<DeleteHabitResponse>()
        coEvery { habitListService.deleteHabit(any()) } returns deleteHabitResponse

        Assert.assertEquals(
            Result.Success(deleteHabitResponse),
            habitListRepository.deleteHabit("1")
        )
    }

    @Test
    fun test_deleteHabit_error() = runTest {
        val exception = mockk<Exception>()
        coEvery { habitListService.deleteHabit(any()) } throws exception

        Assert.assertEquals(Result.Error(exception), habitListRepository.deleteHabit("1"))
    }

    @Test
    fun test_updateHabit_success() = runTest {
        val updateHabitResponse = mockk<UpdateHabitResponse>()
        coEvery { habitListService.updateHabit(any(), any()) } returns updateHabitResponse

        Assert.assertEquals(
            Result.Success(updateHabitResponse),
            habitListRepository.updateHabit("1", mockk())
        )
    }

    @Test
    fun test_updateHabit_error() = runTest {
        val exception = mockk<Exception>()
        coEvery { habitListService.updateHabit(any(), any()) } throws exception

        Assert.assertEquals(Result.Error(exception), habitListRepository.updateHabit("1", mockk()))
    }

    @Test
    fun test_checkIn_success() = runTest {
        val checkInRequest = CheckInRequest("1")
        val checkInResponse = mockk<CheckInResponse>()
        val habitDao = mockk<HabitDao>()
        every { habitTrackerDatabase.habitDao() } returns habitDao
        coEvery { habitListService.checkIn(checkInRequest) } returns checkInResponse
        coEvery { habitDao.incrementStreak("1") } returns Unit

        val result = habitListRepository.checkIn(checkInRequest)

        Assert.assertEquals(Result.Success(checkInResponse), result)
        coVerify {
            habitListService.checkIn(checkInRequest)
            habitDao.incrementStreak("1")
        }
    }

    @Test
    fun test_checkIn_serviceError() = runTest {
        val checkInRequest = CheckInRequest("1")
        val exception = Exception()
        coEvery { habitListService.checkIn(checkInRequest) } throws exception

        val result = habitListRepository.checkIn(checkInRequest)

        Assert.assertEquals(Result.Error(exception), result)
        verify(exactly = 0) { habitTrackerDatabase.habitDao() }
    }

    @Test
    fun test_checkIn_databaseError() = runTest {
        val checkInRequest = CheckInRequest("1")
        val checkInResponse = mockk<CheckInResponse>()
        val habitDao = mockk<HabitDao>()
        val exception = Exception()
        every { habitTrackerDatabase.habitDao() } returns habitDao
        coEvery { habitListService.checkIn(checkInRequest) } returns checkInResponse
        coEvery { habitDao.incrementStreak("1") } throws exception

        val result = habitListRepository.checkIn(checkInRequest)

        Assert.assertEquals(Result.Error(exception), result)
    }
}
