package com.example.pametnipaketnik.API.GetUserOrders

import com.google.gson.annotations.SerializedName

data class Order (
    @SerializedName("ID")
    val id: String,
    @SerializedName("BoxID")
    val boxId: Int,
    @SerializedName("Status")
    val status: String,
    @SerializedName("PageUrl")
    val pageUrl: String,
    @SerializedName("DeliveryTime")
    val deliveryTime: String,
    @SerializedName("Items")
    val items: List<String>?
)