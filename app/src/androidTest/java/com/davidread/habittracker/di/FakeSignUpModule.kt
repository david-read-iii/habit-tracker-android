package com.davidread.habittracker.di

import com.davidread.habittracker.fakes.FakeSignUpRepositoryImpl
import com.davidread.habittracker.signup.di.SignUpModule
import com.davidread.habittracker.signup.repository.SignUpRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SignUpModule::class]
)
object FakeSignUpModule {

    @Provides
    @Singleton
    fun providesSignUpRepository(): SignUpRepository = FakeSignUpRepositoryImpl()
}
