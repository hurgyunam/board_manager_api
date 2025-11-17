package com.overtheinfinite.common

data class BasicRes<T>(
    val result: Boolean,
    val data: T,
)