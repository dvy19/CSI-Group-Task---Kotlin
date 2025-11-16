package com.example.jobportal

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// Response for UPDATE profile (PUT request)
data class UpdateProfileResponse(
    val skills: String,
    val role: String,
    val education_image: String?,
    val profile_image: String?,
    val resume: String?,
    val resume_image: String?
)

// Response for GET profile
data class ProfileResponse(
    val education_text: String?,
    val experience: String?,
    val languages: String?,
    val skills: String?,
    val role: String?,
    val education_image: String?,
    val profile_image: String?,
    val resume: String?,
    val resume_image: String?,
    val user: User?
)

data class User(
    val id: Int?,
    val email: String?,
    val username: String?
)

// Profile Interface
interface ProfileApi {
    @Multipart
    @PUT("auth/profile/")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("education_text") educationText: RequestBody,
        @Part("experience") experience: RequestBody,
        @Part("languages") languages: RequestBody,
        @Part("skills") skills: RequestBody,
        @Part("role") role: RequestBody,
        @Part profile_image: MultipartBody.Part?,
        @Part education_image: MultipartBody.Part?,
        @Part resume: MultipartBody.Part?,
        @Part resume_image: MultipartBody.Part?
    ): Call<UpdateProfileResponse>

    @GET("auth/profile/")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>
}