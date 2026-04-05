package com.davidread.habittracker.list.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.list.database.HabitDao
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.database.RemoteKeyDao
import com.davidread.habittracker.list.database.RemoteKeyEntity
import com.davidread.habittracker.list.model.HabitDto
import com.davidread.habittracker.list.model.HabitListResponse
import com.davidread.habittracker.list.service.HabitListService
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class HabitRemoteMediatorTest {

    private val database = mockk<HabitTrackerDatabase>()
    private val habitDao = mockk<HabitDao>()
    private val remoteKeyDao = mockk<RemoteKeyDao>()
    private val service = mockk<HabitListService>()
    private lateinit var systemUnderTest: HabitRemoteMediator

    @Before
    fun setUp() {
        every { database.habitDao() } returns habitDao
        every { database.remoteKeyDao() } returns remoteKeyDao
        mockkStatic("androidx.room.RoomDatabaseKt")
        val transactionLambda = slot<suspend () -> Any>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }
        systemUnderTest = HabitRemoteMediator(database, service)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkStatic("androidx.room.RoomDatabaseKt")
    }

    @Test
    fun test_load_refresh_success() = runBlocking {
        val habits = listOf(
            HabitDto("1", "Habit 1", 5, "2023-01-01T00:00:00Z"),
            HabitDto("2", "Habit 2", 10, "2023-01-02T00:00:00Z")
        )
        val response = HabitListResponse(habits, 2)
        coEvery { service.getHabits(page = 1, limit = 10) } returns response
        coEvery { remoteKeyDao.clearRemoteKeys() } returns Unit
        coEvery { habitDao.clearAll() } returns Unit
        coEvery { remoteKeyDao.insertAll(any()) } returns Unit
        coEvery { habitDao.insertAll(any()) } returns Unit

        val pagingState = PagingState<Int, HabitEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = systemUnderTest.load(LoadType.REFRESH, pagingState)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify { service.getHabits(page = 1, limit = 10) }
        coVerify { remoteKeyDao.clearRemoteKeys() }
        coVerify { habitDao.clearAll() }
        coVerify { remoteKeyDao.insertAll(any()) }
        coVerify { habitDao.insertAll(any()) }
    }

    @Test
    fun test_load_refresh_success_endOfPagination() = runBlocking {
        val response = HabitListResponse(emptyList(), null)
        coEvery { service.getHabits(page = 1, limit = 10) } returns response
        coEvery { remoteKeyDao.clearRemoteKeys() } returns Unit
        coEvery { habitDao.clearAll() } returns Unit
        coEvery { remoteKeyDao.insertAll(any()) } returns Unit
        coEvery { habitDao.insertAll(any()) } returns Unit

        val pagingState = PagingState<Int, HabitEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = systemUnderTest.load(LoadType.REFRESH, pagingState)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun test_load_refresh_error() = runBlocking {
        val exception = Exception("Network error")
        coEvery { service.getHabits(any(), any()) } throws exception

        val pagingState = PagingState<Int, HabitEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = systemUnderTest.load(LoadType.REFRESH, pagingState)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Error)
        Assert.assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
    }

    @Test
    fun test_load_append_success() = runBlocking {
        val habits = listOf(HabitDto("3", "Habit 3", 0, "2023-01-03T00:00:00Z"))
        val response = HabitListResponse(habits, 3)
        coEvery { service.getHabits(page = 2, limit = 10) } returns response
        coEvery { remoteKeyDao.remoteKeysHabitId("2") } returns RemoteKeyEntity("2", 1, 2)
        coEvery { remoteKeyDao.insertAll(any()) } returns Unit
        coEvery { habitDao.insertAll(any()) } returns Unit

        val pagingState = PagingState<Int, HabitEntity>(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(HabitEntity("2", "Habit 2", 10, "2023-01-02T00:00:00Z")),
                    prevKey = 1,
                    nextKey = 2
                )
            ),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = systemUnderTest.load(LoadType.APPEND, pagingState)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify { service.getHabits(page = 2, limit = 10) }
    }

    @Test
    fun test_load_append_endOfPagination() = runBlocking {
        val pagingState = PagingState<Int, HabitEntity>(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(HabitEntity("2", "Habit 2", 10, "2023-01-02T00:00:00Z")),
                    prevKey = 1,
                    nextKey = null
                )
            ),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )
        coEvery { remoteKeyDao.remoteKeysHabitId("2") } returns RemoteKeyEntity("2", 1, null)

        val result = systemUnderTest.load(LoadType.APPEND, pagingState)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify(exactly = 0) { service.getHabits(any(), any()) }
    }

    @Test
    fun test_load_prepend_endOfPagination() = runBlocking {
        val pagingState = PagingState<Int, HabitEntity>(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(HabitEntity("1", "Habit 1", 5, "2023-01-01T00:00:00Z")),
                    prevKey = null,
                    nextKey = 2
                )
            ),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )
        coEvery { remoteKeyDao.remoteKeysHabitId("1") } returns RemoteKeyEntity("1", null, 2)

        val result = systemUnderTest.load(LoadType.PREPEND, pagingState)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify(exactly = 0) { service.getHabits(any(), any()) }
    }
}
