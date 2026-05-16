package com.davidread.habittracker.settings.service

import com.davidread.habittracker.settings.model.ResetTimezoneRequest
import com.davidread.habittracker.settings.model.ResetTimezoneResponse
import retrofit2.http.Body
import retrofit2.http.PATCH

interface SettingsService {

    @PATCH("api/timezone")
    suspend fun resetTimezone(@Body request: ResetTimezoneRequest): ResetTimezoneResponse
}
