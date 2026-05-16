package com.davidread.habittracker.fakes

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginResponse
import com.davidread.habittracker.login.repository.LoginRepository
import io.mockk.every
import io.mockk.mockk
import retrofit2.HttpException

class FakeLoginRepositoryImpl : LoginRepository {

    var loginResponseType = LoginResponseType.SUCCESS

    override suspend fun login(loginRequest: LoginRequest) = when (loginResponseType) {
        LoginResponseType.SUCCESS -> Result.Success(LoginResponse(token = "12345"))

        LoginResponseType.ERROR_400 -> Result.Error(mockk<HttpException> {
            every { code() } returns 400
        })

        LoginResponseType.GENERIC_ERROR -> Result.Error(Exception())
    }

    enum class LoginResponseType { SUCCESS, ERROR_400, GENERIC_ERROR }
}
