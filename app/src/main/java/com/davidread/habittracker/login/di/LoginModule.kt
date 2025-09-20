package com.davidread.habittracker.login.di

import com.davidread.habittracker.login.repository.LoginRepository
import com.davidread.habittracker.login.repository.LoginRepositoryImpl
import com.davidread.habittracker.login.service.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LoginModule {

    @Provides
    @Singleton
    fun providesLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun providesLoginRepository(service: LoginService): LoginRepository =
        LoginRepositoryImpl(service)
}
