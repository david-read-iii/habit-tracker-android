package com.davidread.habittracker.signup.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.signup.model.SignUpRequest
import com.davidread.habittracker.signup.model.SignUpUserResult
import com.davidread.habittracker.signup.repository.SignUpRepository
import retrofit2.HttpException
import javax.inject.Inject

private const val TAG = "SignUpUserUseCase"

class SignUpUserUseCase @Inject constructor(
    private val signUpRepository: SignUpRepository,
    private val logger: Logger
) {

    suspend operator fun invoke(
        email: String,
        password: String,
        timezone: String
    ): SignUpUserResult {
        val result = signUpRepository.signUp(SignUpRequest(email, password, timezone))
        return when (result) {
            is Result.Success -> result.data.token?.let {
                SignUpUserResult.Success(it)
            } ?: run {
                SignUpUserResult.NullToken
            }
            is Result.Error -> {
                logger.e(TAG, "Error signing up user", result.exception)
                if (result.exception is HttpException && result.exception.code() == 400) {
                    SignUpUserResult.EmailAlreadyUsed
                } else {
                    SignUpUserResult.GenericError
                }
            }
        }
    }
}
