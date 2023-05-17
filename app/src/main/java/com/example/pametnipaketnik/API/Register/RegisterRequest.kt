package com.example.pametnipaketnik.API.Register

data class RegisterRequest(
    val Name: String,
    val Surname: String,
    val Username: String,
    val Email: String,
    val Password: String
)