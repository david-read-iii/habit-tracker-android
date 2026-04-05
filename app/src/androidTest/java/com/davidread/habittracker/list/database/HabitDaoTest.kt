package com.davidread.habittracker.list.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.davidread.habittracker.common.database.HabitTrackerDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HabitDaoTest {

    private lateinit var database: HabitTrackerDatabase
    private lateinit var dao: HabitDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            HabitTrackerDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.habitDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun test_insertAndGetHabit() = runTest {
        val habit = HabitEntity("1", "Exercise", 0, "2023-10-27T10:00:00Z")
        dao.insertAll(listOf(habit))

        val result = dao.getHabitById("1")
        Assert.assertEquals(habit, result)
    }

    @Test
    fun test_incrementStreak() = runTest {
        val habit = HabitEntity("1", "Exercise", 0, "2023-10-27T10:00:00Z")
        dao.insertAll(listOf(habit))
        dao.incrementStreak("1")

        val result = dao.getHabitById("1")
        Assert.assertEquals(1, result?.streak)
    }

    @Test
    fun test_clearAll() = runTest {
        val habits = listOf(
            HabitEntity("1", "Exercise", 0, "2023-10-27T10:00:00Z"),
            HabitEntity("2", "Drink Water", 5, "2023-10-27T11:00:00Z")
        )
        dao.insertAll(habits)
        dao.clearAll()

        val result1 = dao.getHabitById("1")
        val result2 = dao.getHabitById("2")
        Assert.assertNull(result1)
        Assert.assertNull(result2)
    }
}
