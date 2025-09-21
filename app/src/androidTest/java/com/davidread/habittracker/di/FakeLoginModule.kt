package com.davidread.habittracker.di

import com.davidread.habittracker.fakes.FakeLoginRepositoryImpl
import com.davidread.habittracker.login.di.LoginModule
import com.davidread.habittracker.login.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [LoginModule::class]
)
object FakeLoginModule {

    @Provides
    @Singleton
    fun providesLoginRepository(): LoginRepository = FakeLoginRepositoryImpl()
}
