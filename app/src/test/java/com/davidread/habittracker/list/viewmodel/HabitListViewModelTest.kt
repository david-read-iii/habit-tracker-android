package com.davidread.habittracker.list.viewmodel

import android.app.Application
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.davidread.habittracker.R
import com.davidread.habittracker.list.mapper.HabitMapper
import com.davidread.habittracker.list.model.CheckInResult
import com.davidread.habittracker.list.model.CreateHabitResult
import com.davidread.habittracker.list.model.DeleteHabitResult
import com.davidread.habittracker.list.model.EditorState
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.model.UpdateHabitResult
import com.davidread.habittracker.list.usecase.CheckInUseCase
import com.davidread.habittracker.list.usecase.CreateHabitUseCase
import com.davidread.habittracker.list.usecase.DeleteHabitUseCase
import com.davidread.habittracker.list.usecase.GetHabitsUseCase
import com.davidread.habittracker.list.usecase.UpdateHabitUseCase
import com.davidread.habittracker.testutil.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
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
    private val updateHabitUseCase = mockk<UpdateHabitUseCase>()
    private val deleteHabitUseCase = mockk<DeleteHabitUseCase>()
    private val application = mockk<Application>()

    private lateinit var viewModel: HabitListViewModel

    @Before
    fun setUp() {
        every { getHabitsUseCase() } returns emptyFlow()
        every { application.getString(R.string.check_in_already_checked_in) } returns ALREADY_CHECKED_IN_MESSAGE
        every { application.getString(R.string.check_in_generic_error) } returns GENERIC_ERROR_MESSAGE
        every { application.getString(R.string.habit_list_add_habit_sheet_title) } returns
            ADD_HABIT_SHEET_TITLE
        every { application.getString(R.string.habit_list_edit_habit_sheet_title) } returns
            EDIT_HABIT_SHEET_TITLE
        every { application.getString(R.string.habit_list_add_habit_submit) } returns
            ADD_HABIT_SUBMIT_TEXT
        every { application.getString(R.string.habit_list_edit_habit_submit) } returns
            EDIT_HABIT_SUBMIT_TEXT
        every { application.getString(R.string.habit_list_add_habit_name_error) } returns INVALID_HABIT_NAME_MESSAGE
        every { application.getString(R.string.create_habit_success) } returns CREATE_HABIT_SUCCESS_MESSAGE
        every { application.getString(R.string.create_habit_error) } returns CREATE_HABIT_ERROR_MESSAGE
        every { application.getString(R.string.update_habit_error) } returns UPDATE_HABIT_ERROR_MESSAGE
        every { application.getString(R.string.delete_habit_error) } returns DELETE_HABIT_ERROR_MESSAGE
        viewModel = HabitListViewModel(
            getHabitsUseCase,
            habitMapper,
            checkInUseCase,
            createHabitUseCase,
            updateHabitUseCase,
            deleteHabitUseCase,
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

        Assert.assertTrue(actual.checkingInHabitIds.isEmpty())
    }

    @Test
    fun test_habitsPagingDataFlow_initialization() {
        verify { getHabitsUseCase() }
    }

    @Test
    fun test_processIntent_PullToRefresh_setsIsRefreshingTrue() {
        viewModel.processIntent(HabitListViewIntent.PullToRefresh)

        val actual = viewModel.viewState.value

        Assert.assertTrue(actual.isRefreshing)
        Assert.assertTrue(actual.checkingInHabitIds.isEmpty())
    }

    @Test
    fun test_processIntent_RefreshComplete_setsIsRefreshingFalse() {
        viewModel.processIntent(HabitListViewIntent.PullToRefresh)
        Assert.assertTrue(viewModel.viewState.value.isRefreshing)

        viewModel.processIntent(HabitListViewIntent.RefreshComplete)

        val actual = viewModel.viewState.value

        Assert.assertFalse(actual.isRefreshing)
        Assert.assertTrue(actual.checkingInHabitIds.isEmpty())
    }

    @Test
    fun test_processIntent_ClickAddHabitButton_showsHabitEditorBottomSheet() = runTest {
        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)

        val habitEditorBottomSheetViewState = viewModel.viewState.value.habitEditorBottomSheetViewState

        Assert.assertTrue(habitEditorBottomSheetViewState.showBottomSheet)
        Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.value)
        Assert.assertFalse(habitEditorBottomSheetViewState.textFieldViewState.isError)
        Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.errorMessage)
        Assert.assertFalse(habitEditorBottomSheetViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_ChangeHabitEditorNameValue() {
        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
        coEvery { createHabitUseCase(INVALID_HABIT_NAME) } returns CreateHabitResult.InvalidHabitName
        viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(INVALID_HABIT_NAME))
        viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)
        viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(UPDATED_HABIT_NAME))

        val textFieldViewState =
            viewModel.viewState.value.habitEditorBottomSheetViewState.textFieldViewState

        Assert.assertEquals(UPDATED_HABIT_NAME, textFieldViewState.value)
        Assert.assertFalse(textFieldViewState.isError)
        Assert.assertEquals("", textFieldViewState.errorMessage)
    }

    @Test
    fun test_processIntent_DismissHabitEditorBottomSheet() {
        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
        viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(HABIT_NAME))
        viewModel.processIntent(HabitListViewIntent.DismissHabitEditorBottomSheet)

        val habitEditorBottomSheetViewState = viewModel.viewState.value.habitEditorBottomSheetViewState

        Assert.assertFalse(habitEditorBottomSheetViewState.showBottomSheet)
        Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.value)
        Assert.assertFalse(habitEditorBottomSheetViewState.textFieldViewState.isError)
        Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.errorMessage)
        Assert.assertFalse(habitEditorBottomSheetViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_DismissHabitEditorBottomSheet_doesNothingWhileSubmittingHabitEditorChanges() = runTest {
        val createHabitResultDeferred = CompletableDeferred<CreateHabitResult>()
        coEvery { createHabitUseCase(HABIT_NAME) } coAnswers { createHabitResultDeferred.await() }

        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
        viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(HABIT_NAME))
        viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)
        advanceUntilIdle()

        Assert.assertTrue(viewModel.viewState.value.habitEditorBottomSheetViewState.isSubmitting)

        viewModel.processIntent(HabitListViewIntent.DismissHabitEditorBottomSheet)

        val habitEditorBottomSheetViewState = viewModel.viewState.value.habitEditorBottomSheetViewState
        Assert.assertTrue(habitEditorBottomSheetViewState.showBottomSheet)
        Assert.assertEquals(HABIT_NAME, habitEditorBottomSheetViewState.textFieldViewState.value)
        Assert.assertTrue(habitEditorBottomSheetViewState.isSubmitting)

        createHabitResultDeferred.complete(CreateHabitResult.Success)
        advanceUntilIdle()

        Assert.assertFalse(viewModel.viewState.value.habitEditorBottomSheetViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_SubmitHabitEditorChanges_addMode_success() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            val createHabitResultDeferred = CompletableDeferred<CreateHabitResult>()
            coEvery { createHabitUseCase(HABIT_NAME) } coAnswers { createHabitResultDeferred.await() }

            viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
            viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)
            advanceUntilIdle()

            val creatingHabitState =
                viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertTrue(creatingHabitState.showBottomSheet)
            Assert.assertEquals(HABIT_NAME, creatingHabitState.textFieldViewState.value)
            Assert.assertTrue(creatingHabitState.isSubmitting)

            createHabitResultDeferred.complete(CreateHabitResult.Success)
            advanceUntilIdle()

            val completedState =
                viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertFalse(completedState.showBottomSheet)
            Assert.assertEquals("", completedState.textFieldViewState.value)
            Assert.assertFalse(completedState.textFieldViewState.isError)
            Assert.assertEquals("", completedState.textFieldViewState.errorMessage)
            Assert.assertFalse(completedState.isSubmitting)
            coVerify { createHabitUseCase(HABIT_NAME) }
        }
    }

    @Test
    fun test_processIntent_SubmitHabitEditorChanges_addMode_invalidHabitName() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { createHabitUseCase(INVALID_HABIT_NAME) } returns CreateHabitResult.InvalidHabitName

            viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
            viewModel.processIntent(
                HabitListViewIntent.ChangeHabitEditorNameValue(
                    INVALID_HABIT_NAME
                )
            )
            viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)

            val habitEditorBottomSheetViewState =
                viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertTrue(habitEditorBottomSheetViewState.showBottomSheet)
            Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.value)
            Assert.assertTrue(habitEditorBottomSheetViewState.textFieldViewState.isError)
            Assert.assertEquals(
                INVALID_HABIT_NAME_MESSAGE,
                habitEditorBottomSheetViewState.textFieldViewState.errorMessage
            )
            Assert.assertFalse(habitEditorBottomSheetViewState.isSubmitting)
            viewEffectTurbine.expectNoEvents()
            coVerify { createHabitUseCase(INVALID_HABIT_NAME) }
        }
    }

    @Test
    fun test_processIntent_SubmitHabitEditorChanges_addMode_error() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { createHabitUseCase(HABIT_NAME) } returns CreateHabitResult.Error

            viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
            viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)

            val habitEditorBottomSheetViewState =
                viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertFalse(habitEditorBottomSheetViewState.showBottomSheet)
            Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.value)
            Assert.assertFalse(habitEditorBottomSheetViewState.textFieldViewState.isError)
            Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.errorMessage)
            Assert.assertFalse(habitEditorBottomSheetViewState.isSubmitting)
            Assert.assertEquals(
                HabitListViewEffect.ShowSnackbar(CREATE_HABIT_ERROR_MESSAGE),
                viewEffectTurbine.awaitItem()
            )
            coVerify { createHabitUseCase(HABIT_NAME) }
        }
    }

    @Test
    fun test_processIntent_SubmitHabitEditorChanges_doesNotSubmitAgainWhileInFlight() = runTest {
        val createHabitResultDeferred = CompletableDeferred<CreateHabitResult>()
        coEvery { createHabitUseCase(HABIT_NAME) } coAnswers { createHabitResultDeferred.await() }

        viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton)
        viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(HABIT_NAME))
        viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)
        advanceUntilIdle()

        Assert.assertTrue(viewModel.viewState.value.habitEditorBottomSheetViewState.isSubmitting)

        viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)

        coVerify(exactly = 1) { createHabitUseCase(HABIT_NAME) }

        createHabitResultDeferred.complete(CreateHabitResult.Success)
        advanceUntilIdle()

        Assert.assertFalse(viewModel.viewState.value.habitEditorBottomSheetViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_SubmitHabitEditorChanges_editMode_success() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            val updateHabitResultDeferred = CompletableDeferred<UpdateHabitResult>()
            coEvery {
                updateHabitUseCase(
                    id = HABIT_ID,
                    name = UPDATED_HABIT_NAME
                )
            } coAnswers { updateHabitResultDeferred.await() }

            viewModel.processIntent(HabitListViewIntent.ClickEditHabitButton(HABIT_ID, HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(UPDATED_HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)
            advanceUntilIdle()

            val editingState = viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertTrue(editingState.showBottomSheet)
            Assert.assertEquals(UPDATED_HABIT_NAME, editingState.textFieldViewState.value)
            Assert.assertTrue(editingState.isSubmitting)
            Assert.assertEquals(EditorState.Edit(HABIT_ID), editingState.editorState)

            updateHabitResultDeferred.complete(UpdateHabitResult.Success)
            advanceUntilIdle()

            val completedState = viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertFalse(completedState.showBottomSheet)
            Assert.assertEquals("", completedState.textFieldViewState.value)
            Assert.assertFalse(completedState.textFieldViewState.isError)
            Assert.assertEquals("", completedState.textFieldViewState.errorMessage)
            Assert.assertFalse(completedState.isSubmitting)
            viewEffectTurbine.expectNoEvents()
            coVerify {
                updateHabitUseCase(
                    id = HABIT_ID,
                    name = UPDATED_HABIT_NAME
                )
            }
        }
    }

    @Test
    fun test_processIntent_SubmitHabitEditorChanges_editMode_invalidHabitName() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery {
                updateHabitUseCase(
                    id = HABIT_ID,
                    name = INVALID_HABIT_NAME
                )
            } returns UpdateHabitResult.InvalidHabitName

            viewModel.processIntent(HabitListViewIntent.ClickEditHabitButton(HABIT_ID, HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(INVALID_HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)

            val habitEditorBottomSheetViewState =
                viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertTrue(habitEditorBottomSheetViewState.showBottomSheet)
            Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.value)
            Assert.assertTrue(habitEditorBottomSheetViewState.textFieldViewState.isError)
            Assert.assertEquals(
                INVALID_HABIT_NAME_MESSAGE,
                habitEditorBottomSheetViewState.textFieldViewState.errorMessage
            )
            Assert.assertFalse(habitEditorBottomSheetViewState.isSubmitting)
            Assert.assertEquals(EditorState.Edit(HABIT_ID), habitEditorBottomSheetViewState.editorState)
            viewEffectTurbine.expectNoEvents()
            coVerify {
                updateHabitUseCase(
                    id = HABIT_ID,
                    name = INVALID_HABIT_NAME
                )
            }
        }
    }

    @Test
    fun test_processIntent_SubmitHabitEditorChanges_editMode_error() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery {
                updateHabitUseCase(
                    id = HABIT_ID,
                    name = UPDATED_HABIT_NAME
                )
            } returns UpdateHabitResult.Error

            viewModel.processIntent(HabitListViewIntent.ClickEditHabitButton(HABIT_ID, HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.ChangeHabitEditorNameValue(UPDATED_HABIT_NAME))
            viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges)

            val habitEditorBottomSheetViewState =
                viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertFalse(habitEditorBottomSheetViewState.showBottomSheet)
            Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.value)
            Assert.assertFalse(habitEditorBottomSheetViewState.textFieldViewState.isError)
            Assert.assertEquals("", habitEditorBottomSheetViewState.textFieldViewState.errorMessage)
            Assert.assertFalse(habitEditorBottomSheetViewState.isSubmitting)
            Assert.assertEquals(
                HabitListViewEffect.ShowSnackbar(UPDATE_HABIT_ERROR_MESSAGE),
                viewEffectTurbine.awaitItem()
            )
            coVerify {
                updateHabitUseCase(
                    id = HABIT_ID,
                    name = UPDATED_HABIT_NAME
                )
            }
        }
    }

    @Test
    fun test_processIntent_ClickDeleteHabitButton_showsDeleteDialog() {
        viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID))

        val deleteDialogViewState = viewModel.viewState.value.deleteHabitDialogViewState

        Assert.assertTrue(deleteDialogViewState.showDialog)
        Assert.assertEquals(HABIT_ID, deleteDialogViewState.habitId)
        Assert.assertFalse(deleteDialogViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_DismissDeleteHabitDialog_doesNothingWhileSubmitting() = runTest {
        val deleteHabitResultDeferred = CompletableDeferred<DeleteHabitResult>()
        coEvery { deleteHabitUseCase(HABIT_ID) } coAnswers { deleteHabitResultDeferred.await() }

        viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID))
        viewModel.processIntent(HabitListViewIntent.ConfirmDeleteHabit)
        advanceUntilIdle()

        Assert.assertTrue(viewModel.viewState.value.deleteHabitDialogViewState.isSubmitting)

        viewModel.processIntent(HabitListViewIntent.DismissDeleteHabitDialog)

        val deleteDialogViewState = viewModel.viewState.value.deleteHabitDialogViewState
        Assert.assertTrue(deleteDialogViewState.showDialog)
        Assert.assertEquals(HABIT_ID, deleteDialogViewState.habitId)
        Assert.assertTrue(deleteDialogViewState.isSubmitting)

        deleteHabitResultDeferred.complete(DeleteHabitResult.Success)
        advanceUntilIdle()
    }

    @Test
    fun test_processIntent_ConfirmDeleteHabit_success() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            val deleteHabitResultDeferred = CompletableDeferred<DeleteHabitResult>()
            coEvery { deleteHabitUseCase(HABIT_ID) } coAnswers { deleteHabitResultDeferred.await() }

            viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID))
            viewModel.processIntent(HabitListViewIntent.ConfirmDeleteHabit)
            advanceUntilIdle()

            val deletingState = viewStateTurbine.expectMostRecentItem().deleteHabitDialogViewState
            Assert.assertTrue(deletingState.showDialog)
            Assert.assertEquals(HABIT_ID, deletingState.habitId)
            Assert.assertTrue(deletingState.isSubmitting)

            viewModel.processIntent(HabitListViewIntent.ConfirmDeleteHabit)
            coVerify(exactly = 1) { deleteHabitUseCase(HABIT_ID) }

            deleteHabitResultDeferred.complete(DeleteHabitResult.Success)
            advanceUntilIdle()

            val completedState = viewStateTurbine.expectMostRecentItem().deleteHabitDialogViewState
            Assert.assertFalse(completedState.showDialog)
            Assert.assertEquals(null, completedState.habitId)
            Assert.assertFalse(completedState.isSubmitting)
            viewEffectTurbine.expectNoEvents()
        }
    }

    @Test
    fun test_processIntent_ConfirmDeleteHabit_error() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { deleteHabitUseCase(HABIT_ID) } returns DeleteHabitResult.Error

            viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID))
            viewModel.processIntent(HabitListViewIntent.ConfirmDeleteHabit)

            val deleteDialogViewState = viewStateTurbine.expectMostRecentItem().deleteHabitDialogViewState
            Assert.assertFalse(deleteDialogViewState.showDialog)
            Assert.assertEquals(null, deleteDialogViewState.habitId)
            Assert.assertFalse(deleteDialogViewState.isSubmitting)
            Assert.assertEquals(
                HabitListViewEffect.ShowSnackbar(DELETE_HABIT_ERROR_MESSAGE),
                viewEffectTurbine.awaitItem()
            )
            coVerify { deleteHabitUseCase(HABIT_ID) }
        }
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
        private const val ADD_HABIT_SHEET_TITLE = "Add habit"
        private const val EDIT_HABIT_SHEET_TITLE = "Edit habit"
        private const val ADD_HABIT_SUBMIT_TEXT = "Add"
        private const val EDIT_HABIT_SUBMIT_TEXT = "Save"
        private const val ALREADY_CHECKED_IN_MESSAGE =
            "You've already checked in for this habit today."
        private const val GENERIC_ERROR_MESSAGE = "Unable to check in right now. Please try again."
        private const val INVALID_HABIT_NAME_MESSAGE = "Please enter a habit name"
        private const val CREATE_HABIT_SUCCESS_MESSAGE = "Habit added successfully."
        private const val CREATE_HABIT_ERROR_MESSAGE = "Failed to add habit. Please try again."
        private const val UPDATE_HABIT_ERROR_MESSAGE = "Failed to update habit. Please try again."
        private const val DELETE_HABIT_ERROR_MESSAGE = "Failed to delete habit. Please try again."
    }
}
