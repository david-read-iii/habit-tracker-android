package com.davidread.habittracker.common.util

import android.util.Log
import javax.inject.Inject

class Logger @Inject constructor() {

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }
}
