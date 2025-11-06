package com.example.jobportal

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SignUpRetrofitClient {
    private const val BASE_URL = "https://jobseeker-backend-django.onrender.com/"

    // Create OkHttpClient with timeout settings
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Wait up to 30 seconds to connect
        .readTimeout(30, TimeUnit.SECONDS)    // Wait up to 30 seconds for data
        .writeTimeout(30, TimeUnit.SECONDS)   // Wait up to 30 seconds to send data
        .retryOnConnectionFailure(true)       // Retry on connection failures
        .build()

    val instance: JobSeekerApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Add the configured OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JobSeekerApi::class.java)
    }
}