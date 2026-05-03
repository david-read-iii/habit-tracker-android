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
    private val expectedIsNavigateToHabitListScreen: Boolean,
    private val expectedAnnouncementMessage: String?
) {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginFlowUseCase = mockk<LoginFlowUseCase>()

    private val application = mockk<Application>()

    private val viewModel = LoginViewModel(loginFlowUseCase, application)

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any?>> = listOf(
            arrayOf(
                LoginFlowResult.Success(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                LoginViewState(),
                true,
                null
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
                false,
                FORM_VALIDATION_ERROR_ANNOUNCEMENT
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
                false,
                INCORRECT_LOGIN_CREDENTIALS
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
                false,
                GENERIC_ERROR_MESSAGE
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
                false,
                GENERIC_ERROR_MESSAGE
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
                false,
                GENERIC_ERROR_MESSAGE
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
        private const val FORM_VALIDATION_ERROR_ANNOUNCEMENT =
            "Please fix the errors in the form."
        private const val GENERIC_ERROR_MESSAGE = "An error occurred. Please try again later."
    }

    @Before
    fun setUp() {
        application.apply {
            every { getString(R.string.email_validation_error_message) } returns EMAIL_ERROR_MESSAGE
            every { getString(R.string.password_validation_error_message) } returns PASSWORD_ERROR_MESSAGE
            every { getString(R.string.login_credentials_incorrect_error_message) } returns INCORRECT_LOGIN_CREDENTIALS
            every { getString(R.string.form_validation_error_announcement) } returns FORM_VALIDATION_ERROR_ANNOUNCEMENT
            every { getString(R.string.generic_error_message) } returns GENERIC_ERROR_MESSAGE
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
            expectedAnnouncementMessage?.let {
                Assert.assertEquals(
                    LoginViewEffect.AnnounceForAccessibility(it),
                    viewEffectTurbine.expectMostRecentItem()
                )
            }
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
