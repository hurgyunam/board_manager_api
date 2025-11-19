package com.overtheinfinite.board.dto

import com.overtheinfinite.board.domain.PostComment
import java.time.LocalDateTime

data class PostCommentResponse(
    val id: Long,
    val comment: String,
    val parentCommentId: Long?,
    val writerName: String,
    val createdDate: LocalDateTime?,
) {
    constructor(postComment: PostComment) : this(
        id=postComment.id ?: throw IllegalArgumentException("ID는 필수 값입니다."),
        comment=postComment.comment,
        parentCommentId=postComment.parentComment?.id,
        writerName=postComment.writer.email,
        createdDate=postComment.createdDate
    )
}
