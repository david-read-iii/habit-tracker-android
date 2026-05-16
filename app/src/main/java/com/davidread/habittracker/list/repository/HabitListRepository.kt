package com.davidread.habittracker.list.repository

import androidx.paging.PagingData
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResponse
import com.davidread.habittracker.list.model.CreateHabitRequest
import com.davidread.habittracker.list.model.CreateHabitResponse
import com.davidread.habittracker.list.model.DeleteHabitResponse
import com.davidread.habittracker.list.model.UpdateHabitRequest
import com.davidread.habittracker.list.model.UpdateHabitResponse
import kotlinx.coroutines.flow.Flow

interface HabitListRepository {
    fun getHabits(): Flow<PagingData<HabitEntity>>
    fun invalidateHabits()
    suspend fun createHabit(createHabitRequest: CreateHabitRequest): Result<CreateHabitResponse>
    suspend fun deleteHabit(id: String): Result<DeleteHabitResponse>
    suspend fun updateHabit(id: String, updateHabitRequest: UpdateHabitRequest): Result<UpdateHabitResponse>
    suspend fun checkIn(checkInRequest: CheckInRequest): Result<CheckInResponse>
}
