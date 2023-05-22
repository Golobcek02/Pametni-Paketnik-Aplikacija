package com.example.pametnipaketnik.API.GetUserBoxes

import com.google.gson.annotations.SerializedName
import com.mongodb.Bytes
import org.bson.types.ObjectId
import java.nio.ByteBuffer

data class Box(
    @SerializedName("ID")
    val id: String,
    @SerializedName("BoxId")
    val boxId: Int,
    @SerializedName("Latitude")
    val latitude: Float,
    @SerializedName("Longitude")
    val longitude: Float,
    @SerializedName("OwnerId")
    val ownerId: String,
    @SerializedName("AccessIds")
    val accessIds: List<String>,
    @SerializedName("TimeAccessed")
    val timeaccessed: Long
)
