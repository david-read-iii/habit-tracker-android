package com.davidread.habittracker

import android.app.Application
import com.google.crypto.tink.aead.AeadConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HabitTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AeadConfig.register()
    }
}
