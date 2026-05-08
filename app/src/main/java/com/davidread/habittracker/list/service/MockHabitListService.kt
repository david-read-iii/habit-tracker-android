package com.davidread.habittracker.list.service

import com.davidread.habittracker.list.model.CheckIn
import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResponse
import com.davidread.habittracker.list.model.CreateHabitRequest
import com.davidread.habittracker.list.model.CreateHabitResponse
import com.davidread.habittracker.list.model.DeleteHabitResponse
import com.davidread.habittracker.list.model.HabitDto
import com.davidread.habittracker.list.model.HabitListResponse
import com.davidread.habittracker.list.model.UpdateHabitRequest
import com.davidread.habittracker.list.model.UpdateHabitResponse
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * DEBUG ONLY: in-memory habits API used when the device cannot reach the backend.
 */
class MockHabitListService : HabitListService {

    private val lock = Any()

    private val habits = mutableListOf(
        HabitDto(
            id = "debug-habit-1",
            name = "Drink water",
            streak = 3,
            createdAt = Instant.now().minusSeconds(172800).toString()
        ),
        HabitDto(
            id = "debug-habit-2",
            name = "Read 10 pages",
            streak = 5,
            createdAt = Instant.now().minusSeconds(259200).toString()
        ),
        HabitDto(
            id = "debug-habit-3",
            name = "Walk 20 minutes",
            streak = 1,
            createdAt = Instant.now().minusSeconds(86400).toString()
        )
    )

    override suspend fun getHabits(page: Int, limit: Int): HabitListResponse {
        val safePage = page.coerceAtLeast(1)
        val safeLimit = limit.coerceAtLeast(1)
        val startIndex = (safePage - 1) * safeLimit

        synchronized(lock) {
            if (startIndex >= habits.size) {
                return HabitListResponse(habits = emptyList(), nextPage = null)
            }

            val endIndex = (startIndex + safeLimit).coerceAtMost(habits.size)
            val nextPage = if (endIndex < habits.size) safePage + 1 else null
            return HabitListResponse(
                habits = habits.subList(startIndex, endIndex).toList(),
                nextPage = nextPage
            )
        }
    }

    override suspend fun createHabit(createHabitRequest: CreateHabitRequest): CreateHabitResponse {
        val createdHabit = HabitDto(
            id = UUID.randomUUID().toString(),
            name = createHabitRequest.name,
            streak = 0,
            createdAt = Instant.now().toString()
        )

        synchronized(lock) {
            habits.add(0, createdHabit)
        }

        return CreateHabitResponse(
            message = "Habit created in debug mock",
            habit = createdHabit
        )
    }

    override suspend fun deleteHabit(id: String): DeleteHabitResponse {
        synchronized(lock) {
            habits.removeAll { it.id == id }
        }
        return DeleteHabitResponse(message = "Habit deleted in debug mock")
    }

    override suspend fun updateHabit(id: String, updateHabitRequest: UpdateHabitRequest): UpdateHabitResponse {
        synchronized(lock) {
            val index = habits.indexOfFirst { it.id == id }
            if (index == -1) {
                return UpdateHabitResponse(message = "Habit not found in debug mock", habit = null)
            }

            val currentHabit = habits[index]
            val updatedHabit = currentHabit.copy(name = updateHabitRequest.name)
            habits[index] = updatedHabit
            return UpdateHabitResponse(
                message = "Habit updated in debug mock",
                habit = updatedHabit
            )
        }
    }

    override suspend fun checkIn(checkInRequest: CheckInRequest): CheckInResponse {
        val checkIn = synchronized(lock) {
            val index = habits.indexOfFirst { it.id == checkInRequest.habitId }
            if (index != -1) {
                val currentHabit = habits[index]
                habits[index] = currentHabit.copy(streak = (currentHabit.streak ?: 0) + 1)
            }
            CheckIn(
                id = UUID.randomUUID().toString(),
                habitId = checkInRequest.habitId,
                habitDay = LocalDate.now().toString()
            )
        }

        return CheckInResponse(
            message = "Habit checked in using debug mock",
            checkIn = checkIn
        )
    }
}
