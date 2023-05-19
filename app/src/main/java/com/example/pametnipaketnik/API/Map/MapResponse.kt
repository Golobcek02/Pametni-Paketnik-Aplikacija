package com.example.pametnipaketnik.API.Map

data class MapResponse(
    var allBoxes: List<BoxesStruct>
)
data class BoxesStruct(
    var ID: String,
    var BoxId: Int,
    var Latitude: Double,
    var Longitude: Double,
    var OwnerId: String,
    var AccessIds: List<String>,
)