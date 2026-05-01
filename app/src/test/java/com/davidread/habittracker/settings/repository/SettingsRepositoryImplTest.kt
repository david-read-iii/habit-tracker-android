package com.davidread.habittracker.settings.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.settings.model.ResetTimezoneRequest
import com.davidread.habittracker.settings.model.ResetTimezoneResponse
import com.davidread.habittracker.settings.service.SettingsService
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class SettingsRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsService = mockk<SettingsService>()

    private val settingsRepository = SettingsRepositoryImpl(settingsService)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_resetTimezone_success() = runTest {
        val response = ResetTimezoneResponse(message = "Timezone updated successfully")
        val request = ResetTimezoneRequest(timezone = "America/New_York")
        coEvery { settingsService.resetTimezone(any()) } returns response

        Assert.assertEquals(Result.Success(response), settingsRepository.resetTimezone(request))
    }

    @Test
    fun test_resetTimezone_error() = runTest {
        val request = ResetTimezoneRequest(timezone = "America/New_York")
        val exception = mockk<Exception>()
        coEvery { settingsService.resetTimezone(any()) } throws exception

        Assert.assertEquals(Result.Error(exception), settingsRepository.resetTimezone(request))
    }
}

