package com.davidread.habittracker.login.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginResponse
import com.davidread.habittracker.login.model.LoginUserResult
import com.davidread.habittracker.login.repository.LoginRepository
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

class LoginUserUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginRepository = mockk<LoginRepository>()

    private val logger = mockk<Logger>()

    private val loginUserUseCase = LoginUserUseCase(loginRepository, logger)

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
        coEvery { loginRepository.login(any()) } returns Result.Success(LoginResponse(TOKEN))

        Assert.assertEquals(
            LoginUserResult.Success(TOKEN),
            loginUserUseCase(EMAIL, PASSWORD)
        )
        coVerify(exactly = 1) {
            loginRepository.login(LoginRequest(EMAIL, PASSWORD))
        }
    }

    @Test
    fun test_invoke_nullToken() = runTest {
        coEvery { loginRepository.login(any()) } returns Result.Success(LoginResponse(null))

        Assert.assertEquals(
            LoginUserResult.NullToken,
            loginUserUseCase(EMAIL, PASSWORD)
        )
        coVerify(exactly = 1) {
            loginRepository.login(LoginRequest(EMAIL, PASSWORD))
        }
    }

    @Test
    fun test_invoke_emailAlreadyUsed() = runTest {
        coEvery { loginRepository.login(any()) } returns Result.Error(
            mockk<HttpException> {
                every { code() } returns 400
            }
        )

        Assert.assertEquals(
            LoginUserResult.IncorrectLoginCredentials,
            loginUserUseCase(EMAIL, PASSWORD)
        )
    }

    @Test
    fun test_invoke_genericError() = runTest {
        coEvery { loginRepository.login(any()) } returns Result.Error(mockk<Exception>())

        Assert.assertEquals(
            LoginUserResult.GenericError,
            loginUserUseCase(EMAIL, PASSWORD)
        )
    }

    companion object {
        private const val TOKEN = "12345"
        private const val EMAIL = "david.read@gmail.com"
        private const val PASSWORD = "password123"
    }
}
