package com.davidread.habittracker.signup.viewmodel

import android.app.Application
import app.cash.turbine.turbineScope
import com.davidread.habittracker.R
import com.davidread.habittracker.signup.model.AlertDialogViewState
import com.davidread.habittracker.signup.model.SignUpFlowResult
import com.davidread.habittracker.signup.model.SignUpTextFieldViewState
import com.davidread.habittracker.signup.model.SignUpViewEffect
import com.davidread.habittracker.signup.model.SignUpViewIntent
import com.davidread.habittracker.signup.model.SignUpViewState
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.signup.usecase.SignUpFlowUseCase
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ClickSignUpButtonTest(
    private val signUpFlowResult: SignUpFlowResult,
    private val emailValue: String,
    private val passwordValue: String,
    private val confirmPasswordValue: String,
    private val expectedViewState: SignUpViewState,
    private val expectedIsNavigateToHabitListScreen: Boolean,
    private val expectedAnnouncementMessage: String?
) {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val signUpFlowUseCase = mockk<SignUpFlowUseCase>()

    private val application = mockk<Application>()

    private val viewModel = SignUpViewModel(signUpFlowUseCase, application)

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any?>> = listOf(
            arrayOf(
                SignUpFlowResult.Success(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid,
                    confirmPasswordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                PASSWORD,
                SignUpViewState(
                    emailTextFieldViewState = SignUpTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    confirmPasswordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD)
                ),
                true,
                null
            ),
            arrayOf(
                SignUpFlowResult.ValidationError(
                    emailValidationResult = ValidationResult.Invalid,
                    passwordValidationResult = ValidationResult.Invalid,
                    confirmPasswordValidationResult = ValidationResult.Invalid
                ),
                INVALID_EMAIL,
                INVALID_PASSWORD,
                INVALID_PASSWORD,
                SignUpViewState(
                    emailTextFieldViewState = SignUpTextFieldViewState(
                        value = INVALID_EMAIL,
                        isError = true,
                        errorMessage = EMAIL_ERROR_MESSAGE
                    ),
                    passwordTextFieldViewState = SignUpTextFieldViewState(
                        value = INVALID_PASSWORD,
                        isError = true,
                        errorMessage = PASSWORD_ERROR_MESSAGE
                    ),
                    confirmPasswordTextFieldViewState = SignUpTextFieldViewState(
                        value = INVALID_PASSWORD,
                        isError = true,
                        errorMessage = CONFIRM_PASSWORD_ERROR_MESSAGE
                    )
                ),
                false,
                FORM_VALIDATION_ERROR_ANNOUNCEMENT
            ),
            arrayOf(
                SignUpFlowResult.GetTimezoneError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid,
                    confirmPasswordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                PASSWORD,
                SignUpViewState(
                    emailTextFieldViewState = SignUpTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    confirmPasswordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(showDialog = true)
                ),
                false,
                GENERIC_ERROR_MESSAGE
            ),
            arrayOf(
                SignUpFlowResult.EmailAlreadyUsedError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid,
                    confirmPasswordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                PASSWORD,
                SignUpViewState(
                    emailTextFieldViewState = SignUpTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    confirmPasswordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(
                        showDialog = true,
                        message = EMAIL_ALREADY_USED_ERROR_MESSAGE
                    )
                ),
                false,
                EMAIL_ALREADY_USED_ERROR_MESSAGE
            ),
            arrayOf(
                SignUpFlowResult.SignUpServiceGenericError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid,
                    confirmPasswordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                PASSWORD,
                SignUpViewState(
                    emailTextFieldViewState = SignUpTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    confirmPasswordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(showDialog = true)
                ),
                false,
                GENERIC_ERROR_MESSAGE
            ),
            arrayOf(
                SignUpFlowResult.NullTokenError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid,
                    confirmPasswordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                PASSWORD,
                SignUpViewState(
                    emailTextFieldViewState = SignUpTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    confirmPasswordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(showDialog = true)
                ),
                false,
                GENERIC_ERROR_MESSAGE
            ),
            arrayOf(
                SignUpFlowResult.SaveAuthenticationTokenError(
                    emailValidationResult = ValidationResult.Valid,
                    passwordValidationResult = ValidationResult.Valid,
                    confirmPasswordValidationResult = ValidationResult.Valid
                ),
                EMAIL,
                PASSWORD,
                PASSWORD,
                SignUpViewState(
                    emailTextFieldViewState = SignUpTextFieldViewState(value = EMAIL),
                    passwordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    confirmPasswordTextFieldViewState = SignUpTextFieldViewState(value = PASSWORD),
                    alertDialogViewState = AlertDialogViewState(showDialog = true)
                ),
                false,
                GENERIC_ERROR_MESSAGE
            )
        )

        private const val EMAIL = "david.read@gmail.com"
        private const val INVALID_EMAIL = "invalid email format"
        private const val PASSWORD = "password123"
        private const val INVALID_PASSWORD = "pass"
        private const val EMAIL_ERROR_MESSAGE =
            "Please enter a valid email address (e.g. name@example.com)"
        private const val PASSWORD_ERROR_MESSAGE =
            "Please enter a password with at least 8 characters"
        private const val CONFIRM_PASSWORD_ERROR_MESSAGE = "Please make sure your passwords match"
        private const val EMAIL_ALREADY_USED_ERROR_MESSAGE =
            "This email address is already in use. Please try another one."
        private const val FORM_VALIDATION_ERROR_ANNOUNCEMENT =
            "Please fix the errors in the form."
        private const val LOADING_ANNOUNCEMENT = "Loading..."
        private const val GENERIC_ERROR_MESSAGE = "An error occurred. Please try again later."
    }

    @Before
    fun setUp() {
        application.apply {
            every { getString(R.string.email_validation_error_message) } returns EMAIL_ERROR_MESSAGE
            every { getString(R.string.password_validation_error_message) } returns PASSWORD_ERROR_MESSAGE
            every { getString(R.string.confirm_password_validation_error_message) } returns CONFIRM_PASSWORD_ERROR_MESSAGE
            every { getString(R.string.email_already_used_error_message) } returns EMAIL_ALREADY_USED_ERROR_MESSAGE
            every { getString(R.string.form_validation_error_announcement) } returns FORM_VALIDATION_ERROR_ANNOUNCEMENT
            every { getString(R.string.generic_error_message) } returns GENERIC_ERROR_MESSAGE
            every { getString(R.string.loading) } returns LOADING_ANNOUNCEMENT
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_processIntent_ClickSignUpButton() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            runCurrent()
            coEvery {
                signUpFlowUseCase.invoke(any(), any(), any())
            } returns signUpFlowResult
            viewModel.processIntent(SignUpViewIntent.ChangeEmailValue(newValue = emailValue))
            viewModel.processIntent(SignUpViewIntent.ChangePasswordValue(newValue = passwordValue))
            viewModel.processIntent(SignUpViewIntent.ChangeConfirmPasswordValue(newValue = confirmPasswordValue))
            viewModel.processIntent(SignUpViewIntent.ClickSignUpButton)

            coVerify(exactly = 1) {
                signUpFlowUseCase.invoke(
                    email = emailValue,
                    password = passwordValue,
                    confirmPassword = confirmPasswordValue
                )
            }
            Assert.assertEquals(expectedViewState, viewStateTurbine.expectMostRecentItem())
            Assert.assertEquals(
                SignUpViewEffect.AnnounceForAccessibility(LOADING_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
            expectedAnnouncementMessage?.let {
                Assert.assertEquals(
                    SignUpViewEffect.AnnounceForAccessibility(it),
                    viewEffectTurbine.awaitItem()
                )
            }
            if (expectedIsNavigateToHabitListScreen) {
                Assert.assertEquals(
                    SignUpViewEffect.NavigateToHabitListScreen,
                    viewEffectTurbine.awaitItem()
                )
            }
            viewEffectTurbine.expectNoEvents()
        }
    }
}
