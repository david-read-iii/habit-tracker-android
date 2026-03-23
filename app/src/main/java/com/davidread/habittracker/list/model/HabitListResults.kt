package com.davidread.habittracker.list.model

sealed class CheckInResult {
    object Success : CheckInResult()
    object AlreadyCheckedInError : CheckInResult()
    object GenericError : CheckInResult()
}

sealed class CreateHabitResult {
    object Success : CreateHabitResult()
    object InvalidHabitName : CreateHabitResult()
    object Error : CreateHabitResult()
}

sealed class DeleteHabitResult {
    object Success : DeleteHabitResult()
    object Error : DeleteHabitResult()
}

sealed class GetHabitsResult {
    data class Success(val habits: List<HabitDto>, val nextPage: Int?) : GetHabitsResult()
    object Error : GetHabitsResult()
}

sealed class UpdateHabitResult {
    object Success : UpdateHabitResult()
    object InvalidHabitName : UpdateHabitResult()
    object Error : UpdateHabitResult()
}
