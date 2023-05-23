package com.example.pametnipaketnik.API

import com.example.pametnipaketnik.API.GetUserOrders.Order
import com.example.pametnipaketnik.API.GetUserOrders.OrderResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface OrderInterface {
    @GET("getUserOrders/{userId}")
    suspend fun getUserOrders(@Path("userId") userId: String): List<Order>
}