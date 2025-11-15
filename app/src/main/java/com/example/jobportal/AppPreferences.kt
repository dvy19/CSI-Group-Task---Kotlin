package com.example.jobportal

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("JobPortalPrefs", Context.MODE_PRIVATE)

    // Save tokens after successful signup
    fun saveTokens(accessToken: String, refreshToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", accessToken)
        editor.putString("REFRESH_TOKEN", refreshToken)
        editor.apply()
    }

    // Get saved access token
    fun getAccessToken(): String? {
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    // Get saved refresh token
    fun getRefreshToken(): String? {
        return sharedPreferences.getString("REFRESH_TOKEN", null)
    }

    // Clear tokens on logout
    fun clearTokens() {
        val editor = sharedPreferences.edit()
        editor.remove("ACCESS_TOKEN")
        editor.remove("REFRESH_TOKEN")
        editor.apply()
    }
}