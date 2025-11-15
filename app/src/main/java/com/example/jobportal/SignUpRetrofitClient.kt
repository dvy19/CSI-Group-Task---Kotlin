package com.example.jobportal

import com.example.jobportal.CommPostPost
import com.example.jobportal.JobApi
import com.example.jobportal.LoginApi
import com.example.jobportal.ProfileApi
import com.example.jobportal.SignupApi
import com.example.jobportal.CommPostGet

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SignUpRetrofitClient {
    private const val BASE_URL = "https://jobseeker-backend-django.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // For Signup operations
    val signupInstance: SignupApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SignupApi::class.java)
    }

    // For Login operations
    val loginInstance: LoginApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApi::class.java)
    }

    // For Profile operations
    val profileInstance: ProfileApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfileApi::class.java)
    }

    // For Job operations
    val jobInstance: JobApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JobApi::class.java)
    }

    // For Community Post operations (Creating posts)
    val communityPostInstance: CommPostPost by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CommPostPost::class.java)
    }

    // For Getting Community Posts operations
    val commPostGetInstance: CommPostGet by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CommPostGet::class.java)
    }
}