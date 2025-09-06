package com.davidread.habittracker.login.usecase

import android.app.Application
import com.davidread.habittracker.R
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult.Status
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert
import org.junit.Test

class ValidatePasswordUseCaseTest {

    private val application = mockk<Application>()

    private val validatePasswordUseCase = ValidatePasswordUseCase(application = application)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_validPassword() {
        val password = "longerthan8chars"
        val expected = LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = LoginTextFieldViewState(
                value = password,
                isError = false,
                errorMessage = ""
            )
        )
        val actual =
            validatePasswordUseCase.invoke(viewState = LoginTextFieldViewState(value = password))
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_invalidPassword() {
        val password = "pass"
        val errorMessage = "Please enter a password with at least 8 characters"
        every { application.getString(R.string.password_validation_error_message) } returns errorMessage

        val expected = LoginTextFieldValidationResult(
            status = Status.INVALID,
            loginTextFieldViewState = LoginTextFieldViewState(
                value = password,
                isError = true,
                errorMessage = errorMessage
            )
        )
        val actual =
            validatePasswordUseCase.invoke(viewState = LoginTextFieldViewState(value = password))
        Assert.assertEquals(expected, actual)
    }
}
