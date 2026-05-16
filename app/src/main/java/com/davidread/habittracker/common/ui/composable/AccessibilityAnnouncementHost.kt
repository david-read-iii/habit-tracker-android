package com.davidread.habittracker.common.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView

@Composable
fun AccessibilityAnnouncementHost(
    message: String,
    announcementId: Int
) {
    val view = LocalView.current

    LaunchedEffect(announcementId, message, view) {
        if (message.isNotBlank()) {
            view.announceForAccessibility(message)
        }
    }
}
