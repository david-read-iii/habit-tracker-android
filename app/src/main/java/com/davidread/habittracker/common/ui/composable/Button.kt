package com.davidread.habittracker.common.ui.composable

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme

@Composable
fun Button(modifier: Modifier = Modifier, label: String = "", onClick: () -> Unit = {}) {
    Button(modifier = modifier, onClick = onClick) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview
@Composable
private fun ButtonPreview() {
    HabitTrackerTheme {
        Button(label = "Click Me")
    }
}
