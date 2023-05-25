package com.example.pametnipaketnik.API.CreateFaceID

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CreateFaceIDInterface {
    @Multipart
    @POST("createFaceID/{id}")
    suspend fun uploadImages(
        @Path("id") userId: String,
        @Part images: List<@JvmSuppressWildcards MultipartBody.Part>
    ): Boolean
}


