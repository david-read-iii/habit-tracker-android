package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.DeleteHabitResult
import com.davidread.habittracker.list.repository.HabitListRepository
import javax.inject.Inject

private const val TAG = "DeleteHabitUseCase"

class DeleteHabitUseCase @Inject constructor(
    private val habitListRepository: HabitListRepository,
    private val logger: Logger
) {

    suspend operator fun invoke(id: String): DeleteHabitResult {
        return when (val result = habitListRepository.deleteHabit(id)) {
            is Result.Success -> DeleteHabitResult.Success
            is Result.Error -> {
                logger.e(TAG, "Error deleting habit", result.exception)
                DeleteHabitResult.Error
            }
        }
    }
}
