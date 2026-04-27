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
import com.davidread.habittracker.list.model.EditorState
import com.davidread.habittracker.list.model.HabitListTextFieldViewState
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.model.HabitListViewState
import com.davidread.habittracker.list.model.HabitViewState
import com.davidread.habittracker.list.model.UpdateHabitResult
import com.davidread.habittracker.list.usecase.CheckInUseCase
import com.davidread.habittracker.list.usecase.CreateHabitUseCase
import com.davidread.habittracker.list.usecase.GetHabitsUseCase
import com.davidread.habittracker.list.usecase.UpdateHabitUseCase
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
    private val updateHabitUseCase: UpdateHabitUseCase,
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
            HabitListViewIntent.ClickAddHabitButton, is HabitListViewIntent.ClickEditHabitButton -> {
                _viewState.update {
                    it.copy(
                        habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                            showBottomSheet = true,
                            title = when (intent) {
                                HabitListViewIntent.ClickAddHabitButton -> application.getString(R.string.habit_list_add_habit_sheet_title)
                                is HabitListViewIntent.ClickEditHabitButton -> application.getString(
                                    R.string.habit_list_edit_habit_sheet_title
                                )

                                else -> ""
                            },
                            textFieldViewState = HabitListTextFieldViewState(
                                value = when (intent) {
                                    is HabitListViewIntent.ClickEditHabitButton -> intent.currentName
                                    else -> ""
                                }
                            ),
                            positiveButtonText = when (intent) {
                                HabitListViewIntent.ClickAddHabitButton -> application.getString(R.string.habit_list_add_habit_submit)
                                is HabitListViewIntent.ClickEditHabitButton -> application.getString(
                                    R.string.habit_list_edit_habit_submit
                                )

                                else -> ""
                            },
                            isEditingHabit = false,
                            editorState = when (intent) {
                                HabitListViewIntent.ClickAddHabitButton -> EditorState.Add
                                is HabitListViewIntent.ClickEditHabitButton -> EditorState.Edit(
                                    intent.habitId
                                )

                                else -> EditorState.Add
                            }
                        )
                    )
                }
            }

            is HabitListViewIntent.ChangeHabitEditorNameValue -> {
                _viewState.update {
                    it.copy(
                        habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                            textFieldViewState = HabitListTextFieldViewState(
                                value = intent.value,
                                errorMessage = "",
                                isError = false
                            )
                        )
                    )
                }
            }

            HabitListViewIntent.DismissHabitEditorBottomSheet -> {
                if (!_viewState.value.habitEditorBottomSheetViewState.isEditingHabit) {
                    _viewState.update {
                        it.copy(
                            habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                                showBottomSheet = false,
                                textFieldViewState = HabitListTextFieldViewState(),
                                isEditingHabit = false
                            )
                        )
                    }
                }
            }

            HabitListViewIntent.SubmitHabitEditorChanges -> {
                val currentState = _viewState.value
                if (currentState.habitEditorBottomSheetViewState.isEditingHabit) return

                viewModelScope.launch {
                    _viewState.update {
                        it.copy(
                            habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                                isEditingHabit = true
                            )
                        )
                    }

                    when (val editorState =
                        currentState.habitEditorBottomSheetViewState.editorState) {
                        EditorState.Add -> handleSubmitAddHabit(currentState)
                        is EditorState.Edit -> handleSubmitEditHabit(
                            currentState,
                            editorState.habitId
                        )
                    }
                }
            }

            HabitListViewIntent.ClickSettingsButton -> {
                viewModelScope.launch {
                    _viewEffect.emit(HabitListViewEffect.NavigateToSettingsScreen)
                }
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

    private suspend fun handleSubmitAddHabit(currentState: HabitListViewState) {
        when (createHabitUseCase(currentState.habitEditorBottomSheetViewState.textFieldViewState.value)) {
            is CreateHabitResult.Success -> {
                _viewState.update {
                    it.copy(
                        habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                            showBottomSheet = false,
                            textFieldViewState = HabitListTextFieldViewState(),
                            isEditingHabit = false,
                            editorState = EditorState.Add
                        )
                    )
                }
            }

            is CreateHabitResult.InvalidHabitName -> {
                _viewState.update {
                    it.copy(
                        habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                            textFieldViewState = HabitListTextFieldViewState(
                                isError = true,
                                errorMessage = application.getString(R.string.habit_list_add_habit_name_error)
                            ),
                            isEditingHabit = false
                        )
                    )
                }
            }

            is CreateHabitResult.Error -> {
                _viewState.update {
                    it.copy(
                        habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                            showBottomSheet = false,
                            textFieldViewState = HabitListTextFieldViewState(),
                            isEditingHabit = false,
                            editorState = EditorState.Add
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

    private suspend fun handleSubmitEditHabit(currentState: HabitListViewState, habitId: String) {
        when (
            updateHabitUseCase(
                id = habitId,
                name = currentState.habitEditorBottomSheetViewState.textFieldViewState.value
            )
        ) {
            is UpdateHabitResult.Success -> {
                _viewState.update {
                    it.copy(
                        habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                            showBottomSheet = false,
                            textFieldViewState = HabitListTextFieldViewState(),
                            isEditingHabit = false
                        )
                    )
                }
            }

            is UpdateHabitResult.InvalidHabitName -> {
                _viewState.update {
                    it.copy(
                        habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                            textFieldViewState = HabitListTextFieldViewState(
                                isError = true,
                                errorMessage = application.getString(R.string.habit_list_add_habit_name_error)
                            ),
                            isEditingHabit = false
                        )
                    )
                }
            }

            is UpdateHabitResult.Error -> {
                _viewState.update {
                    it.copy(
                        habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                            showBottomSheet = false,
                            textFieldViewState = HabitListTextFieldViewState(),
                            isEditingHabit = false
                        )
                    )
                }
                _viewEffect.emit(
                    HabitListViewEffect.ShowSnackbar(
                        application.getString(R.string.update_habit_error)
                    )
                )
            }
        }
    }
}
