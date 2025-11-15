package com.example.jobportal

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

import com.google.gson.annotations.SerializedName
import retrofit2.Call


data class JobCreateRequest(
    val job_title: String,
    val job_description: String,
    val location: String,
    val company_name: String,
    val job_type: String,
    val salary: String,
    val category: String,
    val job_tags: String,
    val required_experience: String,
    val required_skills: String,
    val required_education: String,
    val required_languages: String,
    val author_email: String,
    val author_name: String,
    val company_logo: String? = null
)


data class JobCreateResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("author") val author: Int,
    @SerializedName("author_email") val authorEmail: String,
    @SerializedName("author_name") val authorName: String,
    @SerializedName("title") val jobTitle: String,  // Changed from "job_title"
    @SerializedName("description") val jobDescription: String,  // Changed from "job_description"
    @SerializedName("location") val location: String,
    @SerializedName("company_name") val companyName: String,
    @SerializedName("company_logo") val companyLogo: String?,
    @SerializedName("company_logo_url") val companyLogoUrl: String?,
    @SerializedName("job_type") val jobType: String,
    @SerializedName("salary") val salary: String,
    @SerializedName("category") val category: String,
    @SerializedName("job_tags") val jobTags: String,
    @SerializedName("required_experience") val requiredExperience: String,
    @SerializedName("required_skills") val requiredSkills: String,
    @SerializedName("required_education") val requiredEducation: String,
    @SerializedName("required_languages") val requiredLanguages: String,
    @SerializedName("created_at") val createdAt: String
)



interface JobApi {

    @Multipart
    @POST("jobs/post/create/")
    fun createJob(
        @Header("Authorization") token: String,
        @Part("title") jobTitle: RequestBody,  // Changed from "job_title"
        @Part("description") jobDescription: RequestBody,  // Changed from "job_description"
        @Part("location") location: RequestBody,
        @Part("company_name") companyName: RequestBody,
        @Part("job_type") jobType: RequestBody,
        @Part("salary") salary: RequestBody,
        @Part("category") category: RequestBody,
        @Part("job_tags") jobTags: RequestBody,
        @Part("required_experience") requiredExperience: RequestBody,
        @Part("required_skills") requiredSkills: RequestBody,
        @Part("required_education") requiredEducation: RequestBody,
        @Part("required_languages") requiredLanguages: RequestBody,
        @Part("author_email") authorEmail: RequestBody,
        @Part("author_name") authorName: RequestBody,
        @Part company_logo: MultipartBody.Part?
    ): Call<JobCreateResponse>
}