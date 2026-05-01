package com.davidread.habittracker.settings.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.settings.model.ResetTimezoneRequest
import com.davidread.habittracker.settings.model.ResetTimezoneResponse
import com.davidread.habittracker.settings.service.SettingsService
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsService: SettingsService
) : SettingsRepository {

    override suspend fun resetTimezone(request: ResetTimezoneRequest): Result<ResetTimezoneResponse> =
        try {
            Result.Success(settingsService.resetTimezone(request))
        } catch (e: Exception) {
            Result.Error(e)
        }
}
