package com.davidread.habittracker.login.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.login.composable.LoginScreen

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerTheme {
                LoginScreen()
            }
        }
    }
}
