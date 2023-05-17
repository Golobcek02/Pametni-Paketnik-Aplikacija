package com.example.pametnipaketnik.API.GetUserBoxes

import com.google.gson.annotations.SerializedName

data class Box(
    @SerializedName("_id")
    val id: String,
    @SerializedName("boxid")
    val boxId: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("ownerid")
    val ownerId: String,
    @SerializedName("accessids")
    val accessIds: List<String>
)
