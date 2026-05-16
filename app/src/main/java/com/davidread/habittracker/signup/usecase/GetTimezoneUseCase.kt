package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.signup.model.GetTimezoneResult
import com.davidread.habittracker.signup.util.TimezoneProvider
import java.time.DateTimeException
import java.time.zone.ZoneRulesException
import javax.inject.Inject

private const val TAG = "GetTimezoneUseCase"

class GetTimezoneUseCase @Inject constructor(
    private val timezoneProvider: TimezoneProvider,
    private val logger: Logger
) {

    operator fun invoke() = try {
        val timezone = timezoneProvider.getSystemDefaultZoneId().id
        GetTimezoneResult.Success(timezone)
    } catch (e: DateTimeException) {
        logger.e(TAG, "Converted zone ID has invalid format", e)
        GetTimezoneResult.Error
    } catch (e: ZoneRulesException) {
        logger.e(TAG, "Converted zone region ID cannot be found", e)
        GetTimezoneResult.Error
    }
}
