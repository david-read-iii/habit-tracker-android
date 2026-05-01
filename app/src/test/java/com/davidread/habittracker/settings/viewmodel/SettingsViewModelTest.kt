package com.davidread.habittracker.settings.viewmodel

import app.cash.turbine.turbineScope
import com.davidread.habittracker.common.usecase.LogoutUseCase
import com.davidread.habittracker.settings.model.SettingsViewEffect
import com.davidread.habittracker.settings.model.SettingsViewIntent
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val logoutUseCase = mockk<LogoutUseCase>()

    private val viewModel = SettingsViewModel(logoutUseCase)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_viewState_initialState() {
        Assert.assertFalse(viewModel.viewState.value.showLogoutDialog)
        Assert.assertFalse(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.showDialog)
        Assert.assertFalse(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_ClickResetTimezoneButton_setsShowResetTimezoneDialogTrue() {
        viewModel.processIntent(SettingsViewIntent.ClickResetTimezoneButton)

        Assert.assertTrue(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.showDialog)
        Assert.assertFalse(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_DismissResetTimezoneDialog_setsShowResetTimezoneDialogFalse() {
        viewModel.processIntent(SettingsViewIntent.ClickResetTimezoneButton)
        Assert.assertTrue(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.showDialog)

        viewModel.processIntent(SettingsViewIntent.DismissResetTimezoneDialog)

        Assert.assertFalse(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.showDialog)
        Assert.assertFalse(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_ConfirmResetTimezone_setsSubmittingThenHidesDialog() = runTest {
        viewModel.processIntent(SettingsViewIntent.ClickResetTimezoneButton)
        Assert.assertTrue(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.showDialog)

        viewModel.processIntent(SettingsViewIntent.ConfirmResetTimezone)

        Assert.assertTrue(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.showDialog)
        Assert.assertTrue(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.isSubmitting)

        advanceTimeBy(5000)
        runCurrent()

        Assert.assertFalse(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.showDialog)
        Assert.assertFalse(viewModel.viewState.value.resetTimezoneConfirmationDialogViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_ClickLogOutButton_setsShowLogoutDialogTrue() {
        viewModel.processIntent(SettingsViewIntent.ClickLogOutButton)

        Assert.assertTrue(viewModel.viewState.value.showLogoutDialog)
    }

    @Test
    fun test_processIntent_DismissLogoutDialog_setsShowLogoutDialogFalse() {
        viewModel.processIntent(SettingsViewIntent.ClickLogOutButton)
        Assert.assertTrue(viewModel.viewState.value.showLogoutDialog)

        viewModel.processIntent(SettingsViewIntent.DismissLogoutDialog)

        Assert.assertFalse(viewModel.viewState.value.showLogoutDialog)
    }

    @Test
    fun test_processIntent_ConfirmLogout_hidesDialogAndEmitsNavigateToLoginScreen() = runTest {
        turbineScope {
            val turbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { logoutUseCase() } returns Unit
            viewModel.processIntent(SettingsViewIntent.ClickLogOutButton)

            viewModel.processIntent(SettingsViewIntent.ConfirmLogout)

            Assert.assertFalse(viewModel.viewState.value.showLogoutDialog)
            Assert.assertEquals(
                SettingsViewEffect.NavigateToLoginScreen,
                turbine.expectMostRecentItem()
            )
            coVerify(exactly = 1) { logoutUseCase() }
        }
    }
}

