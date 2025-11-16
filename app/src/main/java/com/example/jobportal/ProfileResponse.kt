package com.example.jobportal

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("profile_picture")
    val profilePicture: String?,

    @SerializedName("cover_image")
    val coverImage: String?,

    @SerializedName("bio")
    val bio: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("profession")
    val profession: String?,

    @SerializedName("skills")
    val skills: List<String>?,

    @SerializedName("posts_count")
    val postsCount: Int?,

    @SerializedName("followers_count")
    val followersCount: Int?,

    @SerializedName("following_count")
    val followingCount: Int?
) {
    fun getFullName(): String {
        return "$firstName $lastName".trim()
    }

    fun getSkillsString(): String {
        return skills?.joinToString(" • ") ?: "No skills added"
    }
}