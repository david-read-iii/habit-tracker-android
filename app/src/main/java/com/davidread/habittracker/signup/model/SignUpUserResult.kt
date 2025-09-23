package com.davidread.habittracker.signup.model

sealed class SignUpUserResult {
    data class Success(val token: String) : SignUpUserResult()
    object NullToken : SignUpUserResult()
    object EmailAlreadyUsed : SignUpUserResult()
    object GenericError : SignUpUserResult()
}
