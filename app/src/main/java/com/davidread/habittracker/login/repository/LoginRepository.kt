package com.davidread.habittracker.login.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.service.LoginService
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginService: LoginService) {

    suspend fun login(loginRequest: LoginRequest) = try {
        val loginResponse = loginService.login(loginRequest)
        Result.Success(loginResponse)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
