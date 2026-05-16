package com.davidread.habittracker.list.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidread.habittracker.common.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.REMOTE_KEY_TABLE_NAME)
data class RemoteKeyEntity(
    @PrimaryKey val habitId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
