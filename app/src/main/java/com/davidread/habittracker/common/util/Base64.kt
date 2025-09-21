package com.davidread.habittracker.common.util

import android.util.Base64
import javax.inject.Inject

class Base64 @Inject constructor() {

    fun encodeToString(input: ByteArray, flags: Int): String? = Base64.encodeToString(input, flags)

    fun decode(str: String, flags: Int): ByteArray? = Base64.decode(str, flags)
}
