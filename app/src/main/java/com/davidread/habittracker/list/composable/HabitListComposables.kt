package com.davidread.habittracker.list.composable

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.composable.HabitTrackerAlertDialog
import com.davidread.habittracker.common.ui.composable.HabitTrackerAlertDialogMode
import com.davidread.habittracker.common.ui.composable.HabitTrackerLogoutConfirmationDialog
import com.davidread.habittracker.common.ui.composable.HabitTrackerTextField
import com.davidread.habittracker.common.ui.composable.HabitTrackerTopAppBar
import com.davidread.habittracker.common.ui.theme.Color
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.list.model.DeleteHabitDialogViewState
import com.davidread.habittracker.list.model.EditorState
import com.davidread.habittracker.list.model.HabitEditorBottomSheetViewState
import com.davidread.habittracker.list.model.HabitListTextFieldViewState
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.model.HabitListViewState
import com.davidread.habittracker.list.model.HabitViewState
import com.davidread.habittracker.list.viewmodel.HabitListViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color as ComposeColor

internal const val CLEAR_HABIT_NAME_BUTTON_TEST_TAG = "clear_habit_name_button"

@Composable
fun HabitListScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitListViewModel = hiltViewModel(),
    onNavigateToSettingsScreen: () -> Unit = {},
    onNavigateToLoginScreen: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        viewModel.processIntent(HabitListViewIntent.ClickBackButton)
    }

    LaunchedEffect(Unit) {
        viewModel.viewEffect.collect { viewEffect ->
            when (viewEffect) {
                is HabitListViewEffect.NavigateToSettingsScreen -> onNavigateToSettingsScreen()
                is HabitListViewEffect.NavigateToLoginScreen -> onNavigateToLoginScreen()
                is HabitListViewEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = viewEffect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    val habits = viewModel.habitsPagingDataFlow.collectAsLazyPagingItems()
    val viewState by viewModel.viewState.collectAsState()

    HabitListContent(
        modifier = modifier,
        habits = habits,
        viewState = viewState,
        snackbarHostState = snackbarHostState,
        onClickAddHabit = { viewModel.processIntent(HabitListViewIntent.ClickAddHabitButton) },
        onDismissHabitEditorBottomSheet = {
            viewModel.processIntent(HabitListViewIntent.DismissHabitEditorBottomSheet)
        },
        onHabitEditorNameChange = {
            viewModel.processIntent(
                HabitListViewIntent.ChangeHabitEditorNameValue(
                    it
                )
            )
        },
        onHabitEditorSubmit = { viewModel.processIntent(HabitListViewIntent.SubmitHabitEditorChanges) },
        onClickSettings = { viewModel.processIntent(HabitListViewIntent.ClickSettingsButton) },
        onHabitClick = { viewModel.processIntent(HabitListViewIntent.ClickHabit(it)) },
        onHabitCheckInClick = {
            viewModel.processIntent(
                HabitListViewIntent.ClickCheckInHabitButton(
                    it
                )
            )
        },
        onHabitRenameClick = { id, currentName ->
            viewModel.processIntent(HabitListViewIntent.ClickEditHabitButton(id, currentName))
        },
        onHabitDeleteClick = { viewModel.processIntent(HabitListViewIntent.ClickDeleteHabitButton(it)) },
        onDismissDeleteHabitDialog = {
            viewModel.processIntent(HabitListViewIntent.DismissDeleteHabitDialog)
        },
        onConfirmDeleteHabit = {
            viewModel.processIntent(HabitListViewIntent.ConfirmDeleteHabit)
        },
        onRefresh = { viewModel.processIntent(HabitListViewIntent.PullToRefresh) },
        onRefreshComplete = { viewModel.processIntent(HabitListViewIntent.RefreshComplete) },
        onDismissLogoutDialog = { viewModel.processIntent(HabitListViewIntent.DismissLogoutDialog) },
        onConfirmLogout = { viewModel.processIntent(HabitListViewIntent.ConfirmLogout) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListContent(
    modifier: Modifier = Modifier,
    habits: LazyPagingItems<HabitViewState>,
    viewState: HabitListViewState = HabitListViewState(),
    snackbarHostState: SnackbarHostState,
    onClickAddHabit: () -> Unit = {},
    onDismissHabitEditorBottomSheet: () -> Unit = {},
    onHabitEditorNameChange: (String) -> Unit = {},
    onHabitEditorSubmit: () -> Unit = {},
    onClickSettings: () -> Unit = {},
    onHabitClick: (String) -> Unit = {},
    onHabitCheckInClick: (String) -> Unit = {},
    onHabitRenameClick: (String, String) -> Unit = { _, _ -> },
    onHabitDeleteClick: (String) -> Unit = {},
    onDismissDeleteHabitDialog: () -> Unit = {},
    onConfirmDeleteHabit: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onRefreshComplete: () -> Unit = {},
    onDismissLogoutDialog: () -> Unit = {},
    onConfirmLogout: () -> Unit = {}
) {
    var openSwipeHabitId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(habits.loadState.refresh) {
        if (habits.loadState.refresh is LoadState.NotLoading && viewState.isRefreshing) {
            onRefreshComplete()
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            HabitTrackerTopAppBar(
                title = stringResource(R.string.habit_list_title),
                actions = {
                    IconButton(onClick = onClickAddHabit) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.habit_list_add_habit)
                        )
                    }
                    IconButton(onClick = onClickSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.habit_list_open_settings)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val pullToRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            state = pullToRefreshState,
            isRefreshing = viewState.isRefreshing,
            onRefresh = {
                habits.refresh()
                onRefresh()
            }
        ) {
            when (habits.loadState.refresh) {
                is LoadState.Loading -> {
                    LoadingListItem()
                }

                is LoadState.Error -> {
                    ErrorListItem(onClick = { habits.retry() })
                }

                is LoadState.NotLoading -> {
                    if (habits.itemCount == 0) {
                        HabitListEmptyStateItem()
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            when (habits.loadState.prepend) {
                                is LoadState.Loading -> item {
                                    LoadingListItem()
                                    HorizontalDivider()
                                }

                                is LoadState.Error -> item {
                                    ErrorListItem(onClick = { habits.retry() })
                                    HorizontalDivider()
                                }

                                is LoadState.NotLoading -> Unit
                            }

                            items(
                                count = habits.itemCount,
                                key = habits.itemKey { it.id }
                            ) { index ->
                                habits[index]?.let { habit ->
                                    HabitListItem(
                                        viewState = habit,
                                        isCheckingIn = habit.id in viewState.checkingInHabitIds,
                                        shouldCloseSwipeActions =
                                            openSwipeHabitId != null && openSwipeHabitId != habit.id,
                                        onRequestOpenSwipeActions = {
                                            if (openSwipeHabitId != habit.id) {
                                                openSwipeHabitId = habit.id
                                            }
                                        },
                                        onSwipeActionsClosed = {
                                            if (openSwipeHabitId == habit.id) {
                                                openSwipeHabitId = null
                                            }
                                        },
                                        onClick = { onHabitClick(habit.id) },
                                        onCheckInClick = { onHabitCheckInClick(habit.id) },
                                        onRenameClick = {
                                            onHabitRenameClick(
                                                habit.id,
                                                habit.name
                                            )
                                        },
                                        onDeleteClick = { onHabitDeleteClick(habit.id) }
                                    )
                                    HorizontalDivider()
                                }
                            }

                            when (val appendState = habits.loadState.append) {
                                is LoadState.Loading -> item { LoadingListItem() }
                                is LoadState.Error -> item { ErrorListItem(onClick = { habits.retry() }) }
                                is LoadState.NotLoading -> {
                                    if (appendState.endOfPaginationReached && habits.itemCount > 0) {
                                        item { EndOfPaginationListItem() }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewState.habitEditorBottomSheetViewState.showBottomSheet) {
        HabitEditorBottomSheet(
            viewState = viewState.habitEditorBottomSheetViewState,
            onNameChange = onHabitEditorNameChange,
            onDismiss = onDismissHabitEditorBottomSheet,
            onSubmit = onHabitEditorSubmit
        )
    }

    if (viewState.deleteHabitDialogViewState.showDialog) {
        DeleteHabitConfirmationDialog(
            viewState = viewState.deleteHabitDialogViewState,
            onDismiss = onDismissDeleteHabitDialog,
            onConfirm = onConfirmDeleteHabit
        )
    }

    if (viewState.showLogoutDialog) {
        HabitTrackerLogoutConfirmationDialog(
            onDismiss = onDismissLogoutDialog,
            onConfirm = onConfirmLogout
        )
    }
}

@Composable
fun HabitListItem(
    modifier: Modifier = Modifier,
    viewState: HabitViewState,
    isCheckingIn: Boolean = false,
    shouldCloseSwipeActions: Boolean = false,
    onRequestOpenSwipeActions: () -> Unit = {},
    onSwipeActionsClosed: () -> Unit = {},
    onClick: () -> Unit = {},
    onCheckInClick: () -> Unit = {},
    onRenameClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val fireIconScale = remember(viewState.id) { Animatable(1f) }
    var previousStreak by remember(viewState.id) { mutableStateOf(viewState.streak) }
    val offsetX = remember(viewState.id) { Animatable(0f) }
    val density = LocalDensity.current
    var itemHeightPx by remember(viewState.id) { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val actionButtonWidth = 60.dp
    val totalActionsWidth = actionButtonWidth * 3
    val totalActionsWidthPx = with(density) { totalActionsWidth.toPx() }

    val closeSwipeAndRun: (() -> Unit) -> Unit = { action ->
        scope.launch {
            offsetX.animateTo(0f, animationSpec = tween(durationMillis = 300))
            onSwipeActionsClosed()
            action()
        }
    }

    LaunchedEffect(shouldCloseSwipeActions) {
        if (shouldCloseSwipeActions && offsetX.value != 0f) {
            offsetX.animateTo(0f, animationSpec = tween(durationMillis = 300))
        }
    }

    LaunchedEffect(viewState.id, viewState.streak) {
        if (previousStreak != viewState.streak) {
            fireIconScale.snapTo(1f)
            fireIconScale.animateTo(
                targetValue = 1.22f,
                animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
            )
            fireIconScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )
        }
        previousStreak = viewState.streak
    }

    Box(modifier = modifier.fillMaxWidth()) {
        HabitListItemSwipeActions(
            heightPx = itemHeightPx,
            density = density,
            onDeleteClick = {
                closeSwipeAndRun(onDeleteClick)
            },
            onRenameClick = {
                closeSwipeAndRun(onRenameClick)
            },
            onCheckInClick = {
                closeSwipeAndRun(onCheckInClick)
            }
        )

        HabitListItemForeground(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            if (!isCheckingIn) {
                                if (dragAmount < 0f) {
                                    onRequestOpenSwipeActions()
                                }
                                scope.launch {
                                    val newValue = (offsetX.value + dragAmount).coerceIn(
                                        -totalActionsWidthPx,
                                        0f
                                    )
                                    offsetX.snapTo(newValue)
                                }
                            }
                        },
                        onDragEnd = {
                            if (!isCheckingIn) {
                                scope.launch {
                                    val threshold = totalActionsWidthPx * 0.3f
                                    val targetValue =
                                        if (offsetX.value < -threshold) -totalActionsWidthPx else 0f
                                    offsetX.animateTo(
                                        targetValue,
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                    if (targetValue == 0f) {
                                        onSwipeActionsClosed()
                                    } else {
                                        onRequestOpenSwipeActions()
                                    }
                                }
                            }
                        }
                    )
                }
                .clickable(
                    onClick = onClick,
                    enabled = !isCheckingIn && offsetX.value == 0f
                )
                .onSizeChanged { itemHeightPx = it.height }
                .padding(16.dp),
            viewState = viewState,
            isCheckingIn = isCheckingIn,
            fireIconScale = fireIconScale.value
        )
    }
}

@Composable
private fun HabitListItemSwipeActions(
    heightPx: Int,
    density: androidx.compose.ui.unit.Density,
    onDeleteClick: () -> Unit,
    onRenameClick: () -> Unit,
    onCheckInClick: () -> Unit
) {
    val rowModifier = if (heightPx > 0) {
        Modifier
            .fillMaxWidth()
            .height(with(density) { heightPx.toDp() })
    } else {
        Modifier.fillMaxWidth()
    }

    Row(
        modifier = rowModifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.End
    ) {
        SwipeActionButton(
            icon = Icons.Filled.Delete,
            label = stringResource(R.string.habit_list_delete_action),
            containerColor = Color.SwipeDeleteContainer,
            contentColor = Color.SwipeDeleteContent,
            onClick = onDeleteClick
        )
        SwipeActionButton(
            icon = Icons.Filled.Edit,
            label = stringResource(R.string.habit_list_rename_action),
            containerColor = Color.SwipeRenameContainer,
            contentColor = Color.SwipeRenameContent,
            onClick = onRenameClick
        )
        SwipeActionButton(
            icon = Icons.Filled.Whatshot,
            label = stringResource(R.string.habit_list_check_in_action),
            containerColor = Color.SwipeCheckInContainer,
            contentColor = Color.SwipeCheckInContent,
            onClick = onCheckInClick
        )
    }
}

@Composable
private fun HabitListItemForeground(
    modifier: Modifier,
    viewState: HabitViewState,
    isCheckingIn: Boolean,
    fireIconScale: Float
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = viewState.name,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        HabitListItemMetaRow(
            streak = viewState.streak,
            createdAt = viewState.createdAt,
            isCheckingIn = isCheckingIn,
            fireIconScale = fireIconScale
        )
    }
}

@Composable
private fun HabitListItemMetaRow(
    streak: String,
    createdAt: String,
    isCheckingIn: Boolean,
    fireIconScale: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCheckingIn) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.FireRed
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Whatshot,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer {
                            scaleX = fireIconScale
                            scaleY = fireIconScale
                        },
                    tint = Color.FireRed
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = streak,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = createdAt,
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun SwipeActionButton(
    icon: ImageVector,
    label: String,
    containerColor: ComposeColor,
    contentColor: ComposeColor,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .fillMaxHeight()
            .background(color = containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = contentColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LoadingListItem(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val shimmerTranslate by transition.animateFloat(
        initialValue = -600f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            )
        )
    )
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(shimmerTranslate, 0f),
        end = Offset(shimmerTranslate + 600f, 0f)
    )
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(24.dp)
                .background(brush = brush, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(16.dp)
                        .background(brush = brush, shape = RoundedCornerShape(2.dp))
                )
            }
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .background(brush = brush, shape = RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun ErrorListItem(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.RedError
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.habit_list_error_message),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun EndOfPaginationListItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.habit_list_end_of_pagination_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun HabitListEmptyStateItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.undraw_empty_4zx0),
            contentDescription = null,
            modifier = Modifier.size(192.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.habit_list_empty_message),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitEditorBottomSheet(
    viewState: HabitEditorBottomSheetViewState,
    onNameChange: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    val latestIsSubmittingHabit by rememberUpdatedState(viewState.isSubmitting)
    val confirmValueChange = remember {
        { newValue: SheetValue ->
            !(latestIsSubmittingHabit && newValue == SheetValue.Hidden)
        }
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = confirmValueChange
    )
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (!viewState.isSubmitting) {
                onDismiss()
            }
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = viewState.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            HabitTrackerTextField(
                value = viewState.textFieldViewState.value,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                enabled = !viewState.isSubmitting,
                isError = viewState.textFieldViewState.isError,
                labelText = stringResource(R.string.habit_list_add_habit_name_label),
                errorMessage = viewState.textFieldViewState.errorMessage,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Words
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!viewState.isSubmitting) {
                            onSubmit()
                        }
                    }
                ),
                trailingIcon = {
                    if (viewState.textFieldViewState.value.isNotBlank() && !viewState.isSubmitting) {
                        IconButton(
                            modifier = Modifier.testTag(CLEAR_HABIT_NAME_BUTTON_TEST_TAG),
                            onClick = { onNameChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.clear_habit_name)
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !viewState.isSubmitting
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onSubmit,
                    enabled = !viewState.isSubmitting
                ) {
                    if (viewState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = viewState.positiveButtonText
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteHabitConfirmationDialog(
    viewState: DeleteHabitDialogViewState,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    HabitTrackerAlertDialog(
        title = stringResource(R.string.habit_list_delete_dialog_title),
        message = stringResource(R.string.habit_list_delete_dialog_message),
        primaryButtonText = stringResource(R.string.yes),
        onPrimaryButtonClick = onConfirm,
        negativeButtonText = stringResource(R.string.no),
        onNegativeButtonClick = onDismiss,
        dismissOnBackPress = !viewState.isSubmitting,
        dismissOnClickOutside = !viewState.isSubmitting,
        onDismissRequest = onDismiss,
        mode = if (viewState.isSubmitting) {
            HabitTrackerAlertDialogMode.Loading
        } else {
            HabitTrackerAlertDialogMode.Default
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HabitListItemPreview() {
    HabitTrackerTheme {
        HabitListItem(
            viewState = HabitViewState(
                id = "1",
                name = "Drink Water",
                streak = "5",
                createdAt = "Just now"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingListItemPreview() {
    HabitTrackerTheme {
        LoadingListItem()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorListItemPreview() {
    HabitTrackerTheme {
        ErrorListItem()
    }
}

@Preview(showBackground = true)
@Composable
private fun EndOfPaginationListItemPreview() {
    HabitTrackerTheme {
        EndOfPaginationListItem()
    }
}

private val habitListPreviewData = listOf(
    HabitViewState("1", "Drink Water", "5", "Just now"),
    HabitViewState("2", "Exercise", "3", "32 minutes ago"),
    HabitViewState("3", "Read Book", "10", "14 hours ago"),
    HabitViewState("4", "Meditate", "2", "1 day ago"),
    HabitViewState("5", "Walk Dog", "15", "2 days ago"),
    HabitViewState("6", "Eat Healthy", "8", "3 days ago"),
    HabitViewState("7", "Journal", "12", "4 days ago"),
    HabitViewState("8", "Learn Coding", "20", "5 days ago"),
    HabitViewState("9", "Sleep 8h", "7", "6 days ago"),
    HabitViewState("10", "No Sugar", "4", "1 week ago")
)

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_NotLoading() {
    val habits = flowOf(
        PagingData.from(
            habitListPreviewData,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() },
            viewState = HabitListViewState(checkingInHabitIds = emptySet())
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_RefreshLoading() {
    val habits = flowOf(
        PagingData.from(
            listOf<HabitViewState>(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_RefreshError() {
    val habits = flowOf(
        PagingData.from(
            listOf<HabitViewState>(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.Error(Exception()),
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_PrependLoading() {
    val habits = flowOf(
        PagingData.from(
            habitListPreviewData,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                prepend = LoadState.Loading,
                append = LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_PrependError() {
    val habits = flowOf(
        PagingData.from(
            habitListPreviewData,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                prepend = LoadState.Error(Exception()),
                append = LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_AppendLoading() {
    val habits = flowOf(
        PagingData.from(
            habitListPreviewData,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                append = LoadState.Loading
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_AppendError() {
    val habits = flowOf(
        PagingData.from(
            habitListPreviewData,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                append = LoadState.Error(Exception())
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_EndOfPagination() {
    val habits = flowOf(
        PagingData.from(
            habitListPreviewData,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(true)
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview_EmptyList() {
    val habits = flowOf(
        PagingData.from(
            emptyList<HabitViewState>(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(true),
                prepend = LoadState.NotLoading(true),
                append = LoadState.NotLoading(true)
            )
        )
    ).collectAsLazyPagingItems()

    HabitTrackerTheme {
        HabitListContent(
            habits = habits,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitEditorBottomSheetPreview_Default() {
    HabitTrackerTheme {
        HabitEditorBottomSheet(
            viewState = HabitEditorBottomSheetViewState(
                showBottomSheet = true,
                title = "Add habit",
                textFieldViewState = HabitListTextFieldViewState(value = "Drink Water"),
                positiveButtonText = "Add",
                isSubmitting = false,
                editorState = EditorState.Add
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitEditorBottomSheetPreview_Loading() {
    HabitTrackerTheme {
        HabitEditorBottomSheet(
            viewState = HabitEditorBottomSheetViewState(
                showBottomSheet = true,
                title = "Add habit",
                textFieldViewState = HabitListTextFieldViewState(value = "Drink Water"),
                positiveButtonText = "Add",
                isSubmitting = true,
                editorState = EditorState.Add
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitEditorBottomSheetPreview_Error() {
    HabitTrackerTheme {
        HabitEditorBottomSheet(
            viewState = HabitEditorBottomSheetViewState(
                showBottomSheet = true,
                title = "Add habit",
                textFieldViewState = HabitListTextFieldViewState(
                    value = "",
                    isError = true,
                    errorMessage = "Habit name is required"
                ),
                positiveButtonText = "Add",
                isSubmitting = false,
                editorState = EditorState.Add
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteHabitConfirmationDialogPreview_Default() {
    HabitTrackerTheme {
        DeleteHabitConfirmationDialog(
            viewState = DeleteHabitDialogViewState(
                showDialog = true,
                habitId = "1",
                isSubmitting = false
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteHabitConfirmationDialogPreview_Loading() {
    HabitTrackerTheme {
        DeleteHabitConfirmationDialog(
            viewState = DeleteHabitDialogViewState(
                showDialog = true,
                habitId = "1",
                isSubmitting = true
            )
        )
    }
}
