package com.davidread.habittracker.list.di

import com.davidread.habittracker.common.config.BuildVariant
import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.list.repository.HabitListRepository
import com.davidread.habittracker.list.repository.HabitListRepositoryImpl
import com.davidread.habittracker.list.service.HabitListService
import com.davidread.habittracker.list.service.MockHabitListService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HabitListModule {

    @Provides
    @Singleton
    fun providesHabitListService(retrofit: Retrofit): HabitListService {
        return if (BuildVariant.current() == BuildVariant.MOCK_IN_APP_DEBUG) {
            MockHabitListService()
        } else {
            retrofit.create(HabitListService::class.java)
        }
    }

    @Provides
    @Singleton
    fun providesHabitListRepository(
        database: HabitTrackerDatabase,
        service: HabitListService
    ): HabitListRepository = HabitListRepositoryImpl(database, service)
}
