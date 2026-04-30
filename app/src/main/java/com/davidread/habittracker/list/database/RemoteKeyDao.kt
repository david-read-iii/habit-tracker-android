package com.davidread.habittracker.list.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davidread.habittracker.common.database.DatabaseConstants

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeyEntity>)

    @Query("SELECT * FROM ${DatabaseConstants.REMOTE_KEY_TABLE_NAME} WHERE habitId = :habitId")
    suspend fun remoteKeysHabitId(habitId: String): RemoteKeyEntity?

    @Query("DELETE FROM ${DatabaseConstants.REMOTE_KEY_TABLE_NAME}")
    suspend fun clearRemoteKeys()
}
