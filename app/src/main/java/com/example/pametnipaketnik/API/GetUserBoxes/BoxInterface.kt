package com.example.pametnipaketnik.API.GetUserBoxes

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BoxInterface {
    @GET("getEntries/{id}")
    suspend fun getUserBoxes(@Path("id") userId: String): Response<List<Box>>
}