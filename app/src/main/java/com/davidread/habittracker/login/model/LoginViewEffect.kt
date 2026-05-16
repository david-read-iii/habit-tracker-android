package com.davidread.habittracker.login.model

sealed class LoginViewEffect {
    object NavigateToHabitListScreen : LoginViewEffect()
    object NavigateToSignUpScreen : LoginViewEffect()
    data class AnnounceForAccessibility(val message: String) : LoginViewEffect()
}
