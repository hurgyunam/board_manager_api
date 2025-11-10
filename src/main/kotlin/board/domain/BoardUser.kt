package com.overtheinfinite.board.domain

import com.overtheinfinite.user.domain.RoleType
import com.overtheinfinite.user.domain.User
import jakarta.persistence.*

@Entity
data class BoardUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY) // User 엔티티와의 다대일(N:1) 관계
    @JoinColumn(name = "userId")      // 외래 키 컬럼 이름 지정
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY) // Board 엔티티와의 다대일(N:1) 관계
    @JoinColumn(name = "boardId")     // 외래 키 컬럼 이름 지정
    val board: Board,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: BoardRole,
    )