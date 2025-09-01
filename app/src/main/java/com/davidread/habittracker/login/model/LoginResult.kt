package com.davidread.habittracker.login.model

data class LoginResult(
    val viewState: LoginViewState,
    val navigateToListScreen: Boolean = false
)
