package com.davidread.habittracker.fakes

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.signup.model.SignUpRequest
import com.davidread.habittracker.signup.model.SignUpResponse
import com.davidread.habittracker.signup.repository.SignUpRepository
import io.mockk.every
import io.mockk.mockk
import retrofit2.HttpException

class FakeSignUpRepositoryImpl : SignUpRepository {

    var signUpResponseType = SignUpResponseType.SUCCESS

    override suspend fun signUp(signUpRequest: SignUpRequest) = when (signUpResponseType) {
        SignUpResponseType.SUCCESS -> Result.Success(SignUpResponse(token = "12345"))

        SignUpResponseType.ERROR_400 -> Result.Error(mockk<HttpException> {
            every { code() } returns 400
        })

        SignUpResponseType.GENERIC_ERROR -> Result.Error(Exception())
    }

    enum class SignUpResponseType { SUCCESS, ERROR_400, GENERIC_ERROR }
}
