package com.overtheinfinite.board.repository

import com.overtheinfinite.board.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
}