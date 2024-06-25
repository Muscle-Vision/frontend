package com.example.musclevision.data

data class TokenDto(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)
