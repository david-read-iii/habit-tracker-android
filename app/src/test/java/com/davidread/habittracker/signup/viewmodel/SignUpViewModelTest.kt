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
import com.davidread.habittracker.signup.model.ValidationResult
import com.davidread.habittracker.signup.usecase.SignUpFlowUseCase
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

// TODO: Consider using a separate parameterized test for test_processIntent_ClickSignUpButton_xxx scenarios.
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
            val email = "david.read@gmail.com"
            viewModel.processIntent(SignUpViewIntent.ChangeEmailValue(newValue = email))

            Assert.assertEquals(email, turbine.expectMostRecentItem().emailTextFieldViewState.value)
        }
    }

    @Test
    fun test_processIntent_ChangePasswordValue() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            val password = "password123"
            viewModel.processIntent(SignUpViewIntent.ChangePasswordValue(newValue = password))

            Assert.assertEquals(
                password,
                turbine.expectMostRecentItem().passwordTextFieldViewState.value
            )
        }
    }

    @Test
    fun test_processIntent_ChangeConfirmPasswordValue() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            val confirmPassword = "password123"
            viewModel.processIntent(SignUpViewIntent.ChangeConfirmPasswordValue(newValue = confirmPassword))

            Assert.assertEquals(
                confirmPassword,
                turbine.expectMostRecentItem().confirmPasswordTextFieldViewState.value
            )
        }
    }

    @Test
    fun test_processIntent_ClickSignUpButton_success() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery {
                signUpFlowUseCase.invoke(any(), any(), any())
            } returns SignUpFlowResult.Success(
                emailValidationResult = ValidationResult.Valid,
                passwordValidationResult = ValidationResult.Valid,
                confirmPasswordValidationResult = ValidationResult.Valid
            )
            viewModel.processIntent(SignUpViewIntent.ChangeEmailValue(newValue = EMAIL))
            viewModel.processIntent(SignUpViewIntent.ChangePasswordValue(newValue = PASSWORD))
            viewModel.processIntent(SignUpViewIntent.ChangeConfirmPasswordValue(newValue = PASSWORD))
            viewModel.processIntent(SignUpViewIntent.ClickSignUpButton)

            coVerify(exactly = 1) {
                signUpFlowUseCase.invoke(
                    email = EMAIL,
                    password = PASSWORD,
                    confirmPassword = PASSWORD
                )
            }
            val expectedViewState = SignUpViewState(
                emailTextFieldViewState = SignUpTextFieldViewState(
                    value = EMAIL,
                    isError = false,
                    errorMessage = ""
                ),
                passwordTextFieldViewState = SignUpTextFieldViewState(
                    value = PASSWORD,
                    isError = false,
                    errorMessage = ""
                ),
                confirmPasswordTextFieldViewState = SignUpTextFieldViewState(
                    value = PASSWORD,
                    isError = false,
                    errorMessage = ""
                ),
                showLoadingDialog = false,
                alertDialogViewState = AlertDialogViewState(
                    showDialog = false,
                    message = null
                )
            )
            val actualViewState = viewStateTurbine.expectMostRecentItem()
            Assert.assertEquals(expectedViewState, actualViewState)
            Assert.assertEquals(
                SignUpViewEffect.NavigateToHabitListScreen,
                viewEffectTurbine.expectMostRecentItem()
            )
            viewEffectTurbine.expectNoEvents()
        }
    }

    @Test
    fun test_processIntent_ClickSignUpButton_validationError() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery {
                signUpFlowUseCase.invoke(any(), any(), any())
            } returns SignUpFlowResult.ValidationError(
                emailValidationResult = ValidationResult.Invalid,
                passwordValidationResult = ValidationResult.Invalid,
                confirmPasswordValidationResult = ValidationResult.Invalid
            )
            viewModel.processIntent(SignUpViewIntent.ClickSignUpButton)

            val actualViewState = viewStateTurbine.expectMostRecentItem()
            Assert.assertTrue(actualViewState.emailTextFieldViewState.isError)
            Assert.assertEquals(EMAIL_ERROR_MESSAGE, actualViewState.emailTextFieldViewState.errorMessage)
            Assert.assertTrue(actualViewState.passwordTextFieldViewState.isError)
            Assert.assertEquals(PASSWORD_ERROR_MESSAGE, actualViewState.passwordTextFieldViewState.errorMessage)
            Assert.assertTrue(actualViewState.confirmPasswordTextFieldViewState.isError)
            Assert.assertEquals(CONFIRM_PASSWORD_ERROR_MESSAGE, actualViewState.confirmPasswordTextFieldViewState.errorMessage)
            Assert.assertFalse(actualViewState.showLoadingDialog)
            Assert.assertEquals(AlertDialogViewState(), actualViewState.alertDialogViewState)
            viewEffectTurbine.expectNoEvents()
        }
    }

    @Test
    fun test_processIntent_ClickSignUpButton_anyErrorToShowGenericAlertDialog() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery {
                signUpFlowUseCase.invoke(any(), any(), any())
            } returns SignUpFlowResult.SignUpServiceGenericError(
                emailValidationResult = ValidationResult.Valid,
                passwordValidationResult = ValidationResult.Valid,
                confirmPasswordValidationResult = ValidationResult.Valid
            )
            viewModel.processIntent(SignUpViewIntent.ClickSignUpButton)

            val actualViewState = viewStateTurbine.expectMostRecentItem()
            Assert.assertFalse(actualViewState.emailTextFieldViewState.isError)
            Assert.assertEquals("", actualViewState.emailTextFieldViewState.errorMessage)
            Assert.assertFalse(actualViewState.passwordTextFieldViewState.isError)
            Assert.assertEquals("", actualViewState.passwordTextFieldViewState.errorMessage)
            Assert.assertFalse(actualViewState.confirmPasswordTextFieldViewState.isError)
            Assert.assertEquals("", actualViewState.confirmPasswordTextFieldViewState.errorMessage)
            Assert.assertFalse(actualViewState.showLoadingDialog)
            Assert.assertEquals(AlertDialogViewState(showDialog = true), actualViewState.alertDialogViewState)
            viewEffectTurbine.expectNoEvents()
        }
    }

    @Test
    fun test_processIntent_ClickSignUpButton_emailAlreadyUsedError() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery {
                signUpFlowUseCase.invoke(any(), any(), any())
            } returns SignUpFlowResult.EmailAlreadyUsedError(
                emailValidationResult = ValidationResult.Valid,
                passwordValidationResult = ValidationResult.Valid,
                confirmPasswordValidationResult = ValidationResult.Valid
            )
            viewModel.processIntent(SignUpViewIntent.ClickSignUpButton)

            val actualViewState = viewStateTurbine.expectMostRecentItem()
            Assert.assertFalse(actualViewState.emailTextFieldViewState.isError)
            Assert.assertEquals("", actualViewState.emailTextFieldViewState.errorMessage)
            Assert.assertFalse(actualViewState.passwordTextFieldViewState.isError)
            Assert.assertEquals("", actualViewState.passwordTextFieldViewState.errorMessage)
            Assert.assertFalse(actualViewState.confirmPasswordTextFieldViewState.isError)
            Assert.assertEquals("", actualViewState.confirmPasswordTextFieldViewState.errorMessage)
            Assert.assertFalse(actualViewState.showLoadingDialog)
            Assert.assertEquals(
                AlertDialogViewState(
                    showDialog = true,
                    message = EMAIL_ALREADY_USED_ERROR_MESSAGE
                ), actualViewState.alertDialogViewState
            )
            viewEffectTurbine.expectNoEvents()
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
