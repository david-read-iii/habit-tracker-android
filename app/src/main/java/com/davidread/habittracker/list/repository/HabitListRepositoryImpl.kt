package com.davidread.habittracker.list.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResponse
import com.davidread.habittracker.list.model.CreateHabitRequest
import com.davidread.habittracker.list.model.CreateHabitResponse
import com.davidread.habittracker.list.model.DeleteHabitResponse
import com.davidread.habittracker.list.model.HabitListResponse
import com.davidread.habittracker.list.model.UpdateHabitRequest
import com.davidread.habittracker.list.model.UpdateHabitResponse
import com.davidread.habittracker.list.service.HabitListService
import javax.inject.Inject

class HabitListRepositoryImpl @Inject constructor(
    private val habitListService: HabitListService
) : HabitListRepository {

    override suspend fun getHabits(page: Int, limit: Int): Result<HabitListResponse> = try {
        val response = habitListService.getHabits(page, limit)
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun createHabit(createHabitRequest: CreateHabitRequest): Result<CreateHabitResponse> =
        try {
            val response = habitListService.createHabit(createHabitRequest)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun deleteHabit(id: String): Result<DeleteHabitResponse> = try {
        val response = habitListService.deleteHabit(id)
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateHabit(
        id: String,
        updateHabitRequest: UpdateHabitRequest
    ): Result<UpdateHabitResponse> = try {
        val response = habitListService.updateHabit(id, updateHabitRequest)
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun checkIn(checkInRequest: CheckInRequest): Result<CheckInResponse> = try {
        val response = habitListService.checkIn(checkInRequest)
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
