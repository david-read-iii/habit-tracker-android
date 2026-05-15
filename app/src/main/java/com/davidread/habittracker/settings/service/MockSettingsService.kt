package com.davidread.habittracker.settings.service

import com.davidread.habittracker.settings.model.ResetTimezoneRequest
import com.davidread.habittracker.settings.model.ResetTimezoneResponse

/**
 * DEBUG ONLY: mock settings response so debug builds can run without backend connectivity.
 */
class MockSettingsService : SettingsService {

    override suspend fun resetTimezone(request: ResetTimezoneRequest): ResetTimezoneResponse {
        return ResetTimezoneResponse(
            message = "Timezone set to ${request.timezone} in debug mock"
        )
    }
}

