package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.GetHabitsResult
import com.davidread.habittracker.list.repository.HabitListRepository
import javax.inject.Inject

private const val TAG = "GetHabitsUseCase"

class GetHabitsUseCase @Inject constructor(
    private val habitListRepository: HabitListRepository,
    private val logger: Logger
) {

    suspend operator fun invoke(page: Int, limit: Int): GetHabitsResult {
        return when (val result = habitListRepository.getHabits(page, limit)) {
            is Result.Success -> {
                val habits = result.data.habits
                if (habits != null) {
                    GetHabitsResult.Success(habits, result.data.nextPage)
                } else {
                    logger.e(TAG, "Habit list is null")
                    GetHabitsResult.Error
                }
            }
            is Result.Error -> {
                logger.e(TAG, "Error fetching habits", result.exception)
                GetHabitsResult.Error
            }
        }
    }
}
