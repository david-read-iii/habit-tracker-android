package com.davidread.habittracker.list.model

data class CheckInRequest(
    val habitId: String
)

data class CreateHabitRequest(
    val name: String
)

data class UpdateHabitRequest(
    val name: String
)
