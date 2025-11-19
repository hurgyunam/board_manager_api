package com.overtheinfinite.board.dto

data class AddPostCommentRequest(
    val comment: String,
    val postId: Long,
    val parentCommentId: Long?,
)
