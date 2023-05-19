package com.example.pametnipaketnik.API.CreateFaceID

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CreateFaceIDInterface {
    @Multipart
    @POST("createFaceID")
    suspend fun uploadImages(
        @Part images: List<@JvmSuppressWildcards MultipartBody.Part>
    ): Boolean
}


