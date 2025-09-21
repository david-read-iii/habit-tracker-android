package com.davidread.habittracker.common.network

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import okhttp3.Interceptor
import okhttp3.Response

private const val LOGIN_PATH_POSTFIX = "/api/login"
private const val SIGNUP_PATH_POSTFIX = "/api/signup"
private const val AUTHORIZATION_HEADER_NAME = "Authorization"
private const val AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer"

class AuthenticationInterceptor(private val tokenRepository: AuthenticationTokenRepository) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Exclude login and signup services.
        val excludedPaths = listOf(LOGIN_PATH_POSTFIX, SIGNUP_PATH_POSTFIX)
        if (excludedPaths.any { request.url.encodedPath.endsWith(it) }) {
            return chain.proceed(request)
        }

        // Add token for all other requests.
        val requestBuilder = request.newBuilder()
        val tokenResult = tokenRepository.getAuthenticationToken()
        if (tokenResult is Result.Success) {
            requestBuilder.addHeader(
                AUTHORIZATION_HEADER_NAME,
                "$AUTHORIZATION_HEADER_VALUE_PREFIX ${tokenResult.data}"
            )
        }

        return chain.proceed(requestBuilder.build())
    }
}
