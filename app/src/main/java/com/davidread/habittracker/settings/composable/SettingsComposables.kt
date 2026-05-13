package com.davidread.habittracker.settings.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.composable.HabitTrackerAlertDialog
import com.davidread.habittracker.common.ui.composable.HabitTrackerAlertDialogMode
import com.davidread.habittracker.common.ui.composable.HabitTrackerLogoutConfirmationDialog
import com.davidread.habittracker.common.ui.composable.HabitTrackerTopAppBar
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme
import com.davidread.habittracker.settings.model.ResetTimezoneConfirmationDialogViewState
import com.davidread.habittracker.settings.model.SettingsViewEffect
import com.davidread.habittracker.settings.model.SettingsViewIntent
import com.davidread.habittracker.settings.model.SettingsViewState
import com.davidread.habittracker.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToLoginScreen: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val viewState by viewModel.viewState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.viewEffect.collect { viewEffect ->
            when (viewEffect) {
                is SettingsViewEffect.NavigateToLoginScreen -> onNavigateToLoginScreen()
                is SettingsViewEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(viewEffect.messageResId)
                    )
                }
            }
        }
    }

    SettingsContent(
        modifier = modifier,
        viewState = viewState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onClickResetTimezone = { viewModel.processIntent(SettingsViewIntent.ClickResetTimezoneButton) },
        onDismissResetTimezoneDialog = { viewModel.processIntent(SettingsViewIntent.DismissResetTimezoneDialog) },
        onConfirmResetTimezone = { viewModel.processIntent(SettingsViewIntent.ConfirmResetTimezone) },
        onClickLogOut = { viewModel.processIntent(SettingsViewIntent.ClickLogOutButton) },
        onDismissLogoutDialog = { viewModel.processIntent(SettingsViewIntent.DismissLogoutDialog) },
        onConfirmLogout = { viewModel.processIntent(SettingsViewIntent.ConfirmLogout) }
    )
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    viewState: SettingsViewState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit = {},
    onClickResetTimezone: () -> Unit = {},
    onDismissResetTimezoneDialog: () -> Unit = {},
    onConfirmResetTimezone: () -> Unit = {},
    onClickLogOut: () -> Unit = {},
    onDismissLogoutDialog: () -> Unit = {},
    onConfirmLogout: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HabitTrackerTopAppBar(
                title = stringResource(R.string.settings_title),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            SettingsListItem(
                text = stringResource(R.string.settings_reset_timezone),
                onClick = onClickResetTimezone
            )
            HorizontalDivider()
            SettingsListItem(
                text = stringResource(R.string.settings_log_out),
                onClick = onClickLogOut
            )
        }
    }

    if (viewState.resetTimezoneConfirmationDialogViewState.showDialog) {
        ResetTimezoneConfirmationDialog(
            isSubmitting = viewState.resetTimezoneConfirmationDialogViewState.isSubmitting,
            onDismiss = onDismissResetTimezoneDialog,
            onConfirm = onConfirmResetTimezone
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
private fun SettingsListItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}

@Composable
fun ResetTimezoneConfirmationDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    HabitTrackerAlertDialog(
        title = stringResource(R.string.settings_reset_timezone_dialog_title),
        message = stringResource(R.string.settings_reset_timezone_dialog_message),
        primaryButtonText = stringResource(R.string.yes),
        onPrimaryButtonClick = onConfirm,
        negativeButtonText = stringResource(R.string.no),
        onNegativeButtonClick = onDismiss,
        dismissOnBackPress = !isSubmitting,
        dismissOnClickOutside = !isSubmitting,
        onDismissRequest = onDismiss,
        mode = when (isSubmitting) {
            true -> HabitTrackerAlertDialogMode.Loading(
                accessibilityAnnouncementOnLoading = stringResource(
                    R.string.settings_reset_timezone_loading_announcement
                )
            )
            false -> HabitTrackerAlertDialogMode.Default
        }
    )
}

@Preview(showSystemUi = true)
@Composable
private fun SettingsContentPreview() {
    HabitTrackerTheme {
        SettingsContent(
            viewState = SettingsViewState(),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SettingsContentPreview_ResetTimezoneDialog() {
    HabitTrackerTheme {
        SettingsContent(
            viewState = SettingsViewState(
                resetTimezoneConfirmationDialogViewState = ResetTimezoneConfirmationDialogViewState(
                    showDialog = true
                )
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SettingsContentPreview_LogoutDialog() {
    HabitTrackerTheme {
        SettingsContent(
            viewState = SettingsViewState(
                showLogoutDialog = true
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
