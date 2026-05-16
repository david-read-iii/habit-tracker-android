package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.signup.model.GetTimezoneResult
import com.davidread.habittracker.common.model.SaveAuthenticationTokenResult
import com.davidread.habittracker.signup.model.SignUpFlowResult
import com.davidread.habittracker.signup.model.SignUpUserResult
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.common.usecase.SaveAuthenticationTokenUseCase
import com.davidread.habittracker.common.usecase.ValidateEmailUseCase
import com.davidread.habittracker.common.usecase.ValidatePasswordUseCase
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class SignUpFlowUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val validateEmailUseCase = mockk<ValidateEmailUseCase>()

    private val validatePasswordUseCase = mockk<ValidatePasswordUseCase>()

    private val validateConfirmPasswordUseCase = mockk<ValidateConfirmPasswordUseCase>()

    private val getTimezoneUseCase = mockk<GetTimezoneUseCase>()

    private val signUpUserUseCase = mockk<SignUpUserUseCase>()

    private val saveAuthenticationTokenUseCase = mockk<SaveAuthenticationTokenUseCase>()

    private val signUpFlowUseCase = SignUpFlowUseCase(
        validateEmailUseCase,
        validatePasswordUseCase,
        validateConfirmPasswordUseCase,
        getTimezoneUseCase,
        signUpUserUseCase,
        saveAuthenticationTokenUseCase
    )

    @Test
    fun test_invoke_success() = runTest {
        mockExternalUseCases()
        val email = "david.read@gmail.com"
        val password = "password123"
        val confirmPassword = "password123"
        val result = signUpFlowUseCase(email, password, confirmPassword)

        Assert.assertEquals(
            SignUpFlowResult.Success(
                emailValidationResult = ValidationResult.Valid,
                passwordValidationResult = ValidationResult.Valid,
                confirmPasswordValidationResult = ValidationResult.Valid
            ),
            result
        )
        verify(exactly = 1) {
            validateEmailUseCase.invoke(email)
        }
        verify(exactly = 1) {
            validatePasswordUseCase.invoke(password)
        }
        verify(exactly = 1) {
            validateConfirmPasswordUseCase.invoke(password, confirmPassword)
        }
        verify(exactly = 1) {
            getTimezoneUseCase.invoke()
        }
        coVerify(exactly = 1) {
            signUpUserUseCase.invoke(email = email, password = password, timezone = NYC_TIMEZONE)
        }
        verify(exactly = 1) {
            saveAuthenticationTokenUseCase.invoke(token = TOKEN)
        }
    }

    @Test
    fun test_invoke_validationError() = runTest {
        val emailValidationResult = ValidationResult.Invalid
        val passwordValidationResult = ValidationResult.Invalid
        val confirmPasswordValidationResult = ValidationResult.Invalid
        mockExternalUseCases(
            emailValidationResult = emailValidationResult,
            passwordValidationResult = passwordValidationResult,
            confirmPasswordValidationResult = confirmPasswordValidationResult
        )
        val result = signUpFlowUseCase(
            email = "david.read@gmail.com",
            password = "password123",
            confirmPassword = "password123"
        )

        Assert.assertEquals(
            SignUpFlowResult.ValidationError(
                emailValidationResult,
                passwordValidationResult,
                confirmPasswordValidationResult
            ),
            result
        )
    }

    @Test
    fun test_invoke_getTimezoneError() = runTest {
        mockExternalUseCases(timezoneResult = GetTimezoneResult.Error)
        val result = signUpFlowUseCase(
            email = "david.read@gmail.com",
            password = "password123",
            confirmPassword = "password123"
        )

        Assert.assertEquals(
            SignUpFlowResult.GetTimezoneError(
                ValidationResult.Valid,
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    @Test
    fun test_invoke_emailAlreadyUsedError() = runTest {
        mockExternalUseCases(signUpUserResult = SignUpUserResult.EmailAlreadyUsed)
        val result = signUpFlowUseCase(
            email = "david.read@gmail.com",
            password = "password123",
            confirmPassword = "password123"
        )

        Assert.assertEquals(
            SignUpFlowResult.EmailAlreadyUsedError(
                ValidationResult.Valid,
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    @Test
    fun test_invoke_signUpServiceGenericError() = runTest {
        mockExternalUseCases(signUpUserResult = SignUpUserResult.GenericError)
        val result = signUpFlowUseCase(
            email = "david.read@gmail.com",
            password = "password123",
            confirmPassword = "password123"
        )

        Assert.assertEquals(
            SignUpFlowResult.SignUpServiceGenericError(
                ValidationResult.Valid,
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    @Test
    fun test_invoke_nullTokenError() = runTest {
        mockExternalUseCases(signUpUserResult = SignUpUserResult.NullToken)
        val result = signUpFlowUseCase(
            email = "david.read@gmail.com",
            password = "password123",
            confirmPassword = "password123"
        )

        Assert.assertEquals(
            SignUpFlowResult.NullTokenError(
                ValidationResult.Valid,
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    @Test
    fun test_invoke_saveAuthenticationTokenResultError() = runTest {
        mockExternalUseCases(saveAuthenticationTokenResult = SaveAuthenticationTokenResult.Error)
        val result = signUpFlowUseCase(
            email = "david.read@gmail.com",
            password = "password123",
            confirmPassword = "password123"
        )

        Assert.assertEquals(
            SignUpFlowResult.SaveAuthenticationTokenError(
                ValidationResult.Valid,
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    private fun mockExternalUseCases(
        emailValidationResult: ValidationResult = ValidationResult.Valid,
        passwordValidationResult: ValidationResult = ValidationResult.Valid,
        confirmPasswordValidationResult: ValidationResult = ValidationResult.Valid,
        timezoneResult: GetTimezoneResult = GetTimezoneResult.Success(timezone = NYC_TIMEZONE),
        signUpUserResult: SignUpUserResult = SignUpUserResult.Success(token = TOKEN),
        saveAuthenticationTokenResult: SaveAuthenticationTokenResult = SaveAuthenticationTokenResult.Success
    ) {
        every { validateEmailUseCase.invoke(any()) } returns emailValidationResult
        every { validatePasswordUseCase.invoke(any()) } returns passwordValidationResult
        every {
            validateConfirmPasswordUseCase.invoke(
                any(),
                any()
            )
        } returns confirmPasswordValidationResult
        every { getTimezoneUseCase.invoke() } returns timezoneResult
        coEvery { signUpUserUseCase.invoke(any(), any(), any()) } returns signUpUserResult
        every { saveAuthenticationTokenUseCase.invoke(any()) } returns saveAuthenticationTokenResult
    }

    companion object {
        private const val NYC_TIMEZONE = "America/New_York"
        private const val TOKEN = "12345"
    }
}
