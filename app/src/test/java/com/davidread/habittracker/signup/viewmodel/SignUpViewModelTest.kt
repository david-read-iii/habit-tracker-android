package com.davidread.habittracker.signup.viewmodel

import android.app.Application
import app.cash.turbine.turbineScope
import com.davidread.habittracker.R
import com.davidread.habittracker.signup.model.AlertDialogViewState
import com.davidread.habittracker.signup.model.SignUpFlowResult
import com.davidread.habittracker.signup.model.SignUpViewIntent
import com.davidread.habittracker.signup.model.ValidationResult
import com.davidread.habittracker.signup.usecase.SignUpFlowUseCase
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignUpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val signUpFlowUseCase = mockk<SignUpFlowUseCase>()

    private val application = mockk<Application>()

    private val viewModel = SignUpViewModel(signUpFlowUseCase, application)

    @Before
    fun setUp() {
        application.apply {
            every { getString(R.string.email_validation_error_message) } returns EMAIL_ERROR_MESSAGE
            every { getString(R.string.password_validation_error_message) } returns PASSWORD_ERROR_MESSAGE
            every { getString(R.string.confirm_password_validation_error_message) } returns CONFIRM_PASSWORD_ERROR_MESSAGE
            every { getString(R.string.email_already_used_error_message) } returns EMAIL_ALREADY_USED_ERROR_MESSAGE
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_processIntent_ChangeEmailValue() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            viewModel.processIntent(SignUpViewIntent.ChangeEmailValue(newValue = EMAIL))

            Assert.assertEquals(EMAIL, turbine.expectMostRecentItem().emailTextFieldViewState.value)
        }
    }

    @Test
    fun test_processIntent_ChangePasswordValue() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            viewModel.processIntent(SignUpViewIntent.ChangePasswordValue(newValue = PASSWORD))

            Assert.assertEquals(
                PASSWORD,
                turbine.expectMostRecentItem().passwordTextFieldViewState.value
            )
        }
    }

    @Test
    fun test_processIntent_ChangeConfirmPasswordValue() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            viewModel.processIntent(SignUpViewIntent.ChangeConfirmPasswordValue(newValue = PASSWORD))

            Assert.assertEquals(
                PASSWORD,
                turbine.expectMostRecentItem().confirmPasswordTextFieldViewState.value
            )
        }
    }

    @Test
    fun test_processIntent_ClickAlertDialogButton() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            coEvery {
                signUpFlowUseCase.invoke(any(), any(), any())
            } returns SignUpFlowResult.EmailAlreadyUsedError(
                emailValidationResult = ValidationResult.Valid,
                passwordValidationResult = ValidationResult.Valid,
                confirmPasswordValidationResult = ValidationResult.Valid
            )
            viewModel.processIntent(SignUpViewIntent.ClickSignUpButton) // show alert dialog on UI
            Assert.assertEquals(
                AlertDialogViewState(showDialog = true, message = EMAIL_ALREADY_USED_ERROR_MESSAGE),
                turbine.expectMostRecentItem().alertDialogViewState
            )

            viewModel.processIntent(SignUpViewIntent.ClickAlertDialogButton) // hide alert dialog on UI
            Assert.assertEquals(
                AlertDialogViewState(),
                turbine.expectMostRecentItem().alertDialogViewState
            )
        }
    }

    companion object {
        private const val EMAIL = "david.read@gmail.com"
        private const val PASSWORD = "password123"
        private const val EMAIL_ERROR_MESSAGE =
            "Please enter a valid email address (e.g. name@example.com)"
        private const val PASSWORD_ERROR_MESSAGE =
            "Please enter a password with at least 8 characters"
        private const val CONFIRM_PASSWORD_ERROR_MESSAGE = "Please make sure your passwords match"
        private const val EMAIL_ALREADY_USED_ERROR_MESSAGE =
            "This email address is already in use. Please try another one."
    }
}
