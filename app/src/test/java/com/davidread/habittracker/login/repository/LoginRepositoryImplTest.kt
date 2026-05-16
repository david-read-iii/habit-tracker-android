package com.davidread.habittracker.login.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.login.model.LoginResponse
import com.davidread.habittracker.login.service.LoginService
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class LoginRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginService = mockk<LoginService>()

    private val loginRepository = LoginRepositoryImpl(loginService)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_login_success() = runTest {
        val loginResponse = mockk<LoginResponse>()
        coEvery { loginService.login(any()) } returns loginResponse

        Assert.assertEquals(Result.Success(loginResponse), loginRepository.login(mockk()))
    }

    @Test
    fun test_login_error() = runTest {
        val exception = mockk<Exception>()
        coEvery { loginService.login(any()) } throws exception

        Assert.assertEquals(Result.Error(exception), loginRepository.login(mockk()))
    }
}
