package com.davidread.habittracker.list.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.list.model.HabitListViewEffect
import com.davidread.habittracker.list.model.HabitListViewState
import com.davidread.habittracker.list.viewmodel.HabitListViewModel

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

    val viewState by viewModel.viewState.collectAsState()
    HabitListScreenContent(
        modifier = modifier,
        viewState = viewState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreenContent(
    modifier: Modifier = Modifier,
    viewState: HabitListViewState = HabitListViewState()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Habit List Content")
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HabitListScreenContentPreview() {
    HabitTrackerTheme {
        HabitListScreenContent()
    }
}
