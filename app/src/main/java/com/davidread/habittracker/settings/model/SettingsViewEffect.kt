package com.davidread.habittracker.settings.model

sealed class SettingsViewEffect {
    object NavigateToLoginScreen : SettingsViewEffect()
}
