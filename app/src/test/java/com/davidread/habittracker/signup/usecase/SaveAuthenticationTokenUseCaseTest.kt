package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.signup.model.SaveAuthenticationTokenResult
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SaveAuthenticationTokenUseCaseTest {

    private val authenticationTokenRepository = mockk<AuthenticationTokenRepository>()

    private val logger = mockk<Logger>()

    private val saveAuthenticationTokenUseCase =
        SaveAuthenticationTokenUseCase(authenticationTokenRepository, logger)

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
        every { authenticationTokenRepository.saveAuthenticationToken(any()) } returns Result.Success(
            Unit
        )

        Assert.assertEquals(
            SaveAuthenticationTokenResult.Success,
            saveAuthenticationTokenUseCase("12345")
        )
    }

    @Test
    fun test_invoke_error() {
        every { authenticationTokenRepository.saveAuthenticationToken(any()) } returns Result.Error(
            mockk()
        )

        Assert.assertEquals(
            SaveAuthenticationTokenResult.Error,
            saveAuthenticationTokenUseCase("12345")
        )
    }
}
