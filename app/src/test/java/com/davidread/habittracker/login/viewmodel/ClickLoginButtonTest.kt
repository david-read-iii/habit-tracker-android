package com.davidread.habittracker.login.viewmodel

import android.app.Application
import app.cash.turbine.turbineScope
import com.davidread.habittracker.R
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.login.model.AlertDialogViewState
import com.davidread.habittracker.login.model.LoginFlowResult
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.usecase.LoginFlowUseCase
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ClickLoginButtonTest(
    private val loginFlowResult: LoginFlowResult,
    private val emailValue: String,
    private val passwordValue: String,
    private val expectedViewState: LoginViewState,
    private val expectedIsNavigateToHabitListScreen: Boolean
) {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginFlowUseCase = mockk<LoginFlowUseCase>()

    private val application = mockk<Application>()

    private val viewModel = LoginViewModel(loginFlowUseCase, application)

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(
                LoginFlowResult.Success(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                LoginViewState(
                    emailTextFieldViewState = LoginTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = LoginTextFieldViewState(value = PASSWORD)
                ),
                true
            ),
            arrayOf(
                LoginFlowResult.ValidationError(
                    emailValidationResult = ValidationResult.Invalid,
                    passwordValidationResult = ValidationResult.Invalid
                ),
                INVALID_EMAIL,
                INVALID_PASSWORD,
                LoginViewState(
                    emailTextFieldViewState = LoginTextFieldViewState(
                        value = INVALID_EMAIL,
                        isError = true,
                        errorMessage = EMAIL_ERROR_MESSAGE
                    ),
                    passwordTextFieldViewState = LoginTextFieldViewState(
                        value = INVALID_PASSWORD,
                        isError = true,
                        errorMessage = PASSWORD_ERROR_MESSAGE
                    )
                ),
                false
            ),
            arrayOf(
                LoginFlowResult.IncorrectLoginCredentialsError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                LoginViewState(
                    emailTextFieldViewState = LoginTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = LoginTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(
                        showDialog = true,
                        message = INCORRECT_LOGIN_CREDENTIALS
                    )
                ),
                false
            ),
            arrayOf(
                LoginFlowResult.LoginServiceGenericError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                LoginViewState(
                    emailTextFieldViewState = LoginTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = LoginTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(showDialog = true)
                ),
                false
            ),
            arrayOf(
                LoginFlowResult.NullTokenError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                LoginViewState(
                    emailTextFieldViewState = LoginTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = LoginTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(showDialog = true)
                ),
                false
            ),
            arrayOf(
                LoginFlowResult.SaveAuthenticationTokenError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                LoginViewState(
                    emailTextFieldViewState = LoginTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = LoginTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(showDialog = true)
                ),
                false
            ),
        )

        private const val EMAIL = "david.read@gmail.com"
        private const val INVALID_EMAIL = "invalid email format"
        private const val PASSWORD = "password123"
        private const val INVALID_PASSWORD = "pass"
        private const val EMAIL_ERROR_MESSAGE =
            "Please enter a valid email address (e.g. name@example.com)"
        private const val PASSWORD_ERROR_MESSAGE =
            "Please enter a password with at least 8 characters"
        private const val INCORRECT_LOGIN_CREDENTIALS =
            "Incorrect email or password. Please try again."
    }

    @Before
    fun setUp() {
        application.apply {
            every { getString(R.string.email_validation_error_message) } returns EMAIL_ERROR_MESSAGE
            every { getString(R.string.password_validation_error_message) } returns PASSWORD_ERROR_MESSAGE
            every { getString(R.string.login_credentials_incorrect_error_message) } returns INCORRECT_LOGIN_CREDENTIALS
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_processIntent_ClickLoginButton() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery {
                loginFlowUseCase.invoke(any(), any())
            } returns loginFlowResult
            viewModel.processIntent(LoginViewIntent.ChangeEmailValue(newValue = emailValue))
            viewModel.processIntent(LoginViewIntent.ChangePasswordValue(newValue = passwordValue))
            viewModel.processIntent(LoginViewIntent.ClickLoginButton)

            coVerify(exactly = 1) {
                loginFlowUseCase.invoke(email = emailValue, password = passwordValue)
            }
            Assert.assertEquals(expectedViewState, viewStateTurbine.expectMostRecentItem())
            if (expectedIsNavigateToHabitListScreen) {
                Assert.assertEquals(
                    LoginViewEffect.NavigateToHabitListScreen,
                    viewEffectTurbine.expectMostRecentItem()
                )
            }
            viewEffectTurbine.expectNoEvents()
        }
    }
}
