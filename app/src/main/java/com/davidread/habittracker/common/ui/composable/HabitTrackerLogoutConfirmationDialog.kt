package com.davidread.habittracker.common.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme

@Composable
fun HabitTrackerLogoutConfirmationDialog(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    HabitTrackerAlertDialog(
        title = stringResource(R.string.habit_list_logout_dialog_title),
        message = stringResource(R.string.habit_list_logout_dialog_message),
        primaryButtonText = stringResource(R.string.yes),
        onPrimaryButtonClick = onConfirm,
        negativeButtonText = stringResource(R.string.no),
        onNegativeButtonClick = onDismiss,
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        onDismissRequest = onDismiss,
        mode = HabitTrackerAlertDialogMode.Default
    )
}

@Preview(showBackground = true)
@Composable
private fun HabitTrackerLogoutConfirmationDialogPreview() {
    HabitTrackerTheme {
        HabitTrackerLogoutConfirmationDialog()
    }
}
