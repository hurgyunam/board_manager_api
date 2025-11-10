package com.overtheinfinite.user.dto
data class UserCreateRequest(
    val name: String,
    val loginId: String,
    val password: String
)