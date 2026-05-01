package com.davidread.habittracker.settings.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.settings.model.ResetTimezoneRequest
import com.davidread.habittracker.settings.model.ResetTimezoneResult
import com.davidread.habittracker.settings.repository.SettingsRepository
import com.davidread.habittracker.signup.model.GetTimezoneResult
import com.davidread.habittracker.signup.usecase.GetTimezoneUseCase
import javax.inject.Inject

private const val TAG = "ResetTimezoneUseCase"

class ResetTimezoneUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getTimezoneUseCase: GetTimezoneUseCase,
    private val logger: Logger
) {

    suspend operator fun invoke(): ResetTimezoneResult {
        val getTimezoneResult = getTimezoneUseCase()
        if (getTimezoneResult is GetTimezoneResult.Error) {
            logger.e(TAG, "Error getting device timezone")
            return ResetTimezoneResult.Error
        }

        return when (val resetTimezoneResult =
            settingsRepository.resetTimezone(ResetTimezoneRequest(timezone = (getTimezoneResult as GetTimezoneResult.Success).timezone))) {
            is Result.Success -> ResetTimezoneResult.Success
            is Result.Error -> {
                logger.e(TAG, "Error resetting timezone", resetTimezoneResult.exception)
                ResetTimezoneResult.Error
            }
        }
    }
}
