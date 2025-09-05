package com.davidread.habittracker.common.ui.composable

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidread.habittracker.common.model.Route
import com.davidread.habittracker.common.model.Screen
import com.davidread.habittracker.login.composable.LoginScreen
import com.davidread.habittracker.login.model.LoginViewEffect
import com.davidread.habittracker.login.model.LoginViewIntent
import com.davidread.habittracker.login.viewmodel.LoginViewModel

@Composable
fun HabitTrackerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        composable(route = Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                viewModel.viewEffect.collect { viewEffect ->
                    when (viewEffect) {
                        is LoginViewEffect.NavigateToListScreen -> {
                            navController.navigate(Route.HABIT_LIST)
                        }

                        is LoginViewEffect.NavigateToSignUpScreen -> {
                            navController.navigate(Route.SIGN_UP)
                        }
                    }
                }
            }

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
                },
                onAlertDialogButtonClick = {
                    viewModel.processIntent(intent = LoginViewIntent.ClickAlertDialogButton)
                }
            )
        }

        // TODO: Define actual signup screen composable.
        composable(route = Screen.SignUp.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sign Up Screen")
            }
        }

        // TODO: Define actual habit list screen composable.
        composable(route = Screen.HabitList.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Habit List Screen")
            }
        }
    }
}
