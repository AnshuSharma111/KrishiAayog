package com.bytebandits.krishiaayog.DataClass

data class SignUpRequest(
    val name: String,
    val phoneno: String,
    val username: String,
    val password: String
)

data class AuthResponse(
    val message: String,
    val id: String,
    val token: String,
    val username: String,
    val phoneno: String
)
