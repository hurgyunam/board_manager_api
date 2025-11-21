package com.overtheinfinite.board.domain

import com.overtheinfinite.user.domain.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime


@EntityListeners(AuditingEntityListener::class)
@Entity
data class PostComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val comment: String,

    @ManyToOne(fetch = FetchType.LAZY) // User 엔티티와의 다대일(N:1) 관계
    @JoinColumn(name = "postId")      // 외래 키 컬럼 이름 지정
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY) // User 엔티티와의 다대일(N:1) 관계
    @JoinColumn(name = "postCommentId")      // 외래 키 컬럼 이름 지정
    val parentComment: PostComment?,

    @ManyToOne(fetch = FetchType.LAZY) // User 엔티티와의 다대일(N:1) 관계
    @JoinColumn(name = "writerId")      // 외래 키 컬럼 이름 지정
    val writer: User,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP) // java.util.Date 사용 시 필요
    @Column(columnDefinition = "TIMESTAMP") // ✨ DB 타입 강제
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP) // java.util.Date 사용 시 필요
    @Column(columnDefinition = "TIMESTAMP") // ✨ DB 타입 강제
    var updatedDate: LocalDateTime? = null

    )