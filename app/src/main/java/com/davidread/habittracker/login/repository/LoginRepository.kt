package com.davidread.habittracker.login.repository

import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.service.LoginService
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginService: LoginService) {

    suspend fun login(loginRequest: LoginRequest) = loginService.login(loginRequest)
}
