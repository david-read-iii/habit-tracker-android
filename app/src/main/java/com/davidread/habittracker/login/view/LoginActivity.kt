package com.davidread.habittracker.login.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.login.composable.LoginScreen
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaunchedEffect(Unit) {
                viewModel.viewEffect.collect { viewEffect ->
                    when (viewEffect) {
                        is LoginViewEffect.NavigateToListScreen -> {
                            Log.d("LoginActivity", "Navigate to list screen")
                            // TODO: Navigate to list screen.
                        }

                        is LoginViewEffect.NavigateToSignUpScreen -> {
                            Log.d("LoginActivity", "Navigate to sign up screen")
                            // TODO: Navigate to signup screen.
                        }
                    }
                }
            }

            HabitTrackerTheme {
                val viewState by viewModel.viewState.collectAsState()
                LoginScreen(
                    viewState = viewState,
                    onEmailValueChange = {
                        viewModel.processIntent(
                            intent = LoginViewIntent.ChangeEmailValue(
                                newValue = it
                            )
                        )
                    },
                    onPasswordValueChange = {
                        viewModel.processIntent(
                            intent = LoginViewIntent.ChangePasswordValue(
                                newValue = it
                            )
                        )
                    },
                    onLoginButtonClick = {
                        viewModel.processIntent(intent = LoginViewIntent.ClickLoginButton)
                    },
                    onSignUpLinkClick = {
                        viewModel.processIntent(intent = LoginViewIntent.ClickSignUpLink)
                    }
                )
            }
        }
    }
}
