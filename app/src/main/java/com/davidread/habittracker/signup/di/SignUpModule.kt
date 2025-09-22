package com.davidread.habittracker.signup.di

import com.davidread.habittracker.signup.repository.SignUpRepository
import com.davidread.habittracker.signup.repository.SignUpRepositoryImpl
import com.davidread.habittracker.signup.service.SignUpService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SignUpModule {

    @Provides
    @Singleton
    fun providesSignUpService(retrofit: Retrofit): SignUpService {
        return retrofit.create(SignUpService::class.java)
    }

    @Provides
    @Singleton
    fun providesSignUpRepository(service: SignUpService): SignUpRepository =
        SignUpRepositoryImpl(service)
}
