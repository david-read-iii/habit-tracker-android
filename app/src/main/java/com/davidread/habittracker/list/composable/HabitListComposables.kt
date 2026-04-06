package com.davidread.habittracker.list.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.theme.Color
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
import com.davidread.habittracker.list.model.HabitViewState
import com.davidread.habittracker.list.viewmodel.HabitListViewModel
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitListViewModel = hiltViewModel(),
    onNavigateToSettingsScreen: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.viewEffect.collect { viewEffect ->
            when (viewEffect) {
                is HabitListViewEffect.NavigateToSettingsScreen -> onNavigateToSettingsScreen()
            }
        }
    }

    val habits = viewModel.habitsPagingDataFlow.collectAsLazyPagingItems()
    val viewState by viewModel.viewState.collectAsState()

    HabitListContent(
        modifier = modifier,
        habits = habits,
        onHabitClick = { viewModel.processIntent(HabitListViewIntent.ClickHabit(it)) }
    )
}

@Composable
fun HabitListContent(
    modifier: Modifier = Modifier,
    habits: LazyPagingItems<HabitViewState>,
    onHabitClick: (String) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        when (habits.loadState.refresh) {
            is LoadState.Loading -> {
                LoadingListItem()
            }

            is LoadState.Error -> {
                ErrorListItem(onClick = { habits.retry() })
            }

            is LoadState.NotLoading -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    when (habits.loadState.prepend) {
                        is LoadState.Loading -> item { LoadingListItem() }
                        is LoadState.Error -> item { ErrorListItem(onClick = { habits.retry() }) }
                        is LoadState.NotLoading -> Unit
                    }

                    items(
                        count = habits.itemCount,
                        key = habits.itemKey { it.id }
                    ) { index ->
                        habits[index]?.let { habit ->
                            HabitListItem(
                                viewState = habit,
                                onClick = { onHabitClick(habit.id) }
                            )
                            HorizontalDivider()
                        }
                    }

                    when (habits.loadState.append) {
                        is LoadState.Loading -> item { LoadingListItem() }
                        is LoadState.Error -> item { ErrorListItem(onClick = { habits.retry() }) }
                        is LoadState.NotLoading -> Unit
                    }
                }
            }
        }
    }
}

@Composable
fun HabitListItem(
    modifier: Modifier = Modifier,
    viewState: HabitViewState,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        Text(
            text = viewState.name,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Whatshot,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.FireRed
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = viewState.streak,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = viewState.createdAt,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun LoadingListItem(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val shimmerTranslate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
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
        end = Offset(shimmerTranslate + 300f, 0f)
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
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
            habits = habits
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
            habits = habits
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
            habits = habits
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
            habits = habits
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
            habits = habits
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
            habits = habits
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
            habits = habits
        )
    }
}
