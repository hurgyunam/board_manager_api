package com.overtheinfinite.board.dto

data class UpdatePostRequest (
    val id: Long,
    val title: String,
    val content: String,
)