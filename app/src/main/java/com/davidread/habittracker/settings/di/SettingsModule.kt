package com.davidread.habittracker.settings.di

import com.davidread.habittracker.settings.repository.SettingsRepository
import com.davidread.habittracker.settings.repository.SettingsRepositoryImpl
import com.davidread.habittracker.settings.service.SettingsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {

    @Provides
    @Singleton
    fun providesSettingsService(retrofit: Retrofit): SettingsService {
        return retrofit.create(SettingsService::class.java)
    }

    @Provides
    @Singleton
    fun providesSettingsRepository(service: SettingsService): SettingsRepository =
        SettingsRepositoryImpl(service)
}
