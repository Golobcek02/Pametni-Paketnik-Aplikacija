package com.example.pametnipaketnik.API.Register

import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterInterface {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}