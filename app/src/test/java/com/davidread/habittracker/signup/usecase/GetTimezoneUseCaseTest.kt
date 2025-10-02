package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.signup.model.GetTimezoneResult
import com.davidread.habittracker.signup.util.TimezoneProvider
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.DateTimeException
import java.time.zone.ZoneRulesException

class GetTimezoneUseCaseTest {

    private val timezoneProvider = mockk<TimezoneProvider>()

    private val logger = mockk<Logger>()

    private val getTimezoneUseCase = GetTimezoneUseCase(timezoneProvider, logger)

    @Before
    fun setUp() {
        every { logger.e(any(), any(), any()) } just runs
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() {
        val id = "America/New_York"
        every { timezoneProvider.getSystemDefaultZoneId() } returns mockk {
            every { this@mockk.id } returns id
        }

        Assert.assertEquals(GetTimezoneResult.Success(timezone = id), getTimezoneUseCase())
    }

    @Test
    fun test_invoke_dateTimeException() {
        every { timezoneProvider.getSystemDefaultZoneId() } throws DateTimeException("")

        Assert.assertEquals(GetTimezoneResult.Error, getTimezoneUseCase())
    }

    @Test
    fun test_invoke_zoneRulesException() {
        every { timezoneProvider.getSystemDefaultZoneId() } throws ZoneRulesException("")

        Assert.assertEquals(GetTimezoneResult.Error, getTimezoneUseCase())
    }
}
