package com.davidread.habittracker.signup.model

sealed class SignUpViewEffect {
    object NavigateToHabitListScreen : SignUpViewEffect()
}
