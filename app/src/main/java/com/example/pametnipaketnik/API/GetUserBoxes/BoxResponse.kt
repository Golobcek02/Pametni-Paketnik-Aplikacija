package com.example.pametnipaketnik.API.GetUserBoxes

data class BoxResponse(
    val allBoxes: List<Box>,
    val usernames: List<List<String>>
)