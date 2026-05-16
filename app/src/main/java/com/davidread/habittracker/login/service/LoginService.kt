package com.davidread.habittracker.login.service

import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("api/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}
