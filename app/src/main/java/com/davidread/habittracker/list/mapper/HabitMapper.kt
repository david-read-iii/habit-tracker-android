package com.davidread.habittracker.list.mapper

import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.model.HabitViewState
import javax.inject.Inject

class HabitMapper @Inject constructor(
    private val createdAtMapper: CreatedAtMapper
) {

    fun map(entity: HabitEntity): HabitViewState {
        return HabitViewState(
            id = entity.id,
            name = entity.name,
            streak = entity.streak.toString(),
            createdAt = createdAtMapper.map(entity.createdAt)
        )
    }
}
