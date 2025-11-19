package com.overtheinfinite.board.dto

import com.overtheinfinite.board.domain.Post
import java.time.LocalDateTime

data class PostResponse(
    val id: Long,
    val title: String,
    val writerName: String,
    val createdDate: LocalDateTime,
    /**
     * Post Entity를 파라미터로 받는 보조 생성자
     * 이 생성자는 주 생성자의 필드를 초기화하는 역할을 합니다.
     */
) {
    constructor(post: Post) : this(
        id = post.id ?: throw IllegalArgumentException("ID는 필수 값입니다."),
        title = post.title,
        writerName = post.writer.email, // Post Entity 내부의 author.name을 사용
        createdDate = post.createdDate ?: throw IllegalArgumentException("생성일자는 필수 값입니다."),
    )
}
