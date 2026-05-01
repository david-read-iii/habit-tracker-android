package com.davidread.habittracker.settings.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.list.database.HabitDao
import com.davidread.habittracker.list.database.RemoteKeyDao
import com.davidread.habittracker.list.repository.HabitListRepository
import com.davidread.habittracker.settings.model.ResetTimezoneRequest
import com.davidread.habittracker.settings.model.ResetTimezoneResponse
import com.davidread.habittracker.settings.model.ResetTimezoneResult
import com.davidread.habittracker.settings.repository.SettingsRepository
import com.davidread.habittracker.signup.model.GetTimezoneResult
import com.davidread.habittracker.signup.usecase.GetTimezoneUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ResetTimezoneUseCaseTest {

    private val settingsRepository = mockk<SettingsRepository>()
    private val getTimezoneUseCase = mockk<GetTimezoneUseCase>()
    private val database = mockk<HabitTrackerDatabase>()
    private val habitDao = mockk<HabitDao>()
    private val remoteKeyDao = mockk<RemoteKeyDao>()
    private val habitListRepository = mockk<HabitListRepository>()
    private val logger = mockk<Logger>()

    private val resetTimezoneUseCase =
        ResetTimezoneUseCase(
            settingsRepository,
            getTimezoneUseCase,
            database,
            habitListRepository,
            logger
        )

    @Before
    fun setUp() {
        every { logger.e(any(), any(), any()) } just runs
        every { database.habitDao() } returns habitDao
        every { database.remoteKeyDao() } returns remoteKeyDao
        every { habitListRepository.invalidateHabits() } returns Unit
        coEvery { habitDao.clearAll() } just runs
        coEvery { remoteKeyDao.clearRemoteKeys() } just runs
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        val timezone = "America/New_York"
        every { getTimezoneUseCase() } returns GetTimezoneResult.Success(timezone)
        coEvery { settingsRepository.resetTimezone(any()) } returns Result.Success(
            ResetTimezoneResponse("Timezone updated successfully")
        )

        Assert.assertEquals(ResetTimezoneResult.Success, resetTimezoneUseCase())
        coVerify(exactly = 1) {
            settingsRepository.resetTimezone(ResetTimezoneRequest(timezone))
        }
        coVerify(exactly = 1) { remoteKeyDao.clearRemoteKeys() }
        coVerify(exactly = 1) { habitDao.clearAll() }
        coVerify(exactly = 1) { habitListRepository.invalidateHabits() }
    }

    @Test
    fun test_invoke_getTimezoneError() = runTest {
        every { getTimezoneUseCase() } returns GetTimezoneResult.Error

        Assert.assertEquals(ResetTimezoneResult.Error, resetTimezoneUseCase())
        coVerify(exactly = 0) { settingsRepository.resetTimezone(any()) }
        coVerify(exactly = 0) { remoteKeyDao.clearRemoteKeys() }
        coVerify(exactly = 0) { habitDao.clearAll() }
        coVerify(exactly = 0) { habitListRepository.invalidateHabits() }
    }

    @Test
    fun test_invoke_serviceError() = runTest {
        every { getTimezoneUseCase() } returns GetTimezoneResult.Success("America/New_York")
        coEvery { settingsRepository.resetTimezone(any()) } returns Result.Error(mockk<Exception>())

        Assert.assertEquals(ResetTimezoneResult.Error, resetTimezoneUseCase())
        coVerify(exactly = 0) { remoteKeyDao.clearRemoteKeys() }
        coVerify(exactly = 0) { habitDao.clearAll() }
        coVerify(exactly = 0) { habitListRepository.invalidateHabits() }
    }

    @Test
    fun test_invoke_databaseClearError_stillReturnsSuccess() = runTest {
        val timezone = "America/New_York"
        every { getTimezoneUseCase() } returns GetTimezoneResult.Success(timezone)
        coEvery { settingsRepository.resetTimezone(any()) } returns Result.Success(
            ResetTimezoneResponse("Timezone updated successfully")
        )
        coEvery { remoteKeyDao.clearRemoteKeys() } throws Exception("DB error")

        Assert.assertEquals(ResetTimezoneResult.Success, resetTimezoneUseCase())
        coVerify(exactly = 0) { habitListRepository.invalidateHabits() }
    }
}
