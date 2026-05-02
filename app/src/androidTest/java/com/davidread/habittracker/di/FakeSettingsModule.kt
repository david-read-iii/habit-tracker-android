package com.davidread.habittracker.di

import com.davidread.habittracker.fakes.FakeSettingsRepositoryImpl
import com.davidread.habittracker.settings.di.SettingsModule
import com.davidread.habittracker.settings.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SettingsModule::class]
)
object FakeSettingsModule {

    @Provides
    @Singleton
    fun providesSettingsRepository(): SettingsRepository = FakeSettingsRepositoryImpl()
}
