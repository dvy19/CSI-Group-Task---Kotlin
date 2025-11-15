package com.example.jobportal

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

// Community Post Data Classes
data class CreateCommunityPostRequest(
    val content: String,
    val image: String? = null
)

data class CommunityPostResponse(
    val id: Long,
    val author: String,
    val content: String,
    val media: String?,
    val created_at: String,
    val likes_count: Int,
    val comments_count: Int
)

// Community Post Interface
interface CommPostPost {
    @Multipart
    @POST("community/posts/")
    fun createPost(
        @Header("Authorization") token: String,
        @Part content: MultipartBody.Part,
        @Part image: MultipartBody.Part?
    ): Call<CommunityPostResponse>

}