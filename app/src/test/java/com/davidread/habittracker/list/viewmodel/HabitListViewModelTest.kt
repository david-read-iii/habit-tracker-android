package com.davidread.habittracker.list.viewmodel

import android.app.Application
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.davidread.habittracker.R
import com.davidread.habittracker.list.mapper.HabitMapper
import com.davidread.habittracker.list.model.CheckInResult
import com.davidread.habittracker.list.model.CreateHabitResult
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.usecase.CheckInUseCase
import com.davidread.habittracker.list.usecase.CreateHabitUseCase
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HabitListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getHabitsUseCase = mockk<GetHabitsUseCase>()
    private val habitMapper = mockk<HabitMapper>()
    private val checkInUseCase = mockk<CheckInUseCase>()
    private val createHabitUseCase = mockk<CreateHabitUseCase>()
    private val application = mockk<Application>()

    private lateinit var viewModel: HabitListViewModel

    @Before
    fun setUp() {
        every { getHabitsUseCase() } returns emptyFlow()
        every { application.getString(R.string.check_in_already_checked_in) } returns ALREADY_CHECKED_IN_MESSAGE
        every { application.getString(R.string.check_in_generic_error) } returns GENERIC_ERROR_MESSAGE
        every { application.getString(R.string.habit_list_add_habit_name_error) } returns INVALID_HABIT_NAME_MESSAGE
        every { application.getString(R.string.create_habit_success) } returns CREATE_HABIT_SUCCESS_MESSAGE
        every { application.getString(R.string.create_habit_error) } returns CREATE_HABIT_ERROR_MESSAGE
        viewModel = HabitListViewModel(
            getHabitsUseCase,
            habitMapper,
            checkInUseCase,
            createHabitUseCase,
            application
        )
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
    fun test_processIntent_ClickAddHabitButton() = runTest {
        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)

        val addHabitViewState = viewModel.viewState.value.addHabitViewState

        Assert.assertTrue(addHabitViewState.showBottomSheet)
        Assert.assertEquals("", addHabitViewState.textFieldViewState.value)
        Assert.assertFalse(addHabitViewState.textFieldViewState.isError)
        Assert.assertEquals("", addHabitViewState.textFieldViewState.errorMessage)
        Assert.assertFalse(addHabitViewState.isCreatingHabit)
    }

    @Test
    fun test_processIntent_ChangeHabitNameValue() {
        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
        coEvery { createHabitUseCase(INVALID_HABIT_NAME) } returns CreateHabitResult.InvalidHabitName
        viewModel.processIntent(HabitListViewIntent.ChangeHabitNameValue(INVALID_HABIT_NAME))
        viewModel.processIntent(HabitListViewIntent.SubmitAddHabit)
        viewModel.processIntent(HabitListViewIntent.ChangeHabitNameValue(UPDATED_HABIT_NAME))

        val textFieldViewState = viewModel.viewState.value.addHabitViewState.textFieldViewState

        Assert.assertEquals(UPDATED_HABIT_NAME, textFieldViewState.value)
        Assert.assertFalse(textFieldViewState.isError)
        Assert.assertEquals("", textFieldViewState.errorMessage)
    }

    @Test
    fun test_processIntent_DismissAddHabitBottomSheet() {
        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
        viewModel.processIntent(HabitListViewIntent.ChangeHabitNameValue(HABIT_NAME))
        viewModel.processIntent(HabitListViewIntent.DismissAddHabitBottomSheet)

        val addHabitViewState = viewModel.viewState.value.addHabitViewState

        Assert.assertFalse(addHabitViewState.showBottomSheet)
        Assert.assertEquals("", addHabitViewState.textFieldViewState.value)
        Assert.assertFalse(addHabitViewState.textFieldViewState.isError)
        Assert.assertEquals("", addHabitViewState.textFieldViewState.errorMessage)
        Assert.assertFalse(addHabitViewState.isCreatingHabit)
    }

    @Test
    fun test_processIntent_DismissAddHabitBottomSheet_doesNothingWhileCreatingHabit() = runTest {
        val createHabitResultDeferred = CompletableDeferred<CreateHabitResult>()
        coEvery { createHabitUseCase(HABIT_NAME) } coAnswers { createHabitResultDeferred.await() }

        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
        viewModel.processIntent(HabitListViewIntent.ChangeHabitNameValue(HABIT_NAME))
        viewModel.processIntent(HabitListViewIntent.SubmitAddHabit)
        advanceUntilIdle()

        Assert.assertTrue(viewModel.viewState.value.addHabitViewState.isCreatingHabit)

        viewModel.processIntent(HabitListViewIntent.DismissAddHabitBottomSheet)

        val addHabitViewState = viewModel.viewState.value.addHabitViewState
        Assert.assertTrue(addHabitViewState.showBottomSheet)
        Assert.assertEquals(HABIT_NAME, addHabitViewState.textFieldViewState.value)
        Assert.assertTrue(addHabitViewState.isCreatingHabit)

        createHabitResultDeferred.complete(CreateHabitResult.Success)
        advanceUntilIdle()

        Assert.assertFalse(viewModel.viewState.value.addHabitViewState.isCreatingHabit)
    }

    @Test
    fun test_processIntent_SubmitAddHabit_success() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            val createHabitResultDeferred = CompletableDeferred<CreateHabitResult>()
            coEvery { createHabitUseCase(HABIT_NAME) } coAnswers { createHabitResultDeferred.await() }

            viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
            viewModel.processIntent(HabitListViewIntent.ChangeHabitNameValue(HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.SubmitAddHabit)
            advanceUntilIdle()

            val creatingHabitState = viewStateTurbine.expectMostRecentItem().addHabitViewState
            Assert.assertTrue(creatingHabitState.showBottomSheet)
            Assert.assertEquals(HABIT_NAME, creatingHabitState.textFieldViewState.value)
            Assert.assertTrue(creatingHabitState.isCreatingHabit)

            createHabitResultDeferred.complete(CreateHabitResult.Success)
            advanceUntilIdle()

            val completedState = viewStateTurbine.expectMostRecentItem().addHabitViewState
            Assert.assertFalse(completedState.showBottomSheet)
            Assert.assertEquals("", completedState.textFieldViewState.value)
            Assert.assertFalse(completedState.textFieldViewState.isError)
            Assert.assertEquals("", completedState.textFieldViewState.errorMessage)
            Assert.assertFalse(completedState.isCreatingHabit)
            coVerify { createHabitUseCase(HABIT_NAME) }
        }
    }

    @Test
    fun test_processIntent_SubmitAddHabit_invalidHabitName() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { createHabitUseCase(INVALID_HABIT_NAME) } returns CreateHabitResult.InvalidHabitName

            viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
            viewModel.processIntent(HabitListViewIntent.ChangeHabitNameValue(INVALID_HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.SubmitAddHabit)

            val addHabitViewState = viewStateTurbine.expectMostRecentItem().addHabitViewState
            Assert.assertTrue(addHabitViewState.showBottomSheet)
            Assert.assertEquals("", addHabitViewState.textFieldViewState.value)
            Assert.assertTrue(addHabitViewState.textFieldViewState.isError)
            Assert.assertEquals(INVALID_HABIT_NAME_MESSAGE, addHabitViewState.textFieldViewState.errorMessage)
            Assert.assertFalse(addHabitViewState.isCreatingHabit)
            viewEffectTurbine.expectNoEvents()
            coVerify { createHabitUseCase(INVALID_HABIT_NAME) }
        }
    }

    @Test
    fun test_processIntent_SubmitAddHabit_error() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { createHabitUseCase(HABIT_NAME) } returns CreateHabitResult.Error

            viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
            viewModel.processIntent(HabitListViewIntent.ChangeHabitNameValue(HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.SubmitAddHabit)

            val addHabitViewState = viewStateTurbine.expectMostRecentItem().addHabitViewState
            Assert.assertTrue(addHabitViewState.showBottomSheet)
            Assert.assertEquals(HABIT_NAME, addHabitViewState.textFieldViewState.value)
            Assert.assertFalse(addHabitViewState.textFieldViewState.isError)
            Assert.assertEquals("", addHabitViewState.textFieldViewState.errorMessage)
            Assert.assertFalse(addHabitViewState.isCreatingHabit)
            Assert.assertEquals(
                HabitListViewEffect.ShowSnackbar(CREATE_HABIT_ERROR_MESSAGE),
                viewEffectTurbine.awaitItem()
            )
            coVerify { createHabitUseCase(HABIT_NAME) }
        }
    }

    @Test
    fun test_processIntent_SubmitAddHabit_doesNotCreateHabitAgainWhileAlreadyCreating() = runTest {
        val createHabitResultDeferred = CompletableDeferred<CreateHabitResult>()
        coEvery { createHabitUseCase(HABIT_NAME) } coAnswers { createHabitResultDeferred.await() }

        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
        viewModel.processIntent(HabitListViewIntent.ChangeHabitNameValue(HABIT_NAME))
        viewModel.processIntent(HabitListViewIntent.SubmitAddHabit)
        advanceUntilIdle()

        Assert.assertTrue(viewModel.viewState.value.addHabitViewState.isCreatingHabit)

        viewModel.processIntent(HabitListViewIntent.SubmitAddHabit)

        coVerify(exactly = 1) { createHabitUseCase(HABIT_NAME) }

        createHabitResultDeferred.complete(CreateHabitResult.Success)
        advanceUntilIdle()

        Assert.assertFalse(viewModel.viewState.value.addHabitViewState.isCreatingHabit)
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

            Assert.assertEquals(
                setOf(HABIT_ID),
                viewStateTurbine.expectMostRecentItem().checkingInHabitIds
            )

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

    @Test
    fun test_processIntent_ClickCheckInHabitButton_success() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            val checkInResultDeferred = CompletableDeferred<CheckInResult>()
            coEvery { checkInUseCase(HABIT_ID) } coAnswers { checkInResultDeferred.await() }

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())

            viewModel.processIntent(HabitListViewIntent.ClickCheckInHabitButton(HABIT_ID))

            Assert.assertEquals(
                setOf(HABIT_ID),
                viewStateTurbine.expectMostRecentItem().checkingInHabitIds
            )

            checkInResultDeferred.complete(CheckInResult.Success)

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            viewEffectTurbine.expectNoEvents()
            coVerify { checkInUseCase(HABIT_ID) }
        }
    }

    @Test
    fun test_processIntent_ClickCheckInHabitButton_alreadyCheckedInError() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { checkInUseCase(HABIT_ID) } returns CheckInResult.AlreadyCheckedInError

            viewModel.processIntent(HabitListViewIntent.ClickCheckInHabitButton(HABIT_ID))

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.ShowSnackbar(ALREADY_CHECKED_IN_MESSAGE),
                viewEffectTurbine.awaitItem()
            )
            coVerify { checkInUseCase(HABIT_ID) }
        }
    }

    @Test
    fun test_processIntent_ClickCheckInHabitButton_genericError() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { checkInUseCase(HABIT_ID) } returns CheckInResult.GenericError

            viewModel.processIntent(HabitListViewIntent.ClickCheckInHabitButton(HABIT_ID))

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
        private const val HABIT_NAME = "Read for 20 minutes"
        private const val UPDATED_HABIT_NAME = "Read for 30 minutes"
        private const val INVALID_HABIT_NAME = "   "
        private const val ALREADY_CHECKED_IN_MESSAGE =
            "You've already checked in for this habit today."
        private const val GENERIC_ERROR_MESSAGE = "Unable to check in right now. Please try again."
        private const val INVALID_HABIT_NAME_MESSAGE = "Please enter a habit name"
        private const val CREATE_HABIT_SUCCESS_MESSAGE = "Habit added successfully."
        private const val CREATE_HABIT_ERROR_MESSAGE = "Failed to add habit. Please try again."
    }
}
