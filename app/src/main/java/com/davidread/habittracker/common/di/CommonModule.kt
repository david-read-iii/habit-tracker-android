package com.davidread.habittracker.common.di

import com.davidread.habittracker.common.constant.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    // TODO: Add decrypted auth token in all service headers except login and signup.
    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseUrl.ANDROID_PHYSICAL_DEVICE_DEBUG)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
