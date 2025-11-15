package com.example.jobportal

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

// Profile Request - For multipart form data
// Note: We don't need a data class for request since we're using @Part

// Profile Response
data class ProfileResponse(
    val education_text: String,
    val experience: String,
    val languages: String,
    val skills: String,
    val role: String,
    val education_image: String?,
    val profile_image: String?,
    val resume: String?,
    val resume_image: String?
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
    ): Call<ProfileResponse>
}