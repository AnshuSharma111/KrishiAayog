package com.bytebandits.krishiaayog.DataClass

data class LoginRequest(
    val username: String,
    val password: String
)


data class LoginResponse(
    val message: String,
    val id: String,
    val token: String,
    val username: String,
    val phoneno: String
)
