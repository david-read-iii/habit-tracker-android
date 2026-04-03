package com.davidread.habittracker.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.model.HabitListViewState
import com.davidread.habittracker.list.usecase.GetHabitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(getHabitsUseCase: GetHabitsUseCase) : ViewModel() {

    private val _viewState = MutableStateFlow(HabitListViewState())
    val viewState: StateFlow<HabitListViewState>
        get() = _viewState

    private val _viewEffect = MutableSharedFlow<HabitListViewEffect>()
    val viewEffect: SharedFlow<HabitListViewEffect>
        get() = _viewEffect

    val habitsPagingDataFlow: Flow<PagingData<HabitEntity>> =
        getHabitsUseCase().cachedIn(viewModelScope)

    fun processIntent(intent: HabitListViewIntent) {
        when (intent) {
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

            else -> {} // TODO: Handle other intents.
        }
    }
}
