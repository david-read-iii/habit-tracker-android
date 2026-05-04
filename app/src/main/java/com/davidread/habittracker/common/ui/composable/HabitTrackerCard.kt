package com.davidread.habittracker.common.ui.composable

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme

@Composable
fun HabitTrackerCard(
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier,
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        content()
    }
}

@Preview
@Composable
private fun HabitTrackerCardPreview() {
    HabitTrackerTheme {
        HabitTrackerCard {
            Text(modifier = Modifier.padding(16.dp), text = "Put content here")
        }
    }
}
