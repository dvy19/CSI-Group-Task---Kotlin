package com.example.jobportal

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val refresh: String,
    val access: String
)


interface SignIn {
    @Headers("Content-Type: application/json")
    @POST("auth/login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}