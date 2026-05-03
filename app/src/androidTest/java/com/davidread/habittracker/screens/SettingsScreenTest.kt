package com.davidread.habittracker.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidread.habittracker.common.ui.activity.MainActivity
import com.davidread.habittracker.fakes.FakeSettingsRepositoryImpl
import com.davidread.habittracker.login.composable.LOGIN_BUTTON_TEST_TAG
import com.davidread.habittracker.settings.repository.SettingsRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        loginAndNavigateToSettingsScreen()
    }

    @Test
    fun test_clickingResetTimezone_showsResetTimezoneDialog() {
        composeRule.onNodeWithText("Reset timezone").performClick()

        composeRule.onNodeWithText("Reset timezone?").assertIsDisplayed()
        composeRule.onNodeWithText(
            "Are you sure you want to reset your timezone? This will reset all your streaks."
        ).assertIsDisplayed()
        composeRule.onNodeWithText("Yes").assertIsDisplayed()
        composeRule.onNodeWithText("No").assertIsDisplayed()
    }

    @Test
    fun test_clickingNoOnResetTimezoneDialog_dismissesIt() {
        composeRule.onNodeWithText("Reset timezone").performClick()
        composeRule.onNodeWithText("No").performClick()

        waitUntilTextDoesNotExist("Reset timezone?")
        assertTextDoesNotExist("Reset timezone?")
        composeRule.onNodeWithText("Reset timezone").assertIsDisplayed()
    }

    @Test
    fun test_clickingYesOnResetTimezoneDialog_success_dismissesItAndShowsSuccessSnackbar() {
        (settingsRepository as FakeSettingsRepositoryImpl).resetTimezoneResponseType =
            FakeSettingsRepositoryImpl.ResetTimezoneResponseType.SUCCESS

        composeRule.onNodeWithText("Reset timezone").performClick()
        composeRule.onNodeWithText("Yes").performClick()

        waitUntilTextDoesNotExist("Reset timezone?")
        waitUntilTextExists("Timezone reset successfully. Streaks have been reset.")

        assertTextDoesNotExist("Reset timezone?")
        composeRule.onNodeWithText("Timezone reset successfully. Streaks have been reset.")
            .assertIsDisplayed()
    }

    @Test
    fun test_clickingYesOnResetTimezoneDialog_error_dismissesItAndShowsErrorSnackbar() {
        (settingsRepository as FakeSettingsRepositoryImpl).resetTimezoneResponseType =
            FakeSettingsRepositoryImpl.ResetTimezoneResponseType.GENERIC_ERROR

        composeRule.onNodeWithText("Reset timezone").performClick()
        composeRule.onNodeWithText("Yes").performClick()

        waitUntilTextDoesNotExist("Reset timezone?")
        waitUntilTextExists("Failed to reset timezone. Please try again.")

        assertTextDoesNotExist("Reset timezone?")
        composeRule.onNodeWithText("Failed to reset timezone. Please try again.")
            .assertIsDisplayed()
    }

    @Test
    fun test_clickingLogout_showsLogoutDialog() {
        composeRule.onNodeWithText("Log out").performClick()

        composeRule.onNodeWithText("Log out?").assertIsDisplayed()
        composeRule.onNodeWithText("Are you sure you want to log out?").assertIsDisplayed()
        composeRule.onNodeWithText("Yes").assertIsDisplayed()
        composeRule.onNodeWithText("No").assertIsDisplayed()
    }

    @Test
    fun test_clickingNoOnLogoutDialog_dismissesIt() {
        composeRule.onNodeWithText("Log out").performClick()
        composeRule.onNodeWithText("No").performClick()

        waitUntilTextDoesNotExist("Log out?")
        assertTextDoesNotExist("Log out?")
        composeRule.onNodeWithText("Log out").assertIsDisplayed()
    }

    private fun loginAndNavigateToSettingsScreen() {
        composeRule.onNodeWithText("Email").performTextInput("david.read@gmail.com")
        composeRule.onNodeWithText("Password").performTextInput("password123")
        composeRule.onNodeWithTag(LOGIN_BUTTON_TEST_TAG).performClick()

        waitUntilTextExists("Habits")
        composeRule.onNodeWithContentDescription("Open settings").performClick()
        waitUntilTextExists("Settings")
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    private fun waitUntilTextExists(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitUntilTextDoesNotExist(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun assertTextDoesNotExist(text: String) {
        composeRule.onAllNodesWithText(text).assertCountEquals(0)
    }
}
