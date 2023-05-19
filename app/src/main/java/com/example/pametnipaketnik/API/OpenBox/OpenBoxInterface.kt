package com.example.pametnipaketnik.API.OpenBox

import com.example.pametnipaketnik.ui.notifications.OpenBoxRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenBoxInterface {
    @Headers("Content-Type: application/json", "Authorization: Bearer 9ea96945-3a37-4638-a5d4-22e89fbc998f")
    @POST("sandbox/v1/Access/openbox")
    suspend fun openBox(@Body request: OpenBoxRequest): OpenBoxResponse

}