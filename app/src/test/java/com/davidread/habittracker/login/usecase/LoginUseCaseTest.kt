package com.davidread.habittracker.login.usecase

import android.app.Application
import com.davidread.habittracker.R
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.login.model.AlertDialogViewState
import com.davidread.habittracker.login.model.LoginResponse
import com.davidread.habittracker.login.model.LoginResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult
import com.davidread.habittracker.login.model.LoginTextFieldValidationResult.Status
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

    private val authenticationTokenRepository = mockk<AuthenticationTokenRepository>()

    private val loginUseCase = LoginUseCase(
        application = application,
        logger = logger,
        validateEmailUseCase = validateEmailUseCase,
        validatePasswordUseCase = validatePasswordUseCase,
        loginRepository = loginRepository,
        authenticationTokenRepository = authenticationTokenRepository
    )

    @Before
    fun setUp() {
        every { logger.e(any(), any(), any()) } just runs
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_invoke_success() = runTest {
        val emailLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validateEmailUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = emailLoginTextFieldValidationResult
        )
        val passwordLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validatePasswordUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = passwordLoginTextFieldValidationResult
        )
        coEvery { loginRepository.login(any()) } returns Result.Success(LoginResponse(token = "12345"))
        every { authenticationTokenRepository.saveAuthenticationToken(any()) } returns Result.Success(
            Unit
        )

        val expected = LoginResult(
            viewState = LoginViewState(
                emailTextFieldViewState = emailLoginTextFieldValidationResult,
                passwordTextFieldViewState = passwordLoginTextFieldValidationResult
            ),
            navigateToListScreen = true
        )
        val actual = loginUseCase.invoke(LoginViewState())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_textFieldValidationError() = runTest {
        val emailLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validateEmailUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.INVALID,
            loginTextFieldViewState = emailLoginTextFieldValidationResult
        )
        val passwordLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validatePasswordUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.INVALID,
            loginTextFieldViewState = passwordLoginTextFieldValidationResult
        )

        val expected = LoginResult(
            viewState = LoginViewState(
                emailTextFieldViewState = emailLoginTextFieldValidationResult,
                passwordTextFieldViewState = passwordLoginTextFieldValidationResult,
                alertDialogViewState = AlertDialogViewState(showDialog = false, message = null)
            ),
            navigateToListScreen = false
        )
        val actual = loginUseCase.invoke(LoginViewState())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_invoke_loginServiceCall400Error() = runTest {
        val emailLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validateEmailUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = emailLoginTextFieldValidationResult
        )
        val passwordLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validatePasswordUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = passwordLoginTextFieldValidationResult
        )
        coEvery { loginRepository.login(any()) } returns Result.Error(
            mockk<HttpException> {
                every { code() } returns 400
            }
        )
        val errorMessage = "Incorrect email or password. Please try again."
        every { application.getString(R.string.login_credentials_incorrect_error_message) } returns errorMessage

        val expected = LoginResult(
            viewState = LoginViewState(
                emailTextFieldViewState = emailLoginTextFieldValidationResult,
                passwordTextFieldViewState = passwordLoginTextFieldValidationResult,
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
        val emailLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validateEmailUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = emailLoginTextFieldValidationResult
        )
        val passwordLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validatePasswordUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = passwordLoginTextFieldValidationResult
        )
        coEvery { loginRepository.login(any()) } returns Result.Error(Exception())

        val expected = LoginResult(
            viewState = LoginViewState(
                emailTextFieldViewState = emailLoginTextFieldValidationResult,
                passwordTextFieldViewState = passwordLoginTextFieldValidationResult,
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
        val emailLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validateEmailUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = emailLoginTextFieldValidationResult
        )
        val passwordLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validatePasswordUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = passwordLoginTextFieldValidationResult
        )
        coEvery { loginRepository.login(any()) } returns Result.Success(LoginResponse(token = null))

        val expected = LoginResult(
            viewState = LoginViewState(
                emailTextFieldViewState = emailLoginTextFieldValidationResult,
                passwordTextFieldViewState = passwordLoginTextFieldValidationResult,
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
        val emailLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validateEmailUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = emailLoginTextFieldValidationResult
        )
        val passwordLoginTextFieldValidationResult = mockk<LoginTextFieldViewState>()
        every { validatePasswordUseCase.invoke(any()) } returns LoginTextFieldValidationResult(
            status = Status.VALID,
            loginTextFieldViewState = passwordLoginTextFieldValidationResult
        )
        coEvery { loginRepository.login(any()) } returns Result.Success(LoginResponse(token = "12345"))
        every { authenticationTokenRepository.saveAuthenticationToken(any()) } returns Result.Error(mockk())

        val expected = LoginResult(
            viewState = LoginViewState(
                emailTextFieldViewState = emailLoginTextFieldValidationResult,
                passwordTextFieldViewState = passwordLoginTextFieldValidationResult,
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
}
