package com.davidread.habittracker.list.model

data class CheckInResponse(
    val message: String?,
    val checkIn: CheckIn?
)

data class HabitListResponse(
    val habits: List<HabitDto>?,
    val nextPage: Int?
)

data class CreateHabitResponse(
    val message: String?,
    val habit: HabitDto?
)

data class DeleteHabitResponse(
    val message: String?
)

data class UpdateHabitResponse(
    val message: String?,
    val habit: HabitDto?
)

data class HabitDto(
    val id: String?,
    val name: String?,
    val streak: Int?,
    val createdAt: String?
)

data class CheckIn(
    val id: String?,
    val habitId: String?,
    val habitDay: String?
)
