package com.davidread.habittracker.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davidread.habittracker.list.database.HabitDao
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.database.RemoteKeyDao
import com.davidread.habittracker.list.database.RemoteKeyEntity

@Database(entities = [HabitEntity::class, RemoteKeyEntity::class], version = 1, exportSchema = false)
abstract class HabitTrackerDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}
