package com.davidread.habittracker.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.davidread.habittracker.R
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerAlertDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    primaryButtonText: String? = null,
    onPrimaryButtonClick: () -> Unit = {},
    negativeButtonText: String? = null,
    onNegativeButtonClick: () -> Unit = {},
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false,
    mode: HabitTrackerAlertDialogMode = HabitTrackerAlertDialogMode.Default
) {
    BasicAlertDialog(
        onDismissRequest = {},
        modifier = modifier,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Box(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(8.dp)
            )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Left
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = message ?: stringResource(R.string.generic_error_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    negativeButtonText?.let {
                        TextButton(
                            onClick = onNegativeButtonClick,
                            enabled = mode == HabitTrackerAlertDialogMode.Default
                        ) {
                            Text(text = it)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    TextButton(
                        onClick = onPrimaryButtonClick,
                        enabled = mode == HabitTrackerAlertDialogMode.Default
                    ) {
                        when (mode) {
                            HabitTrackerAlertDialogMode.Default -> Text(
                                text = primaryButtonText ?: stringResource(R.string.ok)
                            )
                            HabitTrackerAlertDialogMode.Loading -> CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class HabitTrackerAlertDialogMode {
    object Default : HabitTrackerAlertDialogMode()
    object Loading : HabitTrackerAlertDialogMode()
}

@Preview
@Composable
private fun HabitTrackerAlertDialogPreview_DefaultParams() {
    HabitTrackerTheme {
        HabitTrackerAlertDialog()
    }
}

@Preview
@Composable
private fun HabitTrackerAlertDialogPreview_CustomParams() {
    HabitTrackerTheme {
        HabitTrackerAlertDialog(
            title = "Delete habit",
            message = "Are you sure you want to delete this habit?",
            negativeButtonText = "No",
            primaryButtonText = "Yes"
        )
    }
}

@Preview
@Composable
private fun HabitTrackerAlertDialogPreview_LoadingMode() {
    HabitTrackerTheme {
        HabitTrackerAlertDialog(
            title = "Delete habit",
            message = "Are you sure you want to delete this habit?",
            negativeButtonText = "No",
            primaryButtonText = "Yes",
            mode = HabitTrackerAlertDialogMode.Loading
        )
    }
}

