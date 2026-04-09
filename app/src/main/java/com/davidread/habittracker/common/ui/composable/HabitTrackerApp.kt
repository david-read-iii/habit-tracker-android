package com.davidread.habittracker.common.ui.composable

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidread.habittracker.common.model.Screen
import com.davidread.habittracker.list.composable.HabitListScreen
import com.davidread.habittracker.login.composable.LoginScreen
import com.davidread.habittracker.signup.composable.SignUpScreen

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
            LoginScreen(
                onNavigateToHabitListScreen = { navController.navigate(Screen.HabitList.route) },
                onNavigateToSignUpScreen = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToHabitListScreen = {
                    navController.navigate(Screen.HabitList.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.HabitList.route) {
            HabitListScreen(
                onNavigateToAddHabitScreen = {
                    // TODO: Define this when add-habit screen is available.
                },
                onNavigateToSettingsScreen = {
                    // TODO: Define this when settings screen is available.
                }
            )
        }
    }
}
