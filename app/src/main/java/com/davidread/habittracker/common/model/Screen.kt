package com.davidread.habittracker.common.model

sealed class Screen(val route: String) {
    object Login : Screen(Route.LOGIN)
    object SignUp : Screen(Route.SIGN_UP)
    object HabitList : Screen(Route.HABIT_LIST)
}

object Route {
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val HABIT_LIST = "habit_list"
}
