package com.davidread.habittracker.common.usecase

import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.database.HabitDao
import com.davidread.habittracker.list.database.RemoteKeyDao
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

class LogoutUseCaseTest {

    private val authenticationTokenRepository = mockk<AuthenticationTokenRepository>()
    private val database = mockk<HabitTrackerDatabase>()
    private val habitDao = mockk<HabitDao>()
    private val remoteKeyDao = mockk<RemoteKeyDao>()
    private val logger = mockk<Logger>()

    private val logoutUseCase = LogoutUseCase(
        authenticationTokenRepository = authenticationTokenRepository,
        database = database,
        logger = logger
    )

    @Before
    fun setUp() {
        every { database.habitDao() } returns habitDao
        every { database.remoteKeyDao() } returns remoteKeyDao
        every { logger.e(any(), any(), any()) } just runs
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        every { authenticationTokenRepository.clearAuthenticationToken() } just runs
        coEvery { habitDao.clearAll() } just runs
        coEvery { remoteKeyDao.clearRemoteKeys() } just runs

        val actual = logoutUseCase()

        Assert.assertEquals(Unit, actual)
        coVerify(exactly = 1) { habitDao.clearAll() }
        coVerify(exactly = 1) { remoteKeyDao.clearRemoteKeys() }
    }

    @Test
    fun test_invoke_clearAuthenticationToken_throws() = runTest {
        val exception = IllegalStateException("token")
        every { authenticationTokenRepository.clearAuthenticationToken() } throws exception
        coEvery { habitDao.clearAll() } just runs
        coEvery { remoteKeyDao.clearRemoteKeys() } just runs

        val actual = logoutUseCase()

        Assert.assertEquals(Unit, actual)
        coVerify(exactly = 1) { habitDao.clearAll() }
        coVerify(exactly = 1) { remoteKeyDao.clearRemoteKeys() }
    }

    @Test
    fun test_invoke_clearAll_throws() = runTest {
        val exception = IllegalStateException("db")
        every { authenticationTokenRepository.clearAuthenticationToken() } just runs
        coEvery { habitDao.clearAll() } throws exception

        val actual = logoutUseCase()

        Assert.assertEquals(Unit, actual)
        coVerify(exactly = 1) { habitDao.clearAll() }
        coVerify(exactly = 0) { remoteKeyDao.clearRemoteKeys() }
    }

    @Test
    fun test_invoke_clearRemoteKeys_throws() = runTest {
        val exception = IllegalStateException("remote")
        every { authenticationTokenRepository.clearAuthenticationToken() } just runs
        coEvery { habitDao.clearAll() } just runs
        coEvery { remoteKeyDao.clearRemoteKeys() } throws exception

        val actual = logoutUseCase()

        Assert.assertEquals(Unit, actual)
        coVerify(exactly = 1) { habitDao.clearAll() }
        coVerify(exactly = 1) { remoteKeyDao.clearRemoteKeys() }
    }
}
