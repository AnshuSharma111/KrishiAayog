package com.bytebandits.krishiaayog.retrofitInterface

import com.bytebandits.krishiaayog.DataClass.AuthResponse
import com.bytebandits.krishiaayog.DataClass.LoginRequest
import com.bytebandits.krishiaayog.DataClass.LoginResponse
import com.bytebandits.krishiaayog.DataClass.SignUpRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginSignup {

    @POST("signup") // Adjust based on your API route
    suspend fun signup(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

fun provideLoginSignupService(): LoginSignup {
    return Retrofit.Builder().baseUrl("http://your-backend/api/auth/")
        .addConverterFactory(GsonConverterFactory.create()).build().create()
}