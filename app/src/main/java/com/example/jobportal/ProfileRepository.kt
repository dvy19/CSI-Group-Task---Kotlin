package com.example.jobportal

class ProfileRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getProfile(token: String): Result<ProfileResponse> {
        return try {
            val response = apiService.getProfile("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}