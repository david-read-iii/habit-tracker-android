package com.davidread.habittracker.login.di

import com.davidread.habittracker.common.constant.BuildVariant
import com.davidread.habittracker.login.repository.LoginRepository
import com.davidread.habittracker.login.repository.LoginRepositoryImpl
import com.davidread.habittracker.login.service.LoginService
import com.davidread.habittracker.login.service.MockLoginService
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
        // Use in-app mock services only for the dedicated mock build type.
        return if (BuildVariant.current() == BuildVariant.MOCK_IN_APP_DEBUG) {
            MockLoginService()
        } else {
            retrofit.create(LoginService::class.java)
        }
    }

    @Provides
    @Singleton
    fun providesLoginRepository(service: LoginService): LoginRepository =
        LoginRepositoryImpl(service)
}
