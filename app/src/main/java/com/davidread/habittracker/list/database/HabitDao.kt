package com.davidread.habittracker.list.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davidread.habittracker.common.database.DatabaseConstants

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(habits: List<HabitEntity>)

    @Query("SELECT * FROM ${DatabaseConstants.HABIT_TABLE_NAME} ORDER BY createdAt DESC")
    fun pagingSource(): PagingSource<Int, HabitEntity>

    @Query("DELETE FROM ${DatabaseConstants.HABIT_TABLE_NAME}")
    suspend fun clearAll()

    @Query("SELECT * FROM ${DatabaseConstants.HABIT_TABLE_NAME} WHERE id = :id")
    suspend fun getHabitById(id: String): HabitEntity?

    @Query("UPDATE ${DatabaseConstants.HABIT_TABLE_NAME} SET streak = streak + 1 WHERE id = :id")
    suspend fun incrementStreak(id: String)
}
