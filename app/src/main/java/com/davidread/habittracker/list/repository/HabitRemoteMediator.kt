package com.davidread.habittracker.list.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.database.RemoteKeyEntity
import com.davidread.habittracker.list.model.HabitDto
import com.davidread.habittracker.list.service.HabitListService

@OptIn(ExperimentalPagingApi::class)
class HabitRemoteMediator(
    private val database: HabitTrackerDatabase,
    private val service: HabitListService
) : RemoteMediator<Int, HabitEntity>() {

    var times = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HabitEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = service.getHabits(page = page, limit = state.config.pageSize)
            val habits = response.habits?.filter {
                it.id != null && it.name != null && it.streak != null && it.createdAt != null
            } ?: emptyList()
            val endOfPaginationReached =
                (response.habits ?: emptyList()).isEmpty() || response.nextPage == null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().clearRemoteKeys()
                    database.habitDao().clearAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = habits.map { it.toRemoteKeyEntity(prevKey, nextKey) }
                database.remoteKeyDao().insertAll(keys)
                database.habitDao().insertAll(habits.map { it.toEntity() })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, HabitEntity>): RemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { habit ->
                database.remoteKeyDao().remoteKeysHabitId(habit.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, HabitEntity>): RemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { habit ->
                database.remoteKeyDao().remoteKeysHabitId(habit.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, HabitEntity>
    ): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { habitId ->
                database.remoteKeyDao().remoteKeysHabitId(habitId)
            }
        }
    }

    private fun HabitDto.toRemoteKeyEntity(prevKey: Int?, nextKey: Int?) = RemoteKeyEntity(
        habitId = id!!,
        prevKey = prevKey,
        nextKey = nextKey
    )

    private fun HabitDto.toEntity() = HabitEntity(
        id = id!!,
        name = name!!,
        streak = streak!!,
        createdAt = createdAt!!
    )
}
