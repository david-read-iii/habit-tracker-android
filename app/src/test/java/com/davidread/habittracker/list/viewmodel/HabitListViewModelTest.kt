package com.davidread.habittracker.list.viewmodel

import android.app.Application
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.davidread.habittracker.R
import com.davidread.habittracker.list.mapper.HabitMapper
import com.davidread.habittracker.list.model.CheckInResult
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.usecase.CheckInUseCase
import com.davidread.habittracker.list.usecase.GetHabitsUseCase
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
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
    private val checkInUseCase = mockk<CheckInUseCase>()
    private val application = mockk<Application>()

    private lateinit var viewModel: HabitListViewModel

    @Before
    fun setUp() {
        every { getHabitsUseCase() } returns emptyFlow()
        every { application.getString(R.string.check_in_already_checked_in) } returns ALREADY_CHECKED_IN_MESSAGE
        every { application.getString(R.string.check_in_generic_error) } returns GENERIC_ERROR_MESSAGE
        viewModel = HabitListViewModel(getHabitsUseCase, habitMapper, checkInUseCase, application)
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
        Assert.assertTrue(actual.checkingInHabitIds.isEmpty())
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

    @Test
    fun test_processIntent_ClickHabit_success() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            val checkInResultDeferred = CompletableDeferred<CheckInResult>()
            coEvery { checkInUseCase(HABIT_ID) } coAnswers { checkInResultDeferred.await() }

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())

            viewModel.processIntent(HabitListViewIntent.ClickHabit(HABIT_ID))

            Assert.assertEquals(setOf(HABIT_ID), viewStateTurbine.expectMostRecentItem().checkingInHabitIds)

            checkInResultDeferred.complete(CheckInResult.Success)

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            viewEffectTurbine.expectNoEvents()
            coVerify { checkInUseCase(HABIT_ID) }
        }
    }

    @Test
    fun test_processIntent_ClickHabit_alreadyCheckedInError() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { checkInUseCase(HABIT_ID) } returns CheckInResult.AlreadyCheckedInError

            viewModel.processIntent(HabitListViewIntent.ClickHabit(HABIT_ID))

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.ShowSnackbar(ALREADY_CHECKED_IN_MESSAGE),
                viewEffectTurbine.awaitItem()
            )
            coVerify { checkInUseCase(HABIT_ID) }
        }
    }

    @Test
    fun test_processIntent_ClickHabit_genericError() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { checkInUseCase(HABIT_ID) } returns CheckInResult.GenericError

            viewModel.processIntent(HabitListViewIntent.ClickHabit(HABIT_ID))

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.ShowSnackbar(GENERIC_ERROR_MESSAGE),
                viewEffectTurbine.awaitItem()
            )
            coVerify { checkInUseCase(HABIT_ID) }
        }
    }

    companion object {
        private const val HABIT_ID = "habit_id"
        private const val ALREADY_CHECKED_IN_MESSAGE = "You've already checked in for this habit today."
        private const val GENERIC_ERROR_MESSAGE = "Unable to check in right now. Please try again."
    }
}
