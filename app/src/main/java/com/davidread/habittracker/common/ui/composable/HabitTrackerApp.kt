package com.davidread.habittracker.common.ui.composable

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidread.habittracker.common.model.Route
import com.davidread.habittracker.common.model.Screen
import com.davidread.habittracker.login.composable.LoginScreen

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
                onNavigateToHabitListScreen = { navController.navigate(Route.HABIT_LIST) },
                onNavigateToSignUpScreen = { navController.navigate(Route.SIGN_UP) }
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
