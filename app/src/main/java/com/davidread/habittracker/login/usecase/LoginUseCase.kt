package com.davidread.habittracker.login.usecase

import android.util.Log
import com.davidread.habittracker.login.model.LoginRequest
import com.davidread.habittracker.login.model.LoginResult
import com.davidread.habittracker.login.repository.LoginRepository
import javax.inject.Inject

private const val TAG = "LoginUseCase"

class LoginUseCase @Inject constructor(private val loginRepository: LoginRepository) {

    suspend operator fun invoke(loginRequest: LoginRequest): LoginResult {
        try {
            val loginResponse = loginRepository.login(loginRequest)
            // TODO: Store token somewhere somehow.
            return LoginResult(navigateToListScreen = true)
        } catch (exception: Exception) {
            Log.e(TAG, "Error logging in", exception)
            return LoginResult(showErrorDialog = true)
        }
    }
}
