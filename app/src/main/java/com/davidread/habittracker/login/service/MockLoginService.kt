package com.davidread.habittracker.login.service

import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginResponse

/**
 * DEBUG ONLY: mock login response so debug builds can run without backend connectivity.
 */
class MockLoginService : LoginService {

    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return LoginResponse(token = "debug-token-${loginRequest.email}")
    }
}
