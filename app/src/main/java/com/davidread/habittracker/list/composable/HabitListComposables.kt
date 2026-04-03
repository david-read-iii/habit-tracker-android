package com.davidread.habittracker.list.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewIntent
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
    habits: LazyPagingItems<HabitEntity>,
    onHabitClick: (String) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (habits.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    count = habits.itemCount,
                    key = habits.itemKey { it.id }
                ) { index ->
                    habits[index]?.let { habit ->
                        HabitItem(
                            habit = habit,
                            onClick = { onHabitClick(habit.id) }
                        )
                        HorizontalDivider()
                    }
                }

                if (habits.loadState.append is LoadState.Loading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HabitItem(
    habit: HabitEntity,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = habit.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Created at: ${habit.createdAt}", style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = habit.streak.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListContentPreview() {
    val habits = flowOf(
        PagingData.from(
            listOf(
                HabitEntity("1", "Drink Water", 5, "2023-10-27"),
                HabitEntity("2", "Exercise", 3, "2023-10-26"),
                HabitEntity("3", "Read Book", 10, "2023-10-25")
            ),
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

@Preview(showBackground = true)
@Composable
private fun HabitItemPreview() {
    HabitTrackerTheme {
        HabitItem(
            habit = HabitEntity(
                id = "1",
                name = "Drink Water",
                streak = 5,
                createdAt = "2023-10-27"
            )
        )
    }
}
