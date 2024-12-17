package eu.tutorials.courseapplication.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String, refreshToken: String) {
        with(sharedPreferences.edit()) {
            putString("jwt_token", token)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }


    fun clearToken() {
        with(sharedPreferences.edit()) {
            remove("jwt_token")
            remove("refresh_token")
            apply()
        }
    }

    fun isTokenAvailable(): Boolean {
        return getRefreshToken() != null
    }
}