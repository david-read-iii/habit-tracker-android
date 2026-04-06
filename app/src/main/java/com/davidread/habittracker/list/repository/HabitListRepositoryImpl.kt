package com.davidread.habittracker.list.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResponse
import com.davidread.habittracker.list.model.CreateHabitRequest
import com.davidread.habittracker.list.model.CreateHabitResponse
import com.davidread.habittracker.list.model.DeleteHabitResponse
import com.davidread.habittracker.list.model.UpdateHabitRequest
import com.davidread.habittracker.list.model.UpdateHabitResponse
import com.davidread.habittracker.list.service.HabitListService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HabitListRepositoryImpl @Inject constructor(
    private val database: HabitTrackerDatabase,
    private val habitListService: HabitListService
) : HabitListRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getHabits(): Flow<PagingData<HabitEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = HabitRemoteMediator(
                database = database,
                service = habitListService
            ),
            pagingSourceFactory = {
                database.habitDao().pagingSource()
            }
        ).flow
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

    companion object {
        private const val PAGE_SIZE = 20
    }
}
