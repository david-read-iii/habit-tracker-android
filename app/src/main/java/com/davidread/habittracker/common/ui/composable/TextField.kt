package com.davidread.habittracker.common.ui.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    labelText: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Unspecified
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(labelText) },
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
    if (isError && errorMessage.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldPreview_Default() {
    HabitTrackerTheme {
        TextField(value = "Value", labelText = "Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldPreview_Error() {
    HabitTrackerTheme {
        TextField(
            value = "Invalid value",
            labelText = "Label",
            isError = true,
            errorMessage = "Error message"
        )
    }
}
