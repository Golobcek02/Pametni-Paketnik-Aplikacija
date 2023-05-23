package com.example.pametnipaketnik.API.GetUserBoxes

import com.google.gson.annotations.SerializedName
import com.mongodb.Bytes
import org.bson.types.ObjectId
import java.nio.ByteBuffer

data class Box(
    @SerializedName("ID")
    val id: String,

    @SerializedName("DeliveryId")
    val DeliveryId: Int,

    @SerializedName("BoxId")
    val boxId: Int,

    @SerializedName("Latitude")
    val Latitude: Double,

    @SerializedName("Longitude")
    val Longitude: Double,

    @SerializedName("TimeAccessed")
    val TimeAccessed: Long,

    @SerializedName("LoggerId")
    val LoggerId: String,

    @SerializedName("EntryType")
    val EntryType: String


)

