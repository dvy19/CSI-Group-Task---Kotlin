package com.example.jobportal

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
    val password2: String,
    val first_name: String,
    val last_name: String,
    val phone: String
)

data class SignupResponse(
    val user: User?,
    val phone: String?,
    val refresh: String?,
    val access: String?
)

data class User(
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String
)

interface JobSeekerApi {
    @Headers("Content-Type: application/json")
    @POST("auth/signup/")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>
}
