package com.davidread.habittracker.common.model

sealed class SaveAuthenticationTokenResult {
    object Success : SaveAuthenticationTokenResult()
    object Error : SaveAuthenticationTokenResult()
}
