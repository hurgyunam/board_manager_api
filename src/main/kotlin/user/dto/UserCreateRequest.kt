package com.overtheinfinite.user.dto
data class UserCreateRequest(
    val email: String,
    val username: String,
    val password: String
)