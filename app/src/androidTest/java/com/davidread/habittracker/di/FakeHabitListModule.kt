package com.davidread.habittracker.di

import com.davidread.habittracker.fakes.FakeHabitListRepositoryImpl
import com.davidread.habittracker.list.di.HabitListModule
import com.davidread.habittracker.list.repository.HabitListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HabitListModule::class]
)
object FakeHabitListModule {

    @Provides
    @Singleton
    fun providesHabitListRepository(): HabitListRepository = FakeHabitListRepositoryImpl()
}

