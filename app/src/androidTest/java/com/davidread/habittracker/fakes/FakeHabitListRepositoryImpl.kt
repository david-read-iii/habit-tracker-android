package com.davidread.habittracker.fakes

import androidx.paging.PagingData
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.list.database.HabitEntity
import com.davidread.habittracker.list.model.CheckIn
import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResponse
import com.davidread.habittracker.list.model.CreateHabitRequest
import com.davidread.habittracker.list.model.CreateHabitResponse
import com.davidread.habittracker.list.model.DeleteHabitResponse
import com.davidread.habittracker.list.model.HabitDto
import com.davidread.habittracker.list.model.UpdateHabitRequest
import com.davidread.habittracker.list.model.UpdateHabitResponse
import com.davidread.habittracker.list.repository.HabitListRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException

class FakeHabitListRepositoryImpl : HabitListRepository {

    var habits = listOf(
        HabitEntity(
            id = "habit-id-1",
            name = "Drink water",
            streak = 3,
            createdAt = "2026-01-01T00:00:00.000Z"
        )
    )

    var createHabitResponseType = CreateHabitResponseType.SUCCESS
    var deleteHabitResponseType = DeleteHabitResponseType.SUCCESS
    var updateHabitResponseType = UpdateHabitResponseType.SUCCESS
    var checkInResponseType = CheckInResponseType.SUCCESS

    override fun getHabits(): Flow<PagingData<HabitEntity>> = flowOf(PagingData.from(habits))

    override suspend fun createHabit(createHabitRequest: CreateHabitRequest) =
        when (createHabitResponseType) {
            CreateHabitResponseType.SUCCESS -> Result.Success(
                CreateHabitResponse(
                    message = "Habit created",
                    habit = HabitDto(
                        id = "habit-id-created",
                        name = createHabitRequest.name,
                        streak = 0,
                        createdAt = "2026-01-01T00:00:00.000Z"
                    )
                )
            )

            CreateHabitResponseType.GENERIC_ERROR -> Result.Error(Exception())
        }

    override suspend fun deleteHabit(id: String) = when (deleteHabitResponseType) {
        DeleteHabitResponseType.SUCCESS -> Result.Success(DeleteHabitResponse(message = "Habit deleted"))

        DeleteHabitResponseType.GENERIC_ERROR -> Result.Error(Exception())
    }

    override suspend fun updateHabit(id: String, updateHabitRequest: UpdateHabitRequest) =
        when (updateHabitResponseType) {
            UpdateHabitResponseType.SUCCESS -> Result.Success(
                UpdateHabitResponse(
                    message = "Habit updated",
                    habit = HabitDto(
                        id = id,
                        name = updateHabitRequest.name,
                        streak = 0,
                        createdAt = "2026-01-01T00:00:00.000Z"
                    )
                )
            )

            UpdateHabitResponseType.GENERIC_ERROR -> Result.Error(Exception())
        }

    override suspend fun checkIn(checkInRequest: CheckInRequest) = when (checkInResponseType) {
        CheckInResponseType.SUCCESS -> Result.Success(
            CheckInResponse(
                message = "Checked in",
                checkIn = CheckIn(
                    id = "check-in-id-1",
                    habitId = checkInRequest.habitId,
                    habitDay = "2026-01-01"
                )
            )
        )

        CheckInResponseType.ERROR_400 -> Result.Error(badRequestException())

        CheckInResponseType.GENERIC_ERROR -> Result.Error(Exception())
    }

    private fun badRequestException(): HttpException = mockk {
        every { code() } returns 400
    }

    enum class CreateHabitResponseType { SUCCESS, GENERIC_ERROR }

    enum class DeleteHabitResponseType { SUCCESS, GENERIC_ERROR }

    enum class UpdateHabitResponseType { SUCCESS, GENERIC_ERROR }

    enum class CheckInResponseType { SUCCESS, ERROR_400, GENERIC_ERROR }
}
