package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResult
import com.davidread.habittracker.list.repository.HabitListRepository
import retrofit2.HttpException
import javax.inject.Inject

private const val TAG = "CheckInUseCase"

class CheckInUseCase @Inject constructor(
    private val habitListRepository: HabitListRepository,
    private val logger: Logger
) {

    suspend operator fun invoke(habitId: String): CheckInResult {
        return when (val result = habitListRepository.checkIn(CheckInRequest(habitId))) {
            is Result.Success -> CheckInResult.Success
            is Result.Error -> {
                logger.e(TAG, "Error checking in", result.exception)
                if (result.exception is HttpException && result.exception.code() == 400) {
                    CheckInResult.AlreadyCheckedInError
                } else {
                    CheckInResult.GenericError
                }
            }
        }
    }
}
