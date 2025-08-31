package com.davidread.habittracker.login.model

sealed class LoginViewEffect {
    object NavigateToListScreen : LoginViewEffect()
    object NavigateToSignUpScreen : LoginViewEffect()
}
