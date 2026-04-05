package com.davidread.habittracker.list.mapper

import com.davidread.habittracker.list.database.HabitEntity
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HabitMapperTest {

    private val createdAtMapper = mockk<CreatedAtMapper>()
    private lateinit var habitMapper: HabitMapper

    @Before
    fun setUp() {
        habitMapper = HabitMapper(createdAtMapper)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_map_returnsCorrectHabitViewState() {
        val entity = HabitEntity(
            id = "id",
            name = "name",
            streak = 5,
            createdAt = "2023-10-27T10:00:00Z"
        )
        val expectedCreatedAt = "5 mins ago"
        every { createdAtMapper.map(entity.createdAt) } returns expectedCreatedAt

        val result = habitMapper.map(entity)

        Assert.assertEquals(entity.id, result.id)
        Assert.assertEquals(entity.name, result.name)
        Assert.assertEquals("5", result.streak)
        Assert.assertEquals(expectedCreatedAt, result.createdAt)
        verify { createdAtMapper.map(entity.createdAt) }
    }

    @Test
    fun test_map_returnsCorrectHabitViewState_whenStreakIsZero() {
        val entity = HabitEntity(
            id = "id_2",
            name = "another name",
            streak = 0,
            createdAt = "2023-10-27T11:00:00Z"
        )
        val expectedCreatedAt = "just now"
        every { createdAtMapper.map(entity.createdAt) } returns expectedCreatedAt

        val result = habitMapper.map(entity)

        Assert.assertEquals(entity.id, result.id)
        Assert.assertEquals(entity.name, result.name)
        Assert.assertEquals("0", result.streak)
        Assert.assertEquals(expectedCreatedAt, result.createdAt)
        verify { createdAtMapper.map(entity.createdAt) }
    }
}
