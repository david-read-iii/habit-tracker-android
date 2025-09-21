package com.davidread.habittracker.common.repository

import android.app.Application
import android.content.SharedPreferences
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Base64
import com.google.crypto.tink.Aead
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.security.GeneralSecurityException

class AuthenticationTokenRepositoryTest {

    private val aead = mockk<Aead>()

    private val base64 = mockk<Base64>()

    private val application = mockk<Application>()

    private val authenticationTokenRepository =
        AuthenticationTokenRepository(aead, base64, application)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun test_saveAuthenticationToken_success() {
        val encryptedToken = ByteArray(0)
        every { aead.encrypt(any(), any()) } returns encryptedToken
        val encryptedTokenBase64 = "encryptedtokenbase64"
        every { base64.encodeToString(any(), any()) } returns encryptedTokenBase64
        val sharedPreferencesEditor = mockk<SharedPreferences.Editor> {
            every { putString(any(), any()) } returns this
            every { apply() } just runs
        }
        every { application.getSharedPreferences(any(), any()) } returns mockk {
            every { edit() } returns sharedPreferencesEditor
        }
        val token = "token"
        val actual = authenticationTokenRepository.saveAuthenticationToken(token)

        verify(exactly = 1) {
            aead.encrypt(token.toByteArray(), null)
        }
        verify(exactly = 1) {
            base64.encodeToString(encryptedToken, android.util.Base64.DEFAULT)
        }
        verify(exactly = 1) {
            sharedPreferencesEditor.putString(TOKEN_KEY, encryptedTokenBase64)
        }
        Assert.assertEquals(Result.Success(Unit), actual)
    }

    @Test
    fun test_saveAuthenticationToken_exception() {
        val exception = GeneralSecurityException()
        every { aead.encrypt(any(), any()) } throws exception
        val actual = authenticationTokenRepository.saveAuthenticationToken("token")

        Assert.assertEquals(Result.Error(exception), actual)
    }

    @Test
    fun test_getAuthenticationToken_success() {
        val encryptedTokenBase64 = "encryptedtokenbase64"
        val sharedPreferences = mockk<SharedPreferences> {
            every { getString(any(), any()) } returns encryptedTokenBase64
        }
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        val encryptedTokenBytes = ByteArray(0)
        every { base64.decode(any(), any()) } returns encryptedTokenBytes
        val tokenByteArray = ByteArray(0)
        every { aead.decrypt(any(), any()) } returns tokenByteArray
        val actual = authenticationTokenRepository.getAuthenticationToken()

        verify(exactly = 1) {
            sharedPreferences.getString(TOKEN_KEY, null)
        }
        verify(exactly = 1) {
            base64.decode(encryptedTokenBase64, android.util.Base64.DEFAULT)
        }
        verify(exactly = 1) {
            aead.decrypt(encryptedTokenBytes, null)
        }
        Assert.assertEquals(Result.Success(String(tokenByteArray)), actual)
    }

    @Test
    fun test_getAuthenticationToken_exception() {
        every { application.getSharedPreferences(any(), any()) } returns mockk<SharedPreferences> {
            every { getString(any(), any()) } returns null
        }
        val actual = authenticationTokenRepository.getAuthenticationToken()

        Assert.assertTrue((actual as Result.Error).exception is IllegalStateException)
        Assert.assertEquals("encryptedToken cannot be null", actual.exception.message)
    }

    @Test
    fun test_clearAuthenticationToken() {
        val sharedPreferencesEditor = mockk<SharedPreferences.Editor> {
            every { remove(any()) } returns this
            every { apply() } just runs

        }
        every { application.getSharedPreferences(any(), any()) } returns mockk {
            every { edit() } returns sharedPreferencesEditor
        }
        authenticationTokenRepository.clearAuthenticationToken()

        verify(exactly = 1) {
            sharedPreferencesEditor.remove(TOKEN_KEY)
        }
    }
}
