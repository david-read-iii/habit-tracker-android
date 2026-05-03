package com.davidread.habittracker.common.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AccessibilityAnnouncementHost(
    message: String,
    announcementId: Int,
    modifier: Modifier = Modifier
) {
    if (message.isBlank()) {
        return
    }

    key(announcementId) {
        Box(
            modifier = modifier
                .size(1.dp)
                .semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = message
                }
        )
    }
}

