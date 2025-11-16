package com.example.jobportal

import retrofit2.Response

class ProfileRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getProfile(token: String): Result<ProfileResponse> {
        return try {
            val response = apiService.getProfile("Bearer $token")
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}