package com.example.jobportal

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("JobPortalPrefs", Context.MODE_PRIVATE)

    // Save tokens after successful login/signup
    fun saveTokens(accessToken: String, refreshToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", accessToken)
        editor.putString("REFRESH_TOKEN", refreshToken)
        editor.apply()

        // Debug log to verify saving
        println("DEBUG: AppPreferences - Tokens saved successfully")
        println("DEBUG: AppPreferences - Access Token: $accessToken")
        println("DEBUG: AppPreferences - Refresh Token: $refreshToken")
    }

    // Get saved access token
    fun getAccessToken(): String? {
        val token = sharedPreferences.getString("ACCESS_TOKEN", null)
        println("DEBUG: AppPreferences - Getting access token: $token")
        return token
    }

    // Get saved refresh token
    fun getRefreshToken(): String? {
        val token = sharedPreferences.getString("REFRESH_TOKEN", null)
        println("DEBUG: AppPreferences - Getting refresh token: $token")
        return token
    }

    // Clear tokens on logout
    fun clearTokens() {
        val editor = sharedPreferences.edit()
        editor.remove("ACCESS_TOKEN")
        editor.remove("REFRESH_TOKEN")
        editor.apply()
        println("DEBUG: AppPreferences - Tokens cleared")
    }

    // Save user role
    fun saveUserRole(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString("USER_ROLE", role)
        editor.apply()
        println("DEBUG: AppPreferences - User role saved: $role")
    }

    // Get user role
    fun getUserRole(): String? {
        val role = sharedPreferences.getString("USER_ROLE", null)
        println("DEBUG: AppPreferences - Getting user role: $role")
        return role
    }
}