package com.davidread.habittracker.screens

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidread.habittracker.common.ui.activity.MainActivity
import com.davidread.habittracker.fakes.FakeLoginRepositoryImpl
import com.davidread.habittracker.login.composable.SIGN_UP_LINK_TEST_TAG
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
class LoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var loginRepository: LoginRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_textFieldsAreDisplayed() {
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test
    fun test_textFieldErrorsAreDisplayed() {
        composeRule.onNodeWithText("Email").performTextInput("invalid email")
        composeRule.onNodeWithText("Password").performTextInput("123")
        composeRule.onNodeWithText("Login").performClick()

        composeRule.onNodeWithText("Please enter a valid email address (e.g. name@example.com)").assertIsDisplayed()
        composeRule.onNodeWithText("Please enter a password with at least 8 characters").assertIsDisplayed()
    }

    @Test
    fun test_unknownLoginCredentialsErrorDialogIsDisplayed() {
        (loginRepository as FakeLoginRepositoryImpl).loginResponseType = FakeLoginRepositoryImpl.LoginResponseType.ERROR_400
        composeRule.onNodeWithText("Email").performTextInput("david.read@gmail.com")
        composeRule.onNodeWithText("Password").performTextInput("password123")
        composeRule.onNodeWithText("Login").performClick()

        composeRule.onNodeWithText("Incorrect email or password. Please try again.").assertIsDisplayed()
    }

    @Test
    fun test_genericErrorDialogIsDisplayed() {
        (loginRepository as FakeLoginRepositoryImpl).loginResponseType = FakeLoginRepositoryImpl.LoginResponseType.GENERIC_ERROR
        composeRule.onNodeWithText("Email").performTextInput("david.read@gmail.com")
        composeRule.onNodeWithText("Password").performTextInput("password123")
        composeRule.onNodeWithText("Login").performClick()

        composeRule.onNodeWithText("An error occurred. Please try again later.").assertIsDisplayed()
    }

    @Test
    fun test_signUpScreenIsDisplayed() {
        composeRule.onNodeWithTag(SIGN_UP_LINK_TEST_TAG).performSemanticsAction(SemanticsActions.OnClick)

        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Confirm Password").assertIsDisplayed()
    }

    @Test
    fun test_habitListScreenIsDisplayed() {
        (loginRepository as FakeLoginRepositoryImpl).loginResponseType = FakeLoginRepositoryImpl.LoginResponseType.SUCCESS
        composeRule.onNodeWithText("Email").performTextInput("david.read@gmail.com")
        composeRule.onNodeWithText("Password").performTextInput("password123")
        composeRule.onNodeWithText("Login").performClick()

        composeRule.onNodeWithText("Habit List Screen").assertIsDisplayed()
    }
}
