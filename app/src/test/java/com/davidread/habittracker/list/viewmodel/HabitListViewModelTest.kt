package com.davidread.habittracker.list.viewmodel

import app.cash.turbine.test
import com.davidread.habittracker.list.mapper.HabitMapper
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.usecase.GetHabitsUseCase
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HabitListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getHabitsUseCase = mockk<GetHabitsUseCase>()
    private val habitMapper = mockk<HabitMapper>()

    private lateinit var viewModel: HabitListViewModel

    @Before
    fun setUp() {
        every { getHabitsUseCase() } returns emptyFlow()
        viewModel = HabitListViewModel(getHabitsUseCase, habitMapper)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_viewState_initialState() {
        val actual = viewModel.viewState.value

        Assert.assertFalse(actual.showLoading)
        Assert.assertFalse(actual.alertDialogViewState.showDialog)
        Assert.assertNull(actual.alertDialogViewState.message)
    }

    @Test
    fun test_habitsPagingDataFlow_initialization() {
        verify { getHabitsUseCase() }
    }

    @Test
    fun test_processIntent_ClickSettingsButton() = runTest {
        val intent = HabitListViewIntent.ClickSettingsButton

        viewModel.viewEffect.test {
            viewModel.processIntent(intent)
            Assert.assertEquals(HabitListViewEffect.NavigateToSettingsScreen, awaitItem())
        }
    }

    @Test
    fun test_processIntent_ClickAlertDialogButton() {
        val intent = HabitListViewIntent.ClickAlertDialogButton

        viewModel.processIntent(intent)

        Assert.assertFalse(viewModel.viewState.value.alertDialogViewState.showDialog)
    }
}
