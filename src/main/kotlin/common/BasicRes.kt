package com.overtheinfinite.common

data class BasicRes<T>(
    private var result: Boolean,
    private var data: T,
) {
}