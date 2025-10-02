package com.davidread.habittracker.login.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginUserResult
import com.davidread.habittracker.login.repository.LoginRepository
import retrofit2.HttpException
import javax.inject.Inject

private const val TAG = "LoginUserUseCase"

class LoginUserUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val logger: Logger
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): LoginUserResult {
        val result = loginRepository.login(LoginRequest(email, password))
        return when (result) {
            is Result.Success -> result.data.token?.let {
                LoginUserResult.Success(it)
            } ?: run {
                LoginUserResult.NullToken
            }

            is Result.Error -> {
                logger.e(TAG, "Error logging in user", result.exception)
                if (result.exception is HttpException && result.exception.code() == 400) {
                    LoginUserResult.IncorrectLoginCredentials
                } else {
                    LoginUserResult.GenericError
                }
            }
        }
    }
}
