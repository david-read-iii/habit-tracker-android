package com.davidread.habittracker.common.ui.composable

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.davidread.habittracker.common.ui.theme.HabitTrackerElevation
import com.davidread.habittracker.common.ui.theme.HabitTrackerTheme

@Composable
fun HabitTrackerCard(
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = HabitTrackerElevation.raisedSurfaceShadow
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
