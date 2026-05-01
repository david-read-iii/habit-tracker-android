package com.davidread.habittracker.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidread.habittracker.R
import com.davidread.habittracker.common.usecase.LogoutUseCase
import com.davidread.habittracker.settings.model.ResetTimezoneConfirmationDialogViewState
import com.davidread.habittracker.settings.model.ResetTimezoneResult
import com.davidread.habittracker.settings.model.SettingsViewEffect
import com.davidread.habittracker.settings.model.SettingsViewIntent
import com.davidread.habittracker.settings.model.SettingsViewState
import com.davidread.habittracker.settings.usecase.ResetTimezoneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val resetTimezoneUseCase: ResetTimezoneUseCase
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
                if (_viewState.value.resetTimezoneConfirmationDialogViewState.isSubmitting) return
                _viewState.update {
                    it.copy(
                        resetTimezoneConfirmationDialogViewState = it.resetTimezoneConfirmationDialogViewState.copy(
                            isSubmitting = true
                        )
                    )
                }
                viewModelScope.launch {
                    when (resetTimezoneUseCase()) {
                        is ResetTimezoneResult.Success -> {
                            _viewEffect.emit(
                                SettingsViewEffect.ShowSnackbar(
                                    R.string.settings_reset_timezone_success
                                )
                            )
                        }

                        is ResetTimezoneResult.Error -> {
                            _viewEffect.emit(
                                SettingsViewEffect.ShowSnackbar(
                                    R.string.settings_reset_timezone_error
                                )
                            )
                        }
                    }
                    _viewState.update {
                        it.copy(
                            resetTimezoneConfirmationDialogViewState = ResetTimezoneConfirmationDialogViewState(
                                showDialog = false
                            )
                        )
                    }
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
