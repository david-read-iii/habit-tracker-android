package com.davidread.habittracker.common.di

import android.content.Context
import androidx.room.Room
import com.davidread.habittracker.common.database.DatabaseConstants
import com.davidread.habittracker.common.database.HabitTrackerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesHabitTrackerDatabase(@ApplicationContext context: Context): HabitTrackerDatabase {
        return Room.databaseBuilder(
            context,
            HabitTrackerDatabase::class.java,
            DatabaseConstants.HABIT_TRACKER_DATABASE_NAME
        ).build()
    }
}
