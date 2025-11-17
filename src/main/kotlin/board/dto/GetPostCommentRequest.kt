package com.overtheinfinite.board.dto

data class GetPostCommentRequest(
    val postId: Long,
    val pageNo: Int,
    val pageSize: Int
)
