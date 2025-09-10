package com.davidread.habittracker.common.network

import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.repository.AuthenticationTokenRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.After
import org.junit.Test

class AuthenticationInterceptorTest {

    private val authenticationTokenRepository = mockk<AuthenticationTokenRepository>()

    private val authenticationInterceptor = AuthenticationInterceptor(authenticationTokenRepository)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_success_authenticatedService() {
        val token = "12345"
        every { authenticationTokenRepository.getAuthenticationToken() } returns Result.Success(token)
        val request = mockk<Request>()
        val requestBuilder = mockk<Request.Builder> {
            every { addHeader(any(), any()) } returns this
            every { build() } returns request
        }
        val chain = mockk<Interceptor.Chain> {
            every { request() } returns mockk<Request> {
                every { url.encodedPath } returns "/api/authenticatedapi"
                every { newBuilder() } returns requestBuilder
            }
            every { proceed(any()) } returns mockk()
        }
        authenticationInterceptor.intercept(chain)

        verify(exactly = 1) {
            requestBuilder.addHeader(
                "Authorization",
                "Bearer $token"
            )
        }
        verify(exactly = 1) {
            chain.proceed(request)
        }
    }


    @Test
    fun test_success_nonAuthenticatedService() {
        val request = mockk<Request> {
            every { url.encodedPath } returns "/api/login"
        }
        val chain = mockk<Interceptor.Chain> {
            every { request() } returns request
            every { proceed(any()) } returns mockk()
        }
        authenticationInterceptor.intercept(chain)

        verify(exactly = 1) {
            chain.proceed(request)
        }
    }
}
