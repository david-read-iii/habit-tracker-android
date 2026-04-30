package com.davidread.habittracker.common.usecase

import com.davidread.habittracker.common.database.HabitTrackerDatabase
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import com.davidread.habittracker.common.util.Logger
import javax.inject.Inject

private const val TAG = "LogoutUseCase"

class LogoutUseCase @Inject constructor(
    private val authenticationTokenRepository: AuthenticationTokenRepository,
    private val database: HabitTrackerDatabase,
    private val logger: Logger
) {

    suspend operator fun invoke() {
        try {
            authenticationTokenRepository.clearAuthenticationToken()
        } catch (e: Exception) {
            logger.e(TAG, "Error clearing authentication token during logout", e)
        }

        try {
            database.habitDao().clearAll()
            database.remoteKeyDao().clearRemoteKeys()
        } catch (e: Exception) {
            logger.e(TAG, "Error clearing database during logout", e)
        }
    }
}
