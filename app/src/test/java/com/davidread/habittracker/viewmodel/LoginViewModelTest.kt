package com.davidread.habittracker.viewmodel

import app.cash.turbine.turbineScope
import com.davidread.habittracker.login.model.AlertDialogViewState
import com.davidread.habittracker.login.model.LoginResult
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.model.LoginViewState
import com.davidread.habittracker.login.usecase.LoginUseCase
import com.davidread.habittracker.login.viewmodel.LoginViewModel
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginUseCase = mockk<LoginUseCase>()

    private val viewModel = LoginViewModel(loginUseCase)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_processIntent_ChangeEmailValue() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            val email = "david.read@gmail.com"
            viewModel.processIntent(LoginViewIntent.ChangeEmailValue(newValue = email))

            Assert.assertEquals(email, turbine.expectMostRecentItem().emailTextFieldViewState.value)
        }
    }

    @Test
    fun test_processIntent_ChangePasswordValue() = runTest {
        turbineScope {
            val turbine = viewModel.viewState.testIn(backgroundScope)
            val password = "davidread"
            viewModel.processIntent(LoginViewIntent.ChangePasswordValue(newValue = password))

            Assert.assertEquals(
                password,
                turbine.expectMostRecentItem().passwordTextFieldViewState.value
            )
        }
    }

    @Test
    fun test_processIntent_ClickLoginButton_navigateToListScreenTrue() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            val viewState = mockk<LoginViewState>()
            val navigateToListScreen = true
            coEvery { loginUseCase.invoke(any()) } returns mockk<LoginResult> {
                every { this@mockk.viewState } returns viewState
                every { this@mockk.navigateToListScreen } returns navigateToListScreen
            }
            viewModel.processIntent(LoginViewIntent.ClickLoginButton)

            Assert.assertEquals(viewState, viewStateTurbine.expectMostRecentItem())
            Assert.assertEquals(
                LoginViewEffect.NavigateToHabitListScreen,
                viewEffectTurbine.expectMostRecentItem()
            )
            viewEffectTurbine.expectNoEvents()
        }
    }

    @Test
    fun test_processIntent_ClickLoginButton_navigateToListScreenFalse() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            val viewState = mockk<LoginViewState>()
            val navigateToListScreen = false
            coEvery { loginUseCase.invoke(any()) } returns mockk<LoginResult> {
                every { this@mockk.viewState } returns viewState
                every { this@mockk.navigateToListScreen } returns navigateToListScreen
            }
            viewModel.processIntent(LoginViewIntent.ClickLoginButton)

            Assert.assertEquals(viewState, viewStateTurbine.expectMostRecentItem())
            viewEffectTurbine.expectNoEvents()
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

            // Verify dialog shown.
            val alertDialogViewState = AlertDialogViewState(
                showDialog = true,
                message = "Oops. Something went wrong."
            )
            coEvery { loginUseCase.invoke(any()) } returns LoginResult(
                viewState = LoginViewState(
                    alertDialogViewState = alertDialogViewState
                )
            )
            viewModel.processIntent(LoginViewIntent.ClickLoginButton)
            Assert.assertEquals(alertDialogViewState, turbine.expectMostRecentItem().alertDialogViewState)

            // Verify dialog dismissed.
            viewModel.processIntent(LoginViewIntent.ClickAlertDialogButton)
            Assert.assertEquals(
                AlertDialogViewState(showDialog = false),
                turbine.expectMostRecentItem().alertDialogViewState
            )
        }
    }
}
