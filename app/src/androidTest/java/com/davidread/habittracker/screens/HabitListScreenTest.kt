package com.davidread.habittracker.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidread.habittracker.common.ui.activity.MainActivity
import com.davidread.habittracker.fakes.FakeHabitListRepositoryImpl
import com.davidread.habittracker.list.composable.CLEAR_HABIT_NAME_BUTTON_TEST_TAG
import com.davidread.habittracker.list.repository.HabitListRepository
import com.davidread.habittracker.login.composable.LOGIN_BUTTON_TEST_TAG
import com.davidread.habittracker.login.repository.LoginRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HabitListScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var habitListRepository: HabitListRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        loginAndNavigateToHabitListScreen()
    }

    @Test
    fun test_habitListTopBarAndActionsAreDisplayed() {
        composeRule.onNodeWithText("Habits").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add habit").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Open settings").assertIsDisplayed()
    }

    @Test
    fun test_seededHabitIsDisplayed() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithContentDescription("Drink water", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithContentDescription("Drink water", substring = true).assertIsDisplayed()
    }

    @Test
    fun test_swipeLeftOnHabitRevealsSwipeActions() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithContentDescription("Drink water", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithContentDescription("Drink water", substring = true)
            .performTouchInput { swipeLeft() }

        composeRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Rename").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Check In").assertIsDisplayed()
    }

    @Test
    fun test_addHabitBottomSheetValidationErrorIsDisplayed() {
        composeRule.onNodeWithContentDescription("Add habit").performClick()
        composeRule.onNodeWithText("Add").performClick()

        composeRule.onNodeWithText("Please enter a habit name").assertIsDisplayed()
    }

    @Test
    fun test_addHabitGenericErrorSnackbarIsDisplayed() {
        (habitListRepository as FakeHabitListRepositoryImpl).createHabitResponseType =
            FakeHabitListRepositoryImpl.CreateHabitResponseType.GENERIC_ERROR

        composeRule.onNodeWithContentDescription("Add habit").performClick()
        composeRule.onNodeWithText("Habit name").performTextInput("Read every day")
        composeRule.onNodeWithText("Add").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Failed to add habit. Please try again.")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Failed to add habit. Please try again.").assertIsDisplayed()
    }

    @Test
    fun test_addHabitBottomSheetClearButtonClearsTextField() {
        composeRule.onNodeWithContentDescription("Add habit").performClick()
        composeRule.onNodeWithText("Habit name").performTextInput("Read every day")

        composeRule.onNodeWithTag(CLEAR_HABIT_NAME_BUTTON_TEST_TAG).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Read every day").fetchSemanticsNodes().isEmpty()
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag(CLEAR_HABIT_NAME_BUTTON_TEST_TAG).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun loginAndNavigateToHabitListScreen() {
        composeRule.onNodeWithText("Email").performTextInput("david.read@gmail.com")
        composeRule.onNodeWithText("Password").performTextInput("password123")
        composeRule.onNodeWithTag(LOGIN_BUTTON_TEST_TAG).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Habits").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Habits").assertIsDisplayed()
    }
}

