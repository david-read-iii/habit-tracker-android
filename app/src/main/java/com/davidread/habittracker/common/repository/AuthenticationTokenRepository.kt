package com.davidread.habittracker.common.repository

import android.app.Application
import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import com.davidread.habittracker.common.model.Result
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import javax.inject.Inject

private const val PREFS_NAME = "secure_prefs"
private const val KEYSET_NAME = "auth_keyset"
private const val MASTER_KEY_URI = "android-keystore://auth_master_key"
private const val TOKEN_KEY = "auth_token"

class AuthenticationTokenRepository @Inject constructor(private val application: Application) {

    private val aead by lazy {
        val keysetHandle: KeysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(application, KEYSET_NAME, PREFS_NAME)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
        keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    fun saveAuthenticationToken(token: String): Result<Unit> {
        try {
            val encryptedToken = aead.encrypt(token.toByteArray(), null)
            val encryptedTokenBase64 = Base64.encodeToString(encryptedToken, Base64.DEFAULT)
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
            val encryptedTokenBytes = Base64.decode(encryptedTokenBase64, Base64.DEFAULT)
            val decryptedToken = String(aead.decrypt(encryptedTokenBytes, null))
            return Result.Success(decryptedToken)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun clearAuthenticationToken() {
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            remove(TOKEN_KEY)
        }
    }
}
