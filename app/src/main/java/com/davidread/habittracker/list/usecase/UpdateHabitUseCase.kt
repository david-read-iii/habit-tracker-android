package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.UpdateHabitRequest
import com.davidread.habittracker.list.model.UpdateHabitResult
import com.davidread.habittracker.list.repository.HabitListRepository
import javax.inject.Inject

private const val TAG = "UpdateHabitUseCase"

class UpdateHabitUseCase @Inject constructor(
    private val validateHabitNameUseCase: ValidateHabitNameUseCase,
    private val habitListRepository: HabitListRepository,
    private val logger: Logger
) {

    suspend operator fun invoke(id: String, name: String): UpdateHabitResult {
        val validationResult = validateHabitNameUseCase(name)
        if (validationResult is ValidationResult.Invalid) {
            return UpdateHabitResult.InvalidHabitName
        }
        return when (val result = habitListRepository.updateHabit(id, UpdateHabitRequest(name))) {
            is Result.Success -> UpdateHabitResult.Success
            is Result.Error -> {
                logger.e(TAG, "Error updating habit", result.exception)
                UpdateHabitResult.Error
            }
        }
    }
}
