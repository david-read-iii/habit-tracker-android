package com.davidread.habittracker.list.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.model.ValidationResult
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.list.model.CreateHabitRequest
import com.davidread.habittracker.list.model.CreateHabitResult
import com.davidread.habittracker.list.repository.HabitListRepository
import javax.inject.Inject

private const val TAG = "CreateHabitUseCase"

class CreateHabitUseCase @Inject constructor(
    private val validateHabitNameUseCase: ValidateHabitNameUseCase,
    private val habitListRepository: HabitListRepository,
    private val logger: Logger
) {

    suspend operator fun invoke(name: String): CreateHabitResult {
        val validationResult = validateHabitNameUseCase(name)
        if (validationResult is ValidationResult.Invalid) {
            return CreateHabitResult.InvalidHabitName
        }
        return when (val result = habitListRepository.createHabit(CreateHabitRequest(name))) {
            is Result.Success -> CreateHabitResult.Success
            is Result.Error -> {
                logger.e(TAG, "Error creating habit", result.exception)
                CreateHabitResult.Error
            }
        }
    }
}
