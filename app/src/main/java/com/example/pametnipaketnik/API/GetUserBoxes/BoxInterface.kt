package com.example.pametnipaketnik.API.GetUserBoxes

import retrofit2.http.GET
import retrofit2.http.Path

interface BoxInterface {
    @GET("getUserBoxes/{id}")
    suspend fun getUserBoxes(@Path("id") userId: String): BoxResponse
}