package com.davidread.habittracker.signup.util

import java.time.ZoneId
import javax.inject.Inject

class TimezoneProvider @Inject constructor() {

    fun getSystemDefaultZoneId(): ZoneId = ZoneId.systemDefault()
}
