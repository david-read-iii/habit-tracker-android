package com.davidread.habittracker.settings.model

import androidx.annotation.StringRes

sealed class SettingsViewEffect {
    object NavigateToLoginScreen : SettingsViewEffect()
    data class ShowSnackbar(@param:StringRes val messageResId: Int) : SettingsViewEffect()
}
