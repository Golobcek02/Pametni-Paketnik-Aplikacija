package com.example.pametnipaketnik.API.GetUserBoxes

data class GetUserBoxesResponse(
    val allBoxes: List<Box>,
    val usernames: List<List<String>>
)