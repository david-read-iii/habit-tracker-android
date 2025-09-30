package com.davidread.habittracker.common.usecase

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import com.davidread.habittracker.common.util.Logger
import com.davidread.habittracker.common.model.SaveAuthenticationTokenResult
import javax.inject.Inject

private const val TAG = "SaveTokenUseCase"

// TODO: Use on Login screen too!
class SaveAuthenticationTokenUseCase @Inject constructor(
    private val authenticationTokenRepository: AuthenticationTokenRepository,
    private val logger: Logger
) {

    operator fun invoke(token: String): SaveAuthenticationTokenResult {
        val result = authenticationTokenRepository.saveAuthenticationToken(token)
        return when (result) {
            is Result.Success -> SaveAuthenticationTokenResult.Success
            is Result.Error -> {
                logger.e(TAG, "Error saving token", result.exception)
                SaveAuthenticationTokenResult.Error
            }
        }
    }
}
