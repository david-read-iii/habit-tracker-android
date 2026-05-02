package com.davidread.habittracker.fakes

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.settings.model.ResetTimezoneRequest
import com.davidread.habittracker.settings.model.ResetTimezoneResponse
import com.davidread.habittracker.settings.repository.SettingsRepository

class FakeSettingsRepositoryImpl : SettingsRepository {

    var resetTimezoneResponseType = ResetTimezoneResponseType.SUCCESS

    override suspend fun resetTimezone(request: ResetTimezoneRequest): Result<ResetTimezoneResponse> =
        when (resetTimezoneResponseType) {
            ResetTimezoneResponseType.SUCCESS -> Result.Success(
                ResetTimezoneResponse(message = "Timezone reset successfully")
            )

            ResetTimezoneResponseType.GENERIC_ERROR -> Result.Error(Exception())
        }

    enum class ResetTimezoneResponseType { SUCCESS, GENERIC_ERROR }
}
