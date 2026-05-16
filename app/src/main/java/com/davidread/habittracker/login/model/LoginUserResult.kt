package com.davidread.habittracker.login.model

sealed class LoginUserResult {
    data class Success(val token: String) : LoginUserResult()
    object NullToken : LoginUserResult()
    object IncorrectLoginCredentials : LoginUserResult()
    object GenericError : LoginUserResult()
}
