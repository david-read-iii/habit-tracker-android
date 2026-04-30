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

class RemoteKeyDaoTest {

    private lateinit var database: HabitTrackerDatabase
    private lateinit var dao: RemoteKeyDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            HabitTrackerDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.remoteKeyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun test_insertAndGetRemoteKey() = runTest {
        val remoteKey = RemoteKeyEntity("1", null, 2)
        dao.insertAll(listOf(remoteKey))

        val result = dao.remoteKeysHabitId("1")
        Assert.assertEquals(remoteKey, result)
    }

    @Test
    fun test_replaceOnConflict() = runTest {
        val remoteKey1 = RemoteKeyEntity("1", null, 2)
        val remoteKey2 = RemoteKeyEntity("1", 2, 3)
        dao.insertAll(listOf(remoteKey1))
        dao.insertAll(listOf(remoteKey2))

        val result = dao.remoteKeysHabitId("1")
        Assert.assertEquals(remoteKey2, result)
    }

    @Test
    fun test_clearRemoteKeys() = runTest {
        val remoteKeys = listOf(
            RemoteKeyEntity("1", null, 2),
            RemoteKeyEntity("2", 1, 3)
        )
        dao.insertAll(remoteKeys)
        dao.clearRemoteKeys()

        val result1 = dao.remoteKeysHabitId("1")
        val result2 = dao.remoteKeysHabitId("2")
        Assert.assertNull(result1)
        Assert.assertNull(result2)
    }
}
