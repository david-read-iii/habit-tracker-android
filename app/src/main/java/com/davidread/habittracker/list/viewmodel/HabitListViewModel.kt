package com.davidread.habittracker.list.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.davidread.habittracker.R
import com.davidread.habittracker.list.mapper.HabitMapper
import com.davidread.habittracker.list.model.CheckInResult
import com.davidread.habittracker.list.model.CreateHabitResult
import com.davidread.habittracker.list.model.HabitListTextFieldViewState
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.model.HabitListViewState
import com.davidread.habittracker.list.model.HabitViewState
import com.davidread.habittracker.list.usecase.CheckInUseCase
import com.davidread.habittracker.list.usecase.CreateHabitUseCase
import com.davidread.habittracker.list.usecase.GetHabitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    getHabitsUseCase: GetHabitsUseCase,
    private val habitMapper: HabitMapper,
    private val checkInUseCase: CheckInUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val application: Application
) : ViewModel() {

    private val _viewState = MutableStateFlow(HabitListViewState())
    val viewState: StateFlow<HabitListViewState>
        get() = _viewState

    private val _viewEffect = MutableSharedFlow<HabitListViewEffect>()
    val viewEffect: SharedFlow<HabitListViewEffect>
        get() = _viewEffect

    val habitsPagingDataFlow: Flow<PagingData<HabitViewState>> =
        getHabitsUseCase()
            .map { pagingData -> pagingData.map { habitMapper.map(it) } }
            .cachedIn(viewModelScope)

    fun processIntent(intent: HabitListViewIntent) {
        when (intent) {
            HabitListViewIntent.ClickAddHabitButton -> {
                _viewState.update {
                    it.copy(
                        addHabitViewState = it.addHabitViewState.copy(
                            showBottomSheet = true,
                            textFieldViewState = HabitListTextFieldViewState(),
                            isCreatingHabit = false
                        )
                    )
                }
            }

            is HabitListViewIntent.ChangeHabitNameValue -> {
                _viewState.update {
                    it.copy(
                        addHabitViewState = it.addHabitViewState.copy(
                            textFieldViewState = HabitListTextFieldViewState(
                                value = intent.value,
                                errorMessage = "",
                                isError = false
                            )
                        )
                    )
                }
            }

            HabitListViewIntent.DismissAddHabitBottomSheet -> {
                if (!_viewState.value.addHabitViewState.isCreatingHabit) {
                    _viewState.update {
                        it.copy(
                            addHabitViewState = it.addHabitViewState.copy(
                                showBottomSheet = false,
                                textFieldViewState = HabitListTextFieldViewState(),
                                isCreatingHabit = false
                            )
                        )
                    }
                }
            }

            HabitListViewIntent.SubmitAddHabit -> {
                val currentState = _viewState.value
                if (currentState.addHabitViewState.isCreatingHabit) return

                viewModelScope.launch {
                    _viewState.update {
                        it.copy(
                            addHabitViewState = it.addHabitViewState.copy(
                                isCreatingHabit = true
                            )
                        )
                    }

                    when (createHabitUseCase(currentState.addHabitViewState.textFieldViewState.value)) {
                        is CreateHabitResult.Success -> {
                            _viewState.update {
                                it.copy(
                                    addHabitViewState = it.addHabitViewState.copy(
                                        showBottomSheet = false,
                                        textFieldViewState = HabitListTextFieldViewState(),
                                        isCreatingHabit = false
                                    )
                                )
                            }
                        }

                        is CreateHabitResult.InvalidHabitName -> {
                            _viewState.update {
                                it.copy(
                                    addHabitViewState = it.addHabitViewState.copy(
                                        textFieldViewState = HabitListTextFieldViewState(
                                            isError = true,
                                            errorMessage = application.getString(R.string.habit_list_add_habit_name_error)
                                        ),
                                        isCreatingHabit = false
                                    )
                                )
                            }
                        }

                        is CreateHabitResult.Error -> {
                            _viewState.update {
                                it.copy(
                                    addHabitViewState = it.addHabitViewState.copy(
                                        isCreatingHabit = false
                                    )
                                )
                            }
                            _viewEffect.emit(
                                HabitListViewEffect.ShowSnackbar(
                                    application.getString(R.string.create_habit_error)
                                )
                            )
                        }
                    }
                }
            }

            HabitListViewIntent.ClickSettingsButton -> {
                viewModelScope.launch {
                    _viewEffect.emit(HabitListViewEffect.NavigateToSettingsScreen)
                }
            }

            HabitListViewIntent.ClickAlertDialogButton -> {
                _viewState.value = _viewState.value.copy(
                    alertDialogViewState = _viewState.value.alertDialogViewState.copy(showDialog = false)
                )
            }

            is HabitListViewIntent.ClickHabit, is HabitListViewIntent.ClickCheckInHabitButton -> {
                viewModelScope.launch {
                    val habitId = when (intent) {
                        is HabitListViewIntent.ClickHabit -> intent.habitId
                        is HabitListViewIntent.ClickCheckInHabitButton -> intent.habitId
                        else -> ""
                    }
                    _viewState.update {
                        it.copy(checkingInHabitIds = it.checkingInHabitIds + habitId)
                    }
                    val result = checkInUseCase(habitId)
                    _viewState.update {
                        it.copy(checkingInHabitIds = it.checkingInHabitIds - habitId)
                    }
                    when (result) {
                        is CheckInResult.AlreadyCheckedInError -> {
                            _viewEffect.emit(
                                HabitListViewEffect.ShowSnackbar(
                                    application.getString(
                                        R.string.check_in_already_checked_in
                                    )
                                )
                            )
                        }
                        is CheckInResult.GenericError -> {
                            _viewEffect.emit(
                                HabitListViewEffect.ShowSnackbar(
                                    application.getString(
                                        R.string.check_in_generic_error
                                    )
                                )
                            )
                        }
                        else -> Unit
                    }
                }
            }

            else -> {} // TODO: Handle other intents.
        }
    }
}
