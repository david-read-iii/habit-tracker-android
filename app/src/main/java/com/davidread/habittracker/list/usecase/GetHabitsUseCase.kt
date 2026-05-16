package com.davidread.habittracker.list.usecase

import androidx.paging.PagingData
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.repository.HabitListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(private val habitListRepository: HabitListRepository) {
    operator fun invoke(): Flow<PagingData<HabitEntity>> {
        return habitListRepository.getHabits()
    }
}
