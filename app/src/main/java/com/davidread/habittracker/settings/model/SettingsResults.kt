package com.davidread.habittracker.settings.model

sealed class ResetTimezoneResult {
    object Success : ResetTimezoneResult()
    object Error : ResetTimezoneResult()
}
