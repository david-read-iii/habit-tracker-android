package com.davidread.habittracker.signup.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.signup.model.SignUpResponse
import com.davidread.habittracker.signup.service.SignUpService
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class SignUpRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val signUpService = mockk<SignUpService>()

    private val signUpRepository = SignUpRepositoryImpl(signUpService)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_signUp_success() = runTest {
        val signUpResponse = mockk<SignUpResponse>()
        coEvery { signUpService.signUp(any()) } returns signUpResponse

        Assert.assertEquals(Result.Success(signUpResponse), signUpRepository.signUp(mockk()))
    }

    @Test
    fun test_signUp_error() = runTest {
        val exception = mockk<Exception>()
        coEvery { signUpService.signUp(any()) } throws exception

        Assert.assertEquals(Result.Error(exception), signUpRepository.signUp(mockk()))
    }
}
