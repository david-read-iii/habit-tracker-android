package com.davidread.habittracker.signup.service

import com.davidread.habittracker.signup.model.SignUpRequest
import com.davidread.habittracker.signup.model.SignUpResponse

/**
 * DEBUG ONLY: mock sign-up response so debug builds can run without backend connectivity.
 */
class MockSignUpService : SignUpService {

    override suspend fun signUp(signUpRequest: SignUpRequest): SignUpResponse {
        return SignUpResponse(token = "debug-token-${signUpRequest.email}")
    }
}

