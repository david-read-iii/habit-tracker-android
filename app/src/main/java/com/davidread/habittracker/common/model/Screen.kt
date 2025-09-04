package com.davidread.habittracker.common.model

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object HabitList : Screen("habit_list")
}
