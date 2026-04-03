package com.davidread.habittracker.list.di

import android.content.Context
import androidx.room.Room
import com.davidread.habittracker.common.database.DatabaseConstants
import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.list.repository.HabitListRepository
import com.davidread.habittracker.list.repository.HabitListRepositoryImpl
import com.davidread.habittracker.list.service.HabitListService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HabitListModule {

    @Provides
    @Singleton
    fun providesHabitTrackerDatabase(@ApplicationContext context: Context): HabitTrackerDatabase {
        return Room.databaseBuilder(
            context,
            HabitTrackerDatabase::class.java,
            DatabaseConstants.HABIT_TRACKER_DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesHabitListService(retrofit: Retrofit): HabitListService {
        return retrofit.create(HabitListService::class.java)
    }

    @Provides
    @Singleton
    fun providesHabitListRepository(
        database: HabitTrackerDatabase,
        service: HabitListService
    ): HabitListRepository = HabitListRepositoryImpl(database, service)
}
