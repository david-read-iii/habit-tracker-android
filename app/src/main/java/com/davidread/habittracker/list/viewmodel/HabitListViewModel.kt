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
import com.davidread.habittracker.list.model.DeleteHabitDialogViewState
import com.davidread.habittracker.list.model.DeleteHabitResult
import com.davidread.habittracker.list.model.EditorState
import com.davidread.habittracker.list.model.HabitListTextFieldViewState
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.model.HabitListViewState
import com.davidread.habittracker.list.model.HabitViewState
import com.davidread.habittracker.list.model.UpdateHabitResult
import com.davidread.habittracker.list.usecase.CheckInUseCase
import com.davidread.habittracker.list.usecase.CreateHabitUseCase
import com.davidread.habittracker.list.usecase.DeleteHabitUseCase
import com.davidread.habittracker.list.usecase.GetHabitsUseCase
import com.davidread.habittracker.list.usecase.UpdateHabitUseCase
import com.davidread.habittracker.common.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val logoutUseCase: LogoutUseCase,
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
                            isSubmitting = false,
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

            HabitListViewIntent.ClickSettingsButton -> {
                viewModelScope.launch {
                    _viewEffect.emit(HabitListViewEffect.NavigateToSettingsScreen)
                }
            }

            HabitListViewIntent.PullToRefresh -> {
                _viewState.update {
                    it.copy(isRefreshing = true)
                }
            }

            HabitListViewIntent.RefreshComplete -> {
                _viewState.update {
                    it.copy(isRefreshing = false)
                }
            }

            is HabitListViewIntent.ClickHabit, is HabitListViewIntent.ClickCheckInHabitButton -> {
                viewModelScope.launch {
                    val habitId = when (intent) {
                        is HabitListViewIntent.ClickHabit -> intent.habitId
                        is HabitListViewIntent.ClickCheckInHabitButton -> intent.habitId
                        else -> ""
                    }
                    val habitName = when (intent) {
                        is HabitListViewIntent.ClickHabit -> intent.habitName
                        is HabitListViewIntent.ClickCheckInHabitButton -> intent.habitName
                        else -> ""
                    }
                    if (habitName.isNotBlank()) {
                        _viewEffect.emit(
                            HabitListViewEffect.AnnounceForAccessibility(
                                application.getString(
                                    R.string.habit_list_checking_in_announcement,
                                    habitName
                                )
                            )
                        )
                    }
                    _viewState.update {
                        it.copy(checkingInHabitIds = it.checkingInHabitIds + habitId)
                    }
                    val result = checkInUseCase(habitId)
                    _viewState.update {
                        it.copy(checkingInHabitIds = it.checkingInHabitIds - habitId)
                    }
                    when (result) {
                        is CheckInResult.Success -> {
                            if (habitName.isNotBlank()) {
                                _viewEffect.emit(
                                    HabitListViewEffect.AnnounceForAccessibility(
                                        application.getString(
                                            R.string.habit_list_check_in_success_announcement,
                                            habitName
                                        )
                                    )
                                )
                            }
                        }

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

            is HabitListViewIntent.ClickDeleteHabitButton -> {
                _viewState.update {
                    it.copy(
                        deleteHabitDialogViewState = DeleteHabitDialogViewState(
                            showDialog = true,
                            habitId = intent.habitId,
                            habitName = intent.habitName,
                            isSubmitting = false
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
                if (!_viewState.value.habitEditorBottomSheetViewState.isSubmitting) {
                    _viewState.update {
                        it.copy(
                            habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                                showBottomSheet = false,
                                textFieldViewState = HabitListTextFieldViewState(),
                                isSubmitting = false
                            )
                        )
                    }
                }
            }

            HabitListViewIntent.SubmitHabitEditorChanges -> {
                val currentState = _viewState.value
                if (currentState.habitEditorBottomSheetViewState.isSubmitting) return

                viewModelScope.launch {
                    _viewState.update {
                        it.copy(
                            habitEditorBottomSheetViewState = it.habitEditorBottomSheetViewState.copy(
                                isSubmitting = true
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

            HabitListViewIntent.DismissDeleteHabitDialog -> {
                if (!_viewState.value.deleteHabitDialogViewState.isSubmitting) {
                    _viewState.update {
                        it.copy(deleteHabitDialogViewState = DeleteHabitDialogViewState())
                    }
                }
            }

            HabitListViewIntent.ConfirmDeleteHabit -> {
                val currentDeleteDialogState = _viewState.value.deleteHabitDialogViewState
                val habitId = currentDeleteDialogState.habitId
                val habitName = currentDeleteDialogState.habitName
                if (currentDeleteDialogState.isSubmitting || habitId == null) return

                viewModelScope.launch {
                    _viewState.update {
                        it.copy(
                            deleteHabitDialogViewState = it.deleteHabitDialogViewState.copy(
                                isSubmitting = true
                            )
                        )
                    }
                    if (habitName.isNotBlank()) {
                        _viewEffect.emit(
                            HabitListViewEffect.AnnounceForAccessibility(
                                application.getString(R.string.habit_list_deleting_announcement, habitName)
                            )
                        )
                    }

                    when (deleteHabitUseCase(habitId)) {
                        is DeleteHabitResult.Success -> {
                            _viewState.update {
                                it.copy(deleteHabitDialogViewState = DeleteHabitDialogViewState())
                            }
                            if (habitName.isNotBlank()) {
                                _viewEffect.emit(
                                    HabitListViewEffect.AnnounceForAccessibility(
                                        application.getString(R.string.habit_list_delete_success_announcement, habitName)
                                    )
                                )
                            }
                        }

                        is DeleteHabitResult.Error -> {
                            _viewState.update {
                                it.copy(deleteHabitDialogViewState = DeleteHabitDialogViewState())
                            }
                            _viewEffect.emit(
                                HabitListViewEffect.ShowSnackbar(
                                    application.getString(R.string.delete_habit_error)
                                )
                            )
                        }
                    }
                }
            }

            HabitListViewIntent.ClickBackButton -> {
                _viewState.update { it.copy(showLogoutDialog = true) }
            }

            HabitListViewIntent.DismissLogoutDialog -> {
                _viewState.update { it.copy(showLogoutDialog = false) }
            }

            HabitListViewIntent.ConfirmLogout -> {
                _viewState.update { it.copy(showLogoutDialog = false) }
                viewModelScope.launch {
                    logoutUseCase()
                    _viewEffect.emit(HabitListViewEffect.NavigateToLoginScreen)
                }
            }
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
                            isSubmitting = false,
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
                            isSubmitting = false
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
                            isSubmitting = false,
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
                            isSubmitting = false
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
                            isSubmitting = false
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
                            isSubmitting = false
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
