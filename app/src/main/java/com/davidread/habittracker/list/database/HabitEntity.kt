package com.davidread.habittracker.list.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidread.habittracker.common.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.HABIT_TABLE_NAME)
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val streak: Int,
    val createdAt: String
)
