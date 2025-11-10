package com.overtheinfinite.user.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*

// ✨ @Entity 어노테이션이 필수입니다.

@Entity
data class User(
    // ✨ @Id 어노테이션과 기본 키 설정이 필요합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Column(unique = true, nullable = false) // 로그인 ID는 고유하고 필수
    val loginId: String,

    val hashedPassword: String,

// 💡 Role 필드를 RoleType Enum으로 정의
    // @Enumerated(EnumType.ORDINAL) : DB에 숫자로 저장 (비권장)
    @Enumerated(EnumType.STRING) // ✨ DB에 'USER', 'ADMIN' 등 문자열로 저장 (권장)
    @Column(nullable = false)
    val role: RoleType,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP) // java.util.Date 사용 시 필요
    @Column(columnDefinition = "TIMESTAMP") // ✨ DB 타입 강제
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP) // java.util.Date 사용 시 필요
    @Column(columnDefinition = "TIMESTAMP") // ✨ DB 타입 강제
    var updatedDate: LocalDateTime? = null
)