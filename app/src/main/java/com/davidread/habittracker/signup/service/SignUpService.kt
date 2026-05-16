package com.davidread.habittracker.signup.service

import com.davidread.habittracker.signup.model.SignUpRequest
import com.davidread.habittracker.signup.model.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService {

    @POST("api/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): SignUpResponse
}
