package com.davidread.habittracker.login.viewmodel

import android.app.Application
import app.cash.turbine.turbineScope
import com.davidread.habittracker.R
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.login.model.AlertDialogViewState
import com.davidread.habittracker.login.model.LoginFlowResult
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.usecase.LoginFlowUseCase
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

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginFlowUseCase = mockk<LoginFlowUseCase>()

    private val application = mockk<Application>()

    private val viewModel = LoginViewModel(loginFlowUseCase, application)

    @Before
    fun setUp() {
        application.apply {
            every { getString(R.string.email_validation_error_message) } returns EMAIL_ERROR_MESSAGE
            every { getString(R.string.password_validation_error_message) } returns PASSWORD_ERROR_MESSAGE
            every { getString(R.string.login_credentials_incorrect_error_message) } returns INCORRECT_CREDENTIALS_ERROR_MESSAGE
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
            viewModel.processIntent(LoginViewIntent.ChangeEmailValue(newValue = EMAIL))

            Assert.assertEquals(EMAIL, turbine.expectMostRecentItem().emailTextFieldViewState.value)
        }
    }

    @Test
    fun test_processIntent_ChangePasswordValue() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            viewModel.processIntent(LoginViewIntent.ChangePasswordValue(newValue = PASSWORD))

            Assert.assertEquals(
                PASSWORD,
                turbine.expectMostRecentItem().passwordTextFieldViewState.value
            )
        }
    }

    @Test
    fun test_processIntent_ClickSignUpLink() = runTest {
        turbineScope {
            val turbine = viewModel.viewEffect.testIn(backgroundScope)
            viewModel.processIntent(LoginViewIntent.ClickSignUpLink)

            Assert.assertEquals(
                LoginViewEffect.NavigateToSignUpScreen,
                turbine.expectMostRecentItem()
            )
            turbine.expectNoEvents()
        }
    }

    @Test
    fun test_processIntent_ClickAlertDialogButton() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            coEvery {
                loginFlowUseCase.invoke(any(), any())
            } returns LoginFlowResult.IncorrectLoginCredentialsError(
                emailValidationResult = ValidationResult.Valid,
                passwordValidationResult = ValidationResult.Valid
            )
            viewModel.processIntent(LoginViewIntent.ClickLoginButton) // show alert dialog on UI
            Assert.assertEquals(
                AlertDialogViewState(
                    showDialog = true,
                    message = INCORRECT_CREDENTIALS_ERROR_MESSAGE
                ), turbine.expectMostRecentItem().alertDialogViewState
            )

            viewModel.processIntent(LoginViewIntent.ClickAlertDialogButton) // hide alert dialog on UI
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
        private const val INCORRECT_CREDENTIALS_ERROR_MESSAGE =
            "Incorrect email or password. Please try again."
    }
}
