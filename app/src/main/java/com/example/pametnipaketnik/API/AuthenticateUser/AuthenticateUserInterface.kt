package com.example.pametnipaketnik.API.AuthenticateUser

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthenticateUserInterface {
    @POST("authenticateUser")
    suspend fun authenticateUser(@Body request: AuthenticateUserRequest): AuthenticateUserResponse
}