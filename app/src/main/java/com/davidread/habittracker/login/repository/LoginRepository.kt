package com.davidread.habittracker.login.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginResponse

interface LoginRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>
}
