package com.overtheinfinite.board.repository

import com.overtheinfinite.board.domain.Post
import com.overtheinfinite.board.domain.QPost
import com.overtheinfinite.board.dto.PostResponse
import com.overtheinfinite.board.dto.SearchPostRequest
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

// Querydsl에서 자동 생성된 Q-클래스를 import 합니다. (예: QPost)
// import com.example.project.entity.QPost

@Repository
class PostRepositoryImpl(
    private val entityManager: EntityManager
) : PostRepositoryCustom {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)
    private val post: QPost = QPost.post // Querydsl 자동 생성 클래스

    override fun search(request: SearchPostRequest): Page<PostResponse> {

        // 1. 페이징 정보 추출 및 Pageable 객체 생성
        // pageNo는 보통 0부터 시작하므로 1을 빼줍니다.
        val pageNumber = if (request.pageNo > 0) request.pageNo.toInt() - 1 else 0
        val pageSize = request.pagePostCount.toInt()

        // PageRequest: 페이징 정보를 담는 객체
        val pageable = PageRequest.of(pageNumber, pageSize)

        // 2. Querydsl 쿼리 작성 (콘텐츠 조회)
        val content = queryFactory
            .selectFrom(post)
            .where(
                // 동적 쿼리: boardId가 있으면 조건 추가
                request.boardId?.let { post.board.id.eq(it) },
                // 동적 쿼리: keyword가 있으면 title이나 content에 포함되는 조건 추가
                request.keyword?.let { post.title.containsIgnoreCase(it).or(post.content.containsIgnoreCase(it)) },
                post.isBan.eq(false)
            )
            .offset(pageable.offset) // 페이징 시작 위치
            .limit(pageable.pageSize.toLong()) // 페이지 크기
            .orderBy(post.createdDate.desc()) // 정렬 기준
            .fetch()
            .map { post -> PostResponse(post) }

        // 3. 전체 개수 쿼리 작성 (총 항목 수 계산)
        val totalCount = queryFactory
            .select(post.count())
            .from(post)
            .where(
                request.boardId?.let { post.board.id.eq(it) },
                request.keyword?.let { post.title.containsIgnoreCase(it).or(post.content.containsIgnoreCase(it)) },
                post.isBan.eq(false)
            )
            .fetchOne() ?: 0L

        // 4. Page 객체 생성 및 반환
        return PageImpl(content, pageable, totalCount)
    }
}