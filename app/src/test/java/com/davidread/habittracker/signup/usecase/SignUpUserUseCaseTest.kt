package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.signup.model.SignUpRequest
import com.davidread.habittracker.signup.model.SignUpResponse
import com.davidread.habittracker.signup.model.SignUpUserResult
import com.davidread.habittracker.signup.repository.SignUpRepository
import com.davidread.habittracker.testutil.MainDispatcherRule
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
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException

class SignUpUserUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val signUpRepository = mockk<SignUpRepository>()

    private val logger = mockk<Logger>()

    private val signUpUserUseCase = SignUpUserUseCase(signUpRepository, logger)

    @Before
    fun setUp() {
        every { logger.e(any(), any(), any()) } just runs
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        val token = "12345"
        coEvery { signUpRepository.signUp(any()) } returns Result.Success(SignUpResponse(token))

        val email = "david.read@gmail.com"
        val password = "password123"
        val timezone = "America/New_York"
        Assert.assertEquals(
            SignUpUserResult.Success(token),
            signUpUserUseCase(email, password, timezone)
        )
        coVerify(exactly = 1) {
            signUpRepository.signUp(SignUpRequest(email, password, timezone))
        }
    }

    @Test
    fun test_invoke_nullToken() = runTest {
        coEvery { signUpRepository.signUp(any()) } returns Result.Success(SignUpResponse(null))

        val email = "david.read@gmail.com"
        val password = "password123"
        val timezone = "America/New_York"
        Assert.assertEquals(
            SignUpUserResult.NullToken,
            signUpUserUseCase(email, password, timezone)
        )
        coVerify(exactly = 1) {
            signUpRepository.signUp(SignUpRequest(email, password, timezone))
        }
    }

    @Test
    fun test_invoke_emailAlreadyUsed() = runTest {
        coEvery { signUpRepository.signUp(any()) } returns Result.Error(
            mockk<HttpException> {
                every { code() } returns 400
            }
        )

        Assert.assertEquals(
            SignUpUserResult.EmailAlreadyUsed,
            signUpUserUseCase(
                email = "david.read@gmail.com",
                password = "password123",
                timezone = "America/New_York"
            )
        )
    }

    @Test
    fun test_invoke_genericError() = runTest {
        coEvery { signUpRepository.signUp(any()) } returns Result.Error(mockk<Exception>())

        Assert.assertEquals(
            SignUpUserResult.GenericError,
            signUpUserUseCase(
                email = "david.read@gmail.com",
                password = "password123",
                timezone = "America/New_York"
            )
        )
    }
}
