package com.davidread.habittracker.list.viewmodel

import com.davidread.habittracker.list.mapper.HabitMapper
import com.davidread.habittracker.list.usecase.GetHabitsUseCase
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule

class HabitListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getHabitsUseCase = mockk<GetHabitsUseCase> {
        every { this@mockk.invoke() } returns emptyFlow()
    }
    private val habitMapper = mockk<HabitMapper>()

    private val viewModel = HabitListViewModel(getHabitsUseCase, habitMapper)

    // TODO: Define tests here.
}
