package com.davidread.habittracker.login.usecase

import android.app.Application
import com.davidread.habittracker.R
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult.Status
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ValidateEmailUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()

    private val validateEmailUseCase = ValidateEmailUseCase(application = application)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_validEmail() {
        val email = "david.read@gmail.com"
        val expected = LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = LoginTextFieldViewState(
                value = email,
                isError = false,
                errorMessage = ""
            )
        )
        val actual =
            validateEmailUseCase.invoke(viewState = LoginTextFieldViewState(value = email))
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_invalidEmail() {
        val email = "invalid email"
        val errorMessage = "Please enter a valid email address (e.g. name@example.com)"
        every { application.getString(R.string.email_validation_error_message) } returns errorMessage

        val expected = LoginTextFieldValidationResult(
            status = Status.INVALID,
            loginTextFieldViewState = LoginTextFieldViewState(
                value = email,
                isError = true,
                errorMessage = errorMessage
            )
        )
        val actual = validateEmailUseCase.invoke(viewState = LoginTextFieldViewState(value = email))
        Assert.assertEquals(expected, actual)
    }
}
