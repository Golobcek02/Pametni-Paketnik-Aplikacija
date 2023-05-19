package com.example.pametnipaketnik.API.Map

import retrofit2.http.GET
import retrofit2.http.Path

interface MapInterface {
    @GET("getUserBoxes/{id}")
    suspend fun getUserBoxes(@Path("id") id: String): MapResponse
}