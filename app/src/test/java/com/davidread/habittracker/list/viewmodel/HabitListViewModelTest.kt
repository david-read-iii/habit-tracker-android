package com.davidread.habittracker.list.viewmodel

import android.app.Application
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.davidread.habittracker.R
import com.davidread.habittracker.common.usecase.LogoutUseCase
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
    private val logoutUseCase = mockk<LogoutUseCase>()
    private val application = mockk<Application>()

    private lateinit var viewModel: HabitListViewModel

    @Before
    fun setUp() {
        every { getHabitsUseCase() } returns emptyFlow()
        coEvery { logoutUseCase() } returns Unit
        every { application.getString(R.string.check_in_already_checked_in) } returns ALREADY_CHECKED_IN_MESSAGE
        every { application.getString(R.string.check_in_generic_error) } returns GENERIC_ERROR_MESSAGE
        every {
            application.getString(R.string.habit_list_checking_in_announcement, HABIT_NAME)
        } returns CHECKING_IN_ANNOUNCEMENT
        every {
            application.getString(R.string.habit_list_check_in_success_announcement, HABIT_NAME)
        } returns CHECK_IN_SUCCESS_ANNOUNCEMENT
        every {
            application.getString(R.string.habit_list_delete_success_announcement, HABIT_NAME)
        } returns DELETE_SUCCESS_ANNOUNCEMENT
        every {
            application.getString(R.string.habit_list_adding_announcement, HABIT_NAME)
        } returns ADDING_ANNOUNCEMENT
        every {
            application.getString(R.string.habit_list_add_success_announcement, HABIT_NAME)
        } returns ADD_SUCCESS_ANNOUNCEMENT
        every {
            application.getString(R.string.habit_list_updating_announcement, UPDATED_HABIT_NAME)
        } returns UPDATING_ANNOUNCEMENT
        every {
            application.getString(R.string.habit_list_update_success_announcement, UPDATED_HABIT_NAME)
        } returns UPDATE_SUCCESS_ANNOUNCEMENT
        every { application.getString(R.string.habit_list_initial_loading_announcement) } returns
            INITIAL_LOADING_ANNOUNCEMENT
        every { application.getString(R.string.habit_list_prepend_loading_announcement) } returns
            PREPEND_LOADING_ANNOUNCEMENT
        every { application.getString(R.string.habit_list_prepend_success_announcement) } returns
            PREPEND_SUCCESS_ANNOUNCEMENT
        every { application.getString(R.string.habit_list_append_loading_announcement) } returns
            APPEND_LOADING_ANNOUNCEMENT
        every { application.getString(R.string.habit_list_append_success_announcement) } returns
            APPEND_SUCCESS_ANNOUNCEMENT
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
            logoutUseCase,
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
    fun test_processIntent_ReportPagingLoadStates_initialLoadingWithNoItems_emitsAnnouncement() = runTest {
        viewModel.viewEffect.test {
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 0
                )
            )
            expectNoEvents()

            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = true,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 0
                )
            )

            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(INITIAL_LOADING_ANNOUNCEMENT),
                awaitItem()
            )
        }
    }

    @Test
    fun test_processIntent_ReportPagingLoadStates_refreshLoadingWithExistingItems_doesNotEmitAnnouncement() = runTest {
        viewModel.viewEffect.test {
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = true,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 5
                )
            )

            expectNoEvents()
        }
    }

    @Test
    fun test_processIntent_ReportPagingLoadStates_appendLoading_emitsAnnouncement() = runTest {
        viewModel.viewEffect.test {
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 10
                )
            )
            expectNoEvents()

            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = true,
                    itemCount = 10
                )
            )

            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(APPEND_LOADING_ANNOUNCEMENT),
                awaitItem()
            )
        }
    }

    @Test
    fun test_processIntent_ReportPagingLoadStates_appendLoadingThenNotLoadingWithMoreItems_emitsSuccessAnnouncement() = runTest {
        viewModel.viewEffect.test {
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 10
                )
            )
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = true,
                    itemCount = 10
                )
            )
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(APPEND_LOADING_ANNOUNCEMENT),
                awaitItem()
            )

            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 15
                )
            )

            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(APPEND_SUCCESS_ANNOUNCEMENT),
                awaitItem()
            )
        }
    }

    @Test
    fun test_processIntent_ReportPagingLoadStates_appendLoadingThenNotLoadingWithoutNewItems_doesNotEmitSuccessAnnouncement() = runTest {
        viewModel.viewEffect.test {
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 10
                )
            )
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = true,
                    itemCount = 10
                )
            )
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(APPEND_LOADING_ANNOUNCEMENT),
                awaitItem()
            )

            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 10
                )
            )

            expectNoEvents()
        }
    }

    @Test
    fun test_processIntent_ReportPagingLoadStates_prependLoadingThenNotLoadingWithMoreItems_emitsLoadingAndSuccessAnnouncements() = runTest {
        viewModel.viewEffect.test {
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 10
                )
            )
            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = true,
                    isAppendLoading = false,
                    itemCount = 10
                )
            )
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(PREPEND_LOADING_ANNOUNCEMENT),
                awaitItem()
            )

            viewModel.processIntent(
                HabitListViewIntent.ReportPagingLoadStates(
                    isRefreshLoading = false,
                    isPrependLoading = false,
                    isAppendLoading = false,
                    itemCount = 15
                )
            )

            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(PREPEND_SUCCESS_ANNOUNCEMENT),
                awaitItem()
            )

            expectNoEvents()
        }
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
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(ADDING_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )

            createHabitResultDeferred.complete(CreateHabitResult.Success)
            advanceUntilIdle()

            val completedState =
                viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertFalse(completedState.showBottomSheet)
            Assert.assertEquals("", completedState.textFieldViewState.value)
            Assert.assertFalse(completedState.textFieldViewState.isError)
            Assert.assertEquals("", completedState.textFieldViewState.errorMessage)
            Assert.assertFalse(completedState.isSubmitting)
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(ADD_SUCCESS_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
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
                HabitListViewEffect.AnnounceForAccessibility(ADDING_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
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
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(UPDATING_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )

            updateHabitResultDeferred.complete(UpdateHabitResult.Success)
            advanceUntilIdle()

            val completedState = viewStateTurbine.expectMostRecentItem().habitEditorBottomSheetViewState
            Assert.assertFalse(completedState.showBottomSheet)
            Assert.assertEquals("", completedState.textFieldViewState.value)
            Assert.assertFalse(completedState.textFieldViewState.isError)
            Assert.assertEquals("", completedState.textFieldViewState.errorMessage)
            Assert.assertFalse(completedState.isSubmitting)
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(UPDATE_SUCCESS_ANNOUNCEMENT),
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
                HabitListViewEffect.AnnounceForAccessibility(UPDATING_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
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
        viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID, HABIT_NAME))

        val deleteDialogViewState = viewModel.viewState.value.deleteHabitDialogViewState

        Assert.assertTrue(deleteDialogViewState.showDialog)
        Assert.assertEquals(HABIT_ID, deleteDialogViewState.habitId)
        Assert.assertEquals(HABIT_NAME, deleteDialogViewState.habitName)
        Assert.assertFalse(deleteDialogViewState.isSubmitting)
    }

    @Test
    fun test_processIntent_DismissDeleteHabitDialog() {
        viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID, HABIT_NAME))

        viewModel.processIntent(HabitListViewIntent.DismissDeleteHabitDialog)

        val deleteDialogViewState = viewModel.viewState.value.deleteHabitDialogViewState
        Assert.assertFalse(deleteDialogViewState.showDialog)
        Assert.assertEquals(null, deleteDialogViewState.habitId)
        Assert.assertFalse(deleteDialogViewState.isSubmitting)
        coVerify(exactly = 0) { deleteHabitUseCase(any()) }
    }

    @Test
    fun test_processIntent_DismissDeleteHabitDialog_doesNothingWhileSubmitting() = runTest {
        val deleteHabitResultDeferred = CompletableDeferred<DeleteHabitResult>()
        coEvery { deleteHabitUseCase(HABIT_ID) } coAnswers { deleteHabitResultDeferred.await() }

        viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID, HABIT_NAME))
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

            viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID, HABIT_NAME))
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
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(DELETE_SUCCESS_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
        }
    }

    @Test
    fun test_processIntent_ConfirmDeleteHabit_error() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { deleteHabitUseCase(HABIT_ID) } returns DeleteHabitResult.Error

            viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(HABIT_ID, HABIT_NAME))
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
    fun test_processIntent_ClickBackButton_setsShowLogoutDialogTrue() {
        viewModel.processIntent(HabitListViewIntent.ClickBackButton)

        Assert.assertTrue(viewModel.viewState.value.showLogoutDialog)
        coVerify(exactly = 0) { logoutUseCase() }
    }

    @Test
    fun test_processIntent_DismissLogoutDialog_setsShowLogoutDialogFalse() {
        viewModel.processIntent(HabitListViewIntent.ClickBackButton)
        Assert.assertTrue(viewModel.viewState.value.showLogoutDialog)

        viewModel.processIntent(HabitListViewIntent.DismissLogoutDialog)

        Assert.assertFalse(viewModel.viewState.value.showLogoutDialog)
        coVerify(exactly = 0) { logoutUseCase() }
    }

    @Test
    fun test_processIntent_ConfirmLogout_hidesDialogAndEmitsNavigateToLoginScreen() = runTest {
        viewModel.viewEffect.test {
            viewModel.processIntent(HabitListViewIntent.ClickBackButton)
            Assert.assertTrue(viewModel.viewState.value.showLogoutDialog)
            coVerify(exactly = 0) { logoutUseCase() }

            viewModel.processIntent(HabitListViewIntent.ConfirmLogout)

            Assert.assertFalse(viewModel.viewState.value.showLogoutDialog)
            Assert.assertEquals(HabitListViewEffect.NavigateToLoginScreen, awaitItem())
            coVerify(exactly = 1) { logoutUseCase() }
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

            viewModel.processIntent(HabitListViewIntent.ClickHabit(HABIT_ID, HABIT_NAME))

            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(CHECKING_IN_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
            Assert.assertEquals(
                setOf(HABIT_ID),
                viewStateTurbine.expectMostRecentItem().checkingInHabitIds
            )

            checkInResultDeferred.complete(CheckInResult.Success)

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(CHECK_IN_SUCCESS_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
            coVerify { checkInUseCase(HABIT_ID) }
        }
    }

    @Test
    fun test_processIntent_ClickHabit_alreadyCheckedInError() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { checkInUseCase(HABIT_ID) } returns CheckInResult.AlreadyCheckedInError

            viewModel.processIntent(HabitListViewIntent.ClickHabit(HABIT_ID, HABIT_NAME))

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(CHECKING_IN_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
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

            viewModel.processIntent(HabitListViewIntent.ClickHabit(HABIT_ID, HABIT_NAME))

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(CHECKING_IN_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
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

            viewModel.processIntent(HabitListViewIntent.ClickCheckInHabitButton(HABIT_ID, HABIT_NAME))

            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(CHECKING_IN_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
            Assert.assertEquals(
                setOf(HABIT_ID),
                viewStateTurbine.expectMostRecentItem().checkingInHabitIds
            )

            checkInResultDeferred.complete(CheckInResult.Success)

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(CHECK_IN_SUCCESS_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
            coVerify { checkInUseCase(HABIT_ID) }
        }
    }

    @Test
    fun test_processIntent_ClickCheckInHabitButton_alreadyCheckedInError() = runTest {
        turbineScope {
            val viewStateTurbine = viewModel.viewState.testIn(backgroundScope)
            val viewEffectTurbine = viewModel.viewEffect.testIn(backgroundScope)
            coEvery { checkInUseCase(HABIT_ID) } returns CheckInResult.AlreadyCheckedInError

            viewModel.processIntent(HabitListViewIntent.ClickCheckInHabitButton(HABIT_ID, HABIT_NAME))

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(CHECKING_IN_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
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

            viewModel.processIntent(HabitListViewIntent.ClickCheckInHabitButton(HABIT_ID, HABIT_NAME))

            Assert.assertTrue(viewStateTurbine.expectMostRecentItem().checkingInHabitIds.isEmpty())
            Assert.assertEquals(
                HabitListViewEffect.AnnounceForAccessibility(CHECKING_IN_ANNOUNCEMENT),
                viewEffectTurbine.awaitItem()
            )
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
        private const val CHECKING_IN_ANNOUNCEMENT = "Read for 20 minutes. Checking in."
        private const val CHECK_IN_SUCCESS_ANNOUNCEMENT = "Read for 20 minutes checked in."
        private const val DELETE_SUCCESS_ANNOUNCEMENT = "Read for 20 minutes deleted."
        private const val ADDING_ANNOUNCEMENT = "Adding Read for 20 minutes."
        private const val ADD_SUCCESS_ANNOUNCEMENT = "Read for 20 minutes added."
        private const val UPDATING_ANNOUNCEMENT = "Saving Read for 30 minutes."
        private const val UPDATE_SUCCESS_ANNOUNCEMENT = "Read for 30 minutes updated."
        private const val INITIAL_LOADING_ANNOUNCEMENT = "Loading habits."
        private const val PREPEND_LOADING_ANNOUNCEMENT = "Loading earlier habits."
        private const val PREPEND_SUCCESS_ANNOUNCEMENT = "Earlier habits loaded."
        private const val APPEND_LOADING_ANNOUNCEMENT = "Loading more habits."
        private const val APPEND_SUCCESS_ANNOUNCEMENT = "More habits loaded."
        private const val INVALID_HABIT_NAME_MESSAGE = "Please enter a habit name"
        private const val CREATE_HABIT_SUCCESS_MESSAGE = "Habit added successfully."
        private const val CREATE_HABIT_ERROR_MESSAGE = "Failed to add habit. Please try again."
        private const val UPDATE_HABIT_ERROR_MESSAGE = "Failed to update habit. Please try again."
        private const val DELETE_HABIT_ERROR_MESSAGE = "Failed to delete habit. Please try again."
    }
}
