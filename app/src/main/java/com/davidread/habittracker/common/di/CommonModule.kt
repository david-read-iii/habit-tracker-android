package com.davidread.habittracker.common.di

import com.davidread.habittracker.common.constant.BaseUrl
import com.davidread.habittracker.common.network.AuthenticationInterceptor
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseUrl.ANDROID_EMULATOR_DEBUG)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(authenticationInterceptor: AuthenticationInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authenticationInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesAuthenticationInterceptor(authenticationTokenRepository: AuthenticationTokenRepository): AuthenticationInterceptor {
        return AuthenticationInterceptor(authenticationTokenRepository)
    }
}
