package com.example.pametnipaketnik.API.Login

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterface {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
