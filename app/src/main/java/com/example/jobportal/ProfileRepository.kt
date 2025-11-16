package com.example.jobportal

import retrofit2.Response

class ProfileRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getProfile(token: String): Result<ProfileResponse> {
        return try {
            val response = apiService.getProfile("Bearer $token")

            if (response.isSuccessful) {
                val profile = response.body()
                if (profile != null) {
                    Result.success(profile)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                // Handle HTTP error codes
                val errorMessage = when (response.code()) {
                    401 -> "Unauthorized - Please login again"
                    404 -> "Profile not found"
                    500 -> "Server error"
                    else -> "Error: ${response.code()} - ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}