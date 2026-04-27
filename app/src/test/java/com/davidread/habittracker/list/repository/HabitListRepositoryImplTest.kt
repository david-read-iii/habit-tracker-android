package com.davidread.habittracker.list.repository

import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.list.database.HabitDao
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResponse
import com.davidread.habittracker.list.model.CreateHabitRequest
import com.davidread.habittracker.list.model.CreateHabitResponse
import com.davidread.habittracker.list.model.DeleteHabitResponse
import com.davidread.habittracker.list.model.HabitDto
import com.davidread.habittracker.list.model.UpdateHabitRequest
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
        val createHabitRequest = CreateHabitRequest("Exercise")
        val createHabitResponse = CreateHabitResponse(
            message = "Habit created",
            habit = HabitDto("1", "Exercise", 0, "2023-10-27T10:00:00Z")
        )
        val expectedHabitEntity = HabitEntity("1", "Exercise", 0, "2023-10-27T10:00:00Z")
        val habitDao = mockk<HabitDao>()
        every { habitTrackerDatabase.habitDao() } returns habitDao
        coEvery { habitListService.createHabit(createHabitRequest) } returns createHabitResponse
        coEvery { habitDao.insertAll(listOf(expectedHabitEntity)) } returns Unit

        Assert.assertEquals(
            Result.Success(createHabitResponse),
            habitListRepository.createHabit(createHabitRequest)
        )
        coVerify {
            habitListService.createHabit(createHabitRequest)
            habitDao.insertAll(listOf(expectedHabitEntity))
        }
    }

    @Test
    fun test_createHabit_successWithoutHabitPayload_doesNotInsertIntoDatabase() = runTest {
        val createHabitRequest = CreateHabitRequest("Exercise")
        val createHabitResponse = CreateHabitResponse(
            message = "Habit created",
            habit = null
        )
        coEvery { habitListService.createHabit(createHabitRequest) } returns createHabitResponse

        Assert.assertEquals(
            Result.Success(createHabitResponse),
            habitListRepository.createHabit(createHabitRequest)
        )
        verify(exactly = 0) { habitTrackerDatabase.habitDao() }
    }

    @Test
    fun test_createHabit_error() = runTest {
        val exception = mockk<Exception>()
        coEvery { habitListService.createHabit(any()) } throws exception

        Assert.assertEquals(Result.Error(exception), habitListRepository.createHabit(mockk()))
        verify(exactly = 0) { habitTrackerDatabase.habitDao() }
    }

    @Test
    fun test_deleteHabit_success() = runTest {
        val habitDao = mockk<HabitDao>()
        val deleteHabitResponse = mockk<DeleteHabitResponse>()
        every { habitTrackerDatabase.habitDao() } returns habitDao
        coEvery { habitListService.deleteHabit("1") } returns deleteHabitResponse
        coEvery { habitDao.deleteHabit("1") } returns Unit

        Assert.assertEquals(
            Result.Success(deleteHabitResponse),
            habitListRepository.deleteHabit("1")
        )
        coVerify {
            habitListService.deleteHabit("1")
            habitDao.deleteHabit("1")
        }
    }

    @Test
    fun test_deleteHabit_error() = runTest {
        val exception = mockk<Exception>()
        coEvery { habitListService.deleteHabit("1") } throws exception

        Assert.assertEquals(Result.Error(exception), habitListRepository.deleteHabit("1"))
        verify(exactly = 0) { habitTrackerDatabase.habitDao() }
    }

    @Test
    fun test_deleteHabit_databaseError() = runTest {
        val habitDao = mockk<HabitDao>()
        val deleteHabitResponse = mockk<DeleteHabitResponse>()
        val exception = mockk<Exception>()
        every { habitTrackerDatabase.habitDao() } returns habitDao
        coEvery { habitListService.deleteHabit("1") } returns deleteHabitResponse
        coEvery { habitDao.deleteHabit("1") } throws exception

        Assert.assertEquals(Result.Error(exception), habitListRepository.deleteHabit("1"))
        coVerify {
            habitListService.deleteHabit("1")
            habitDao.deleteHabit("1")
        }
    }

    @Test
    fun test_updateHabit_success() = runTest {
        val updateHabitRequest = UpdateHabitRequest("Exercise Updated")
        val updateHabitResponse = mockk<UpdateHabitResponse>()
        val habitDao = mockk<HabitDao>()
        every { habitTrackerDatabase.habitDao() } returns habitDao
        coEvery { habitListService.updateHabit("1", updateHabitRequest) } returns updateHabitResponse
        coEvery { habitDao.updateHabitName("1", "Exercise Updated") } returns Unit

        Assert.assertEquals(
            Result.Success(updateHabitResponse),
            habitListRepository.updateHabit("1", updateHabitRequest)
        )
        coVerify {
            habitListService.updateHabit("1", updateHabitRequest)
            habitDao.updateHabitName("1", "Exercise Updated")
        }
    }

    @Test
    fun test_updateHabit_error() = runTest {
        val updateHabitRequest = UpdateHabitRequest("Exercise Updated")
        val exception = mockk<Exception>()
        coEvery { habitListService.updateHabit("1", updateHabitRequest) } throws exception

        Assert.assertEquals(Result.Error(exception), habitListRepository.updateHabit("1", updateHabitRequest))
        verify(exactly = 0) { habitTrackerDatabase.habitDao() }
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
