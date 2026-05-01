package com.davidread.habittracker.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidread.habittracker.common.usecase.LogoutUseCase
import com.davidread.habittracker.settings.model.ResetTimezoneConfirmationDialogViewState
import com.davidread.habittracker.settings.model.SettingsViewEffect
import com.davidread.habittracker.settings.model.SettingsViewIntent
import com.davidread.habittracker.settings.model.SettingsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(SettingsViewState())
    val viewState: StateFlow<SettingsViewState>
        get() = _viewState

    private val _viewEffect = MutableSharedFlow<SettingsViewEffect>()
    val viewEffect: SharedFlow<SettingsViewEffect>
        get() = _viewEffect

    fun processIntent(intent: SettingsViewIntent) {
        when (intent) {
            SettingsViewIntent.ClickResetTimezoneButton -> {
                _viewState.update {
                    it.copy(
                        resetTimezoneConfirmationDialogViewState = ResetTimezoneConfirmationDialogViewState(
                            showDialog = true
                        )
                    )
                }
            }

            SettingsViewIntent.DismissResetTimezoneDialog -> {
                _viewState.update {
                    it.copy(
                        resetTimezoneConfirmationDialogViewState = ResetTimezoneConfirmationDialogViewState(
                            showDialog = false
                        )
                    )
                }
            }

            SettingsViewIntent.ConfirmResetTimezone -> {
                _viewState.update {
                    it.copy(
                        resetTimezoneConfirmationDialogViewState = ResetTimezoneConfirmationDialogViewState(
                            showDialog = true,
                            isSubmitting = true
                        )
                    )
                }
                viewModelScope.launch {
                    delay(5000) // FIXME: Simulated delay.
                    _viewState.update {
                        it.copy(
                            resetTimezoneConfirmationDialogViewState = ResetTimezoneConfirmationDialogViewState(
                                showDialog = false
                            )
                        )
                    }
                    // TODO: Implement reset timezone functionality.
                }
            }

            SettingsViewIntent.ClickLogOutButton -> {
                _viewState.update { it.copy(showLogoutDialog = true) }
            }

            SettingsViewIntent.DismissLogoutDialog -> {
                _viewState.update { it.copy(showLogoutDialog = false) }
            }

            SettingsViewIntent.ConfirmLogout -> {
                _viewState.update { it.copy(showLogoutDialog = false) }
                viewModelScope.launch {
                    logoutUseCase()
                    _viewEffect.emit(SettingsViewEffect.NavigateToLoginScreen)
                }
            }
        }
    }
}
