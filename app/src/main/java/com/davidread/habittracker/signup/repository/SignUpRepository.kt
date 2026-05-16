package com.davidread.habittracker.signup.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.signup.model.SignUpRequest
import com.davidread.habittracker.signup.model.SignUpResponse

interface SignUpRepository {
    suspend fun signUp(signUpRequest: SignUpRequest): Result<SignUpResponse>
}
