package com.davidread.habittracker.signup.model

sealed class GetTimezoneResult {
    data class Success(val timezone: String) : GetTimezoneResult()
    object Error : GetTimezoneResult()
}
