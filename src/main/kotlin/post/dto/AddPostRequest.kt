package com.overtheinfinite.board.dto

data class AddPostRequest (
    val title: String,
    val content: String,
    val boardId: Long,
)