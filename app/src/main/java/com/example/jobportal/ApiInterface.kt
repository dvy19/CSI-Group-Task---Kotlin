package com.example.jobportal

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiInterface {

    @Multipart
    @PUT("auth/profile/")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("education_text") educationText: RequestBody,
        @Part("experience") experience: RequestBody,
        @Part("languages") languages: RequestBody,
        @Part("skills") skills: RequestBody,
        @Part("role") role: RequestBody,
        @Part education_image: MultipartBody.Part?,  // Make sure field name matches backend
        @Part resume_image: MultipartBody.Part?,     // Added this missing field
        @Part resume: MultipartBody.Part?            // Added this missing field
    ): Call<ResponseBody>
}