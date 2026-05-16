package com.davidread.habittracker.settings.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.settings.model.ResetTimezoneRequest
import com.davidread.habittracker.settings.model.ResetTimezoneResponse

interface SettingsRepository {
    suspend fun resetTimezone(request: ResetTimezoneRequest): Result<ResetTimezoneResponse>
}
