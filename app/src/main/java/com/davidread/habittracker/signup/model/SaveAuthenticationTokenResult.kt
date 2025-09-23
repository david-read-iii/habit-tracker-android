package com.davidread.habittracker.signup.model

sealed class SaveAuthenticationTokenResult {
    object Success : SaveAuthenticationTokenResult()
    object Error : SaveAuthenticationTokenResult()
}
