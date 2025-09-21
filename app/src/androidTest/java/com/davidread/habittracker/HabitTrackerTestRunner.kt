package com.davidread.habittracker

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.google.crypto.tink.config.TinkConfig
import dagger.hilt.android.testing.HiltTestApplication

class HabitTrackerTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        val application = super.newApplication(cl, HiltTestApplication::class.java.name, context)
        TinkConfig.register()
        return application
    }
}
