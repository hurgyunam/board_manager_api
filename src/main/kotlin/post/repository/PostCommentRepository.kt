package com.overtheinfinite.board.repository

import com.overtheinfinite.board.domain.PostComment
import com.overtheinfinite.board.dto.PostCommentResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostCommentRepository: JpaRepository<PostComment, Long> {
    fun findByPostId(postId: Long, pageable: Pageable): Page<PostComment>
}