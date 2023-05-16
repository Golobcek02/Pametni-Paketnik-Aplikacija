package com.example.pametnipaketnik.API

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginInterface {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
//    suspend fun getData(): LoginResponse
}
