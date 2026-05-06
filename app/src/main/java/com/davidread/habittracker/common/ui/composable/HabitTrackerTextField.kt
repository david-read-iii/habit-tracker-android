package com.davidread.habittracker.common.ui.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme

@Composable
fun HabitTrackerTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    labelText: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(labelText) },
        isError = isError,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true
    )
    if (isError && errorMessage.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.semantics {
                error(errorMessage)
            }
        )
    }
}

@Composable
fun HabitTrackerTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue = TextFieldValue(""),
    onValueChange: (TextFieldValue) -> Unit = {},
    labelText: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(labelText) },
        isError = isError,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true
    )
    if (isError && errorMessage.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.semantics {
                error(errorMessage)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitTrackerTextFieldPreview_Default() {
    HabitTrackerTheme {
        HabitTrackerTextField(
            value = "Value",
            labelText = "Label",
            keyboardOptions = KeyboardOptions.Default
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitTrackerTextFieldPreview_Error() {
    HabitTrackerTheme {
        HabitTrackerTextField(
            value = "Invalid value",
            labelText = "Label",
            isError = true,
            errorMessage = "Error message",
            keyboardOptions = KeyboardOptions.Default
        )
    }
}
