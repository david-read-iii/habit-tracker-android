package com.davidread.habittracker.login.usecase

import android.app.Application
import com.davidread.habittracker.R
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.model.SaveAuthenticationTokenResult
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.common.usecase.SaveAuthenticationTokenUseCase
import com.davidread.habittracker.common.usecase.ValidateEmailUseCase
import com.davidread.habittracker.common.usecase.ValidatePasswordUseCase
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.login.model.AlertDialogViewState
import com.davidread.habittracker.login.model.LoginResponse
import com.davidread.habittracker.login.model.LoginResult
import com.davidread.habittracker.login.model.LoginTextFieldViewState
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.repository.LoginRepositoryImpl
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException

class LoginUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()

    private val logger = mockk<Logger>()

    private val validateEmailUseCase = mockk<ValidateEmailUseCase>()

    private val validatePasswordUseCase = mockk<ValidatePasswordUseCase>()

    private val loginRepository = mockk<LoginRepositoryImpl>()

    private val saveAuthenticationTokenUseCase = mockk<SaveAuthenticationTokenUseCase>()

    private val loginUseCase = LoginUseCase(
        application = application,
        logger = logger,
        validateEmailUseCase = validateEmailUseCase,
        validatePasswordUseCase = validatePasswordUseCase,
        loginRepository = loginRepository,
        saveAuthenticationTokenUseCase = saveAuthenticationTokenUseCase
    )

    @Before
    fun setUp() {
        application.apply {
            every { getString(R.string.email_validation_error_message) } returns EMAIL_ERROR_MESSAGE
            every { getString(R.string.password_validation_error_message) } returns PASSWORD_ERROR_MESSAGE
        }
        every { logger.e(any(), any(), any()) } just runs
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        mockExternalUseCases()
        coEvery { loginRepository.login(any()) } returns Result.Success(LoginResponse(token = "12345"))

        val expected = LoginResult(
            viewState = LoginViewState(),
            navigateToListScreen = true
        )
        val actual = loginUseCase.invoke(LoginViewState())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_textFieldValidationError() = runTest {
        mockExternalUseCases(
            emailValidationResult = ValidationResult.Invalid,
            passwordValidationResult = ValidationResult.Invalid
        )

        val expected = LoginResult(
            viewState = LoginViewState(
                emailTextFieldViewState = LoginTextFieldViewState(
                    isError = true,
                    errorMessage = EMAIL_ERROR_MESSAGE
                ),
                passwordTextFieldViewState = LoginTextFieldViewState(
                    isError = true,
                    errorMessage = PASSWORD_ERROR_MESSAGE
                ),
                alertDialogViewState = AlertDialogViewState(showDialog = false, message = null)
            ),
            navigateToListScreen = false
        )
        val actual = loginUseCase.invoke(LoginViewState())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_loginServiceCall400Error() = runTest {
        mockExternalUseCases()
        coEvery { loginRepository.login(any()) } returns Result.Error(
            mockk<HttpException> {
                every { code() } returns 400
            }
        )
        val errorMessage = "Incorrect email or password. Please try again."
        every { application.getString(R.string.login_credentials_incorrect_error_message) } returns errorMessage

        val expected = LoginResult(
            viewState = LoginViewState(
                alertDialogViewState = AlertDialogViewState(
                    showDialog = true,
                    message = errorMessage
                )
            ),
            navigateToListScreen = false
        )
        val actual = loginUseCase.invoke(LoginViewState())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_loginServiceCallGenericError() = runTest {
        mockExternalUseCases()
        coEvery { loginRepository.login(any()) } returns Result.Error(Exception())

        val expected = LoginResult(
            viewState = LoginViewState(
                alertDialogViewState = AlertDialogViewState(
                    showDialog = true,
                    message = null
                )
            ),
            navigateToListScreen = false
        )
        val actual = loginUseCase.invoke(LoginViewState())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_loginServiceCallNullToken() = runTest {
        mockExternalUseCases()
        coEvery { loginRepository.login(any()) } returns Result.Success(LoginResponse(token = null))

        val expected = LoginResult(
            viewState = LoginViewState(
                alertDialogViewState = AlertDialogViewState(
                    showDialog = true,
                    message = null
                )
            ),
            navigateToListScreen = false
        )
        val actual = loginUseCase.invoke(LoginViewState())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_tokenSaveResultError() = runTest {
        mockExternalUseCases(saveAuthenticationTokenResult = SaveAuthenticationTokenResult.Error)
        coEvery { loginRepository.login(any()) } returns Result.Success(LoginResponse(token = "12345"))

        val expected = LoginResult(
            viewState = LoginViewState(
                alertDialogViewState = AlertDialogViewState(
                    showDialog = true,
                    message = null
                )
            ),
            navigateToListScreen = false
        )
        val actual = loginUseCase.invoke(LoginViewState())
        Assert.assertEquals(expected, actual)
    }

    private fun mockExternalUseCases(
        emailValidationResult: ValidationResult = ValidationResult.Valid,
        passwordValidationResult: ValidationResult = ValidationResult.Valid,
        saveAuthenticationTokenResult: SaveAuthenticationTokenResult = SaveAuthenticationTokenResult.Success
    ) {
        every { validateEmailUseCase.invoke(any()) } returns emailValidationResult
        every { validatePasswordUseCase.invoke(any()) } returns passwordValidationResult
        every { saveAuthenticationTokenUseCase.invoke(any()) } returns saveAuthenticationTokenResult
    }

    companion object {
        private const val EMAIL_ERROR_MESSAGE = "Please enter a valid email address (e.g. name@example.com)"
        private const val PASSWORD_ERROR_MESSAGE = "Please enter a password with at least 8 characters"
    }
}
