package com.davidread.habittracker.settings.model

sealed class SettingsViewIntent {
    object ClickResetTimezoneButton : SettingsViewIntent()
    object DismissResetTimezoneDialog : SettingsViewIntent()
    object ConfirmResetTimezone : SettingsViewIntent()
    object ClickLogOutButton : SettingsViewIntent()
    object DismissLogoutDialog : SettingsViewIntent()
    object ConfirmLogout : SettingsViewIntent()
}
