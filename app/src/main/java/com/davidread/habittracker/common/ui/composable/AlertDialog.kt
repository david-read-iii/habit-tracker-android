package com.davidread.habittracker.common.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun AlertDialog(
    modifier: Modifier = Modifier,
    message: String? = null,
    buttonText: String? = null,
    onButtonClick: () -> Unit = {}
) {
    BasicAlertDialog(
        onDismissRequest = {},
        modifier = modifier,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
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
                Text(
                    text = message ?: stringResource(R.string.generic_error_message),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = buttonText ?: stringResource(R.string.ok),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AlertDialogPreview_Default() {
    HabitTrackerTheme {
        AlertDialog()
    }
}

@Preview
@Composable
private fun AlertDialogPreview_Custom() {
    HabitTrackerTheme {
        AlertDialog(
            message = "Service failed.",
            buttonText = "Retry"
        )
    }
}
