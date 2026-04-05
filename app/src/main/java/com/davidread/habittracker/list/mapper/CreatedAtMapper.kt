package com.davidread.habittracker.list.mapper

import android.app.Application
import com.davidread.habittracker.R
import com.davidread.habittracker.common.util.Logger
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class CreatedAtMapper @Inject constructor(
    private val application: Application,
    private val logger: Logger
) {

    private val dateTimeFormatter = DateTimeFormatter
        .ofPattern(DATE_PATTERN)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    fun map(createdAt: String): String {
        val createdAtInstant = try {
            Instant.parse(createdAt)
        } catch (e: Exception) {
            logger.e(TAG, "Error parsing createdAt: $createdAt", e)
            return createdAt
        }
        val now = Instant.now()
        val duration = Duration.between(createdAtInstant, now)

        return when {
            duration.toMinutes() < 1 -> application.getString(R.string.created_at_just_now)
            duration.toHours() < 1 -> {
                val mins = duration.toMinutes().toInt()
                application.resources.getQuantityString(R.plurals.created_at_mins_ago, mins, mins)
            }

            duration.toDays() < 1 -> {
                val hours = duration.toHours().toInt()
                application.resources.getQuantityString(
                    R.plurals.created_at_hours_ago,
                    hours,
                    hours
                )
            }

            duration.toDays() < 365 -> {
                val days = duration.toDays().toInt()
                application.resources.getQuantityString(R.plurals.created_at_days_ago, days, days)
            }

            else -> dateTimeFormatter.format(createdAtInstant)
        }
    }

    companion object {
        private const val TAG = "CreatedAtMapper"
        private const val DATE_PATTERN = "MMM dd, yyyy"
    }
}
