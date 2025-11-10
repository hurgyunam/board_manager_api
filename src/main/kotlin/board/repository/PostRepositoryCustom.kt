package com.overtheinfinite.board.repository

import com.overtheinfinite.board.domain.Post
import com.overtheinfinite.board.dto.PostResponse
import com.overtheinfinite.board.dto.SearchPostRequest
import org.springframework.data.domain.Page

interface PostRepositoryCustom {
    /**
     * SearchPostRequest를 받아 조건 검색 및 페이징 처리
     */
    fun search(request: SearchPostRequest): Page<PostResponse>
}