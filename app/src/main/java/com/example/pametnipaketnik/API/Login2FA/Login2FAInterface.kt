package com.example.pametnipaketnik.API.Login2FA

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Login2FAInterface {
    @Multipart
    @POST("login2fa")
    suspend fun uploadImages(
        @Part images: List<@JvmSuppressWildcards MultipartBody.Part>
    ): Boolean
}


