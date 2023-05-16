package com.example.pametnipaketnik.API

import retrofit2.http.GET

interface LoginInterface {
    @GET("temp")
    suspend fun getData(): LoginResponse
}
