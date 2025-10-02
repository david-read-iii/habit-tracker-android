package com.davidread.habittracker.login.usecase

import com.davidread.habittracker.common.model.SaveAuthenticationTokenResult
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.common.usecase.SaveAuthenticationTokenUseCase
import com.davidread.habittracker.common.usecase.ValidateEmailUseCase
import com.davidread.habittracker.common.usecase.ValidatePasswordUseCase
import com.davidread.habittracker.login.model.LoginFlowResult
import com.davidread.habittracker.login.model.LoginUserResult
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class LoginFlowUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val validateEmailUseCase = mockk<ValidateEmailUseCase>()

    private val validatePasswordUseCase = mockk<ValidatePasswordUseCase>()

    private val loginUserUseCase = mockk<LoginUserUseCase>()

    private val saveAuthenticationTokenUseCase = mockk<SaveAuthenticationTokenUseCase>()

    private val loginFlowUseCase = LoginFlowUseCase(
        validateEmailUseCase = validateEmailUseCase,
        validatePasswordUseCase = validatePasswordUseCase,
        loginUserUseCase = loginUserUseCase,
        saveAuthenticationTokenUseCase = saveAuthenticationTokenUseCase
    )

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        mockExternalUseCases()
        val result = loginFlowUseCase.invoke(EMAIL, PASSWORD)

        Assert.assertEquals(
            LoginFlowResult.Success(
                emailValidationResult = ValidationResult.Valid,
                passwordValidationResult = ValidationResult.Valid
            ),
            result
        )
        verify(exactly = 1) {
            validateEmailUseCase.invoke(EMAIL)
        }
        verify(exactly = 1) {
            validatePasswordUseCase.invoke(PASSWORD)
        }
        coVerify(exactly = 1) {
            loginUserUseCase.invoke(EMAIL, PASSWORD)
        }
        verify(exactly = 1) {
            saveAuthenticationTokenUseCase.invoke(TOKEN)
        }
    }

    @Test
    fun test_invoke_validationError() = runTest {
        mockExternalUseCases(
            emailValidationResult = ValidationResult.Invalid,
            passwordValidationResult = ValidationResult.Invalid
        )
        val result = loginFlowUseCase(EMAIL, PASSWORD)

        Assert.assertEquals(
            LoginFlowResult.ValidationError(
                emailValidationResult = ValidationResult.Invalid,
                passwordValidationResult = ValidationResult.Invalid
            ),
            result
        )
    }

    @Test
    fun test_invoke_loginServiceCall400Error() = runTest {
        mockExternalUseCases(loginUserResult = LoginUserResult.IncorrectLoginCredentials)
        val result = loginFlowUseCase(EMAIL, PASSWORD)

        Assert.assertEquals(
            LoginFlowResult.IncorrectLoginCredentialsError(
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    @Test
    fun test_invoke_loginServiceCallGenericError() = runTest {
        mockExternalUseCases(loginUserResult = LoginUserResult.GenericError)
        val result = loginFlowUseCase(EMAIL, PASSWORD)

        Assert.assertEquals(
            LoginFlowResult.LoginServiceGenericError(
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    @Test
    fun test_invoke_loginServiceCallNullToken() = runTest {
        mockExternalUseCases(loginUserResult = LoginUserResult.NullToken)
        val result = loginFlowUseCase(EMAIL, PASSWORD)

        Assert.assertEquals(
            LoginFlowResult.NullTokenError(
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    @Test
    fun test_invoke_tokenSaveResultError() = runTest {
        mockExternalUseCases(saveAuthenticationTokenResult = SaveAuthenticationTokenResult.Error)
        val result = loginFlowUseCase(EMAIL, PASSWORD)

        Assert.assertEquals(
            LoginFlowResult.SaveAuthenticationTokenError(
                ValidationResult.Valid,
                ValidationResult.Valid
            ),
            result
        )
    }

    private fun mockExternalUseCases(
        emailValidationResult: ValidationResult = ValidationResult.Valid,
        passwordValidationResult: ValidationResult = ValidationResult.Valid,
        loginUserResult: LoginUserResult = LoginUserResult.Success(TOKEN),
        saveAuthenticationTokenResult: SaveAuthenticationTokenResult = SaveAuthenticationTokenResult.Success
    ) {
        every { validateEmailUseCase.invoke(any()) } returns emailValidationResult
        every { validatePasswordUseCase.invoke(any()) } returns passwordValidationResult
        coEvery { loginUserUseCase.invoke(any(), any()) } returns loginUserResult
        every { saveAuthenticationTokenUseCase.invoke(any()) } returns saveAuthenticationTokenResult
    }

    companion object {
        private const val EMAIL = "david.read@gmail.com"
        private const val PASSWORD = "password123"
        private const val TOKEN = "12345"
    }
}
