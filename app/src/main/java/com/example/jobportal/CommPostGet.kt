package com.example.jobportal

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

// Community Post Data Classes
data class CommunityPostsGetResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<CommunityPostGet>
)

data class CommunityPostGet(
    val id: Int,
    val author: String,
    val content: String,
    val media: String?,
    val created_at: String,
    val likes_count: Int,
    val comments_count: Int
)

// Community Posts Interface
interface CommPostGet {
    @GET("community/posts/")
    fun getCommunityPosts(
        @Header("Authorization") token: String
    ): Call<CommunityPostsGetResponse>
}