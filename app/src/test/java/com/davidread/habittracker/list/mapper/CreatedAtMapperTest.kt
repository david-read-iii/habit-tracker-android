package com.davidread.habittracker.list.mapper

import android.app.Application
import android.content.res.Resources
import com.davidread.habittracker.R
import com.davidread.habittracker.common.util.Logger
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

class CreatedAtMapperTest {

    private val application = mockk<Application>()
    private val resources = mockk<Resources>()
    private val logger = mockk<Logger>()
    private val clock = mockk<Clock>()
    private lateinit var createdAtMapper: CreatedAtMapper

    private val now = Instant.parse("2023-10-27T10:00:00Z")

    @Before
    fun setUp() {
        every { clock.instant() } returns now
        every { clock.zone } returns ZoneId.of("UTC")
        every { application.resources } returns resources
        createdAtMapper = CreatedAtMapper(application, logger, clock, Locale.US)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_map_returnsJustNow_whenDurationIsLessThan1Minute() {
        val createdAt = "2023-10-27T09:59:30Z"
        every { application.getString(R.string.created_at_just_now) } returns "Just now"

        val result = createdAtMapper.map(createdAt)

        Assert.assertEquals("Just now", result)
    }

    @Test
    fun test_map_returnsMinsAgo_whenDurationIsLessThan1Hour() {
        val createdAt = "2023-10-27T09:55:00Z"
        every {
            resources.getQuantityString(R.plurals.created_at_mins_ago, 5, 5)
        } returns "5 mins ago"

        val result = createdAtMapper.map(createdAt)

        Assert.assertEquals("5 mins ago", result)
    }

    @Test
    fun test_map_returnsHoursAgo_whenDurationIsLessThan1Day() {
        val createdAt = "2023-10-27T07:00:00Z"
        every {
            resources.getQuantityString(R.plurals.created_at_hours_ago, 3, 3)
        } returns "3 hours ago"

        val result = createdAtMapper.map(createdAt)

        Assert.assertEquals("3 hours ago", result)
    }

    @Test
    fun test_map_returnsDaysAgo_whenDurationIsLessThan1Year() {
        val createdAt = "2023-10-20T10:00:00Z"
        every {
            resources.getQuantityString(R.plurals.created_at_days_ago, 7, 7)
        } returns "7 days ago"

        val result = createdAtMapper.map(createdAt)

        Assert.assertEquals("7 days ago", result)
    }

    @Test
    fun test_map_returnsFormattedDate_whenDurationIsMoreThan1Year() {
        val createdAt = "2022-10-20T10:00:00Z"

        val result = createdAtMapper.map(createdAt)

        Assert.assertEquals("Oct 20, 2022", result)
    }

    @Test
    fun test_map_returnsOriginalString_whenParsingFails() {
        val invalidCreatedAt = "invalid-date"
        every { logger.e(any(), any(), any()) } returns Unit

        val result = createdAtMapper.map(invalidCreatedAt)

        Assert.assertEquals(invalidCreatedAt, result)
        verify { logger.e(any(), any(), any()) }
    }
}
