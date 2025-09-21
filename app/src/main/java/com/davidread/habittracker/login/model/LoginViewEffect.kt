package com.davidread.habittracker.login.model

sealed class LoginViewEffect {
    object NavigateToHabitListScreen : LoginViewEffect()
    object NavigateToSignUpScreen : LoginViewEffect()
}
