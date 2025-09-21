package com.davidread.habittracker.common.repository

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import com.davidread.habittracker.common.model.Result
import com.davidread.habittracker.common.util.Base64
import com.google.crypto.tink.Aead
import javax.inject.Inject

internal const val PREFS_NAME = "secure_prefs"
internal const val TOKEN_KEY = "auth_token"

class AuthenticationTokenRepository @Inject constructor(
    private val aead: Aead,
    private val base64: Base64,
    private val application: Application
) {

    fun saveAuthenticationToken(token: String): Result<Unit> {
        try {
            val encryptedToken = aead.encrypt(token.toByteArray(), null)
            val encryptedTokenBase64 =
                base64.encodeToString(encryptedToken, android.util.Base64.DEFAULT)
            application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                putString(TOKEN_KEY, encryptedTokenBase64)
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun getAuthenticationToken(): Result<String> {
        try {
            val encryptedTokenBase64 =
                application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString(TOKEN_KEY, null)
                    ?: throw IllegalStateException("encryptedToken cannot be null")
            val encryptedTokenBytes =
                base64.decode(encryptedTokenBase64, android.util.Base64.DEFAULT)
            val decryptedToken = String(aead.decrypt(encryptedTokenBytes, null))
            return Result.Success(decryptedToken)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    // TODO: Remove this if it remains unused.
    fun clearAuthenticationToken() {
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            remove(TOKEN_KEY)
        }
    }
}
