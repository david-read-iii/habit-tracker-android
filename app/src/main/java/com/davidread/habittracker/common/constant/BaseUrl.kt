package com.davidread.habittracker.common.constant

object BaseUrl {
    private const val QA_DEBUG = "https://qa-api.example.com/"
    private const val LOCALHOST_EMULATOR = "http://10.0.2.2:3000/"

    fun current(): String {
        return if (BuildVariant.current() == BuildVariant.LOCAL_HOST_DEBUG) {
            LOCALHOST_EMULATOR
        } else {
            QA_DEBUG
        }
    }
}
