package com.example.jobportal

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("auth/profile/")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>
}