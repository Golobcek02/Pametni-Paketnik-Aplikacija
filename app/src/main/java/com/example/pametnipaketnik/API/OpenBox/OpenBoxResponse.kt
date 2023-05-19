package com.example.pametnipaketnik.API.OpenBox

data class OpenBoxResponse(
    val result: Int,
    val message: String,
    val validationErrors: Map<String, String>,
    val errorNumber: Int,
    val data: String
)