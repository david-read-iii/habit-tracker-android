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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class HabitListRepositoryImpl @Inject constructor(
    private val database: HabitTrackerDatabase,
    private val habitListService: HabitListService
) : HabitListRepository {

    private val pagingResetTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getHabits(): Flow<PagingData<HabitEntity>> {
        return pagingResetTrigger.flatMapLatest { createPager().flow }
    }

    override fun invalidateHabits() {
        pagingResetTrigger.update { it + 1 }
    }

    override suspend fun createHabit(createHabitRequest: CreateHabitRequest): Result<CreateHabitResponse> =
        try {
            val response = habitListService.createHabit(createHabitRequest)
            response.toEntityOrNull()?.let { createdHabit ->
                database.habitDao().insertAll(listOf(createdHabit))
            }
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun deleteHabit(id: String): Result<DeleteHabitResponse> = try {
        val response = habitListService.deleteHabit(id)
        database.habitDao().deleteHabit(id)
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateHabit(
        id: String,
        updateHabitRequest: UpdateHabitRequest
    ): Result<UpdateHabitResponse> = try {
        val response = habitListService.updateHabit(id, updateHabitRequest)
        database.habitDao().updateHabitName(id, updateHabitRequest.name)
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun checkIn(checkInRequest: CheckInRequest): Result<CheckInResponse> = try {
        val response = habitListService.checkIn(checkInRequest)
        database.habitDao().incrementStreak(checkInRequest.habitId)
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(e)
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun createPager(): Pager<Int, HabitEntity> {
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
        )
    }

    private fun CreateHabitResponse?.toEntityOrNull(): HabitEntity? {
        val habit = this?.habit ?: return null
        val id = habit.id ?: return null
        val name = habit.name ?: return null
        val streak = habit.streak ?: return null
        val createdAt = habit.createdAt ?: return null
        return HabitEntity(
            id = id,
            name = name,
            streak = streak,
            createdAt = createdAt
        )
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
