package com.davidread.habittracker.signup.repository

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.signup.model.SignUpRequest
import com.davidread.habittracker.signup.service.SignUpService
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(private val signUpService: SignUpService) :
    SignUpRepository {

    override suspend fun signUp(signUpRequest: SignUpRequest) = try {
        val signUpResponse = signUpService.signUp(signUpRequest)
        Result.Success(signUpResponse)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
