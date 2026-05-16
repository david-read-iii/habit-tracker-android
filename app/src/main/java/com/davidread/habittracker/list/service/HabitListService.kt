package com.davidread.habittracker.list.service

import com.davidread.habittracker.list.model.CheckInRequest
import com.davidread.habittracker.list.model.CheckInResponse
import com.davidread.habittracker.list.model.CreateHabitRequest
import com.davidread.habittracker.list.model.CreateHabitResponse
import com.davidread.habittracker.list.model.DeleteHabitResponse
import com.davidread.habittracker.list.model.HabitListResponse
import com.davidread.habittracker.list.model.UpdateHabitRequest
import com.davidread.habittracker.list.model.UpdateHabitResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HabitListService {

    @GET("api/habits")
    suspend fun getHabits(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): HabitListResponse

    @POST("api/habit")
    suspend fun createHabit(@Body createHabitRequest: CreateHabitRequest): CreateHabitResponse

    @DELETE("api/habit/{id}")
    suspend fun deleteHabit(@Path("id") id: String): DeleteHabitResponse

    @PATCH("api/habit/{id}")
    suspend fun updateHabit(
        @Path("id") id: String,
        @Body updateHabitRequest: UpdateHabitRequest
    ): UpdateHabitResponse

    @POST("api/check-in")
    suspend fun checkIn(@Body checkInRequest: CheckInRequest): CheckInResponse
}
