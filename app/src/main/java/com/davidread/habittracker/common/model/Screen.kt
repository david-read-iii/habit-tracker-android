package com.davidread.habittracker.common.model

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object HabitList : Screen("habit_list")
}
