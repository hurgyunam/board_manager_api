package com.overtheinfinite.admin.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
data class Board (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP) // java.util.Date 사용 시 필요
    @Column(columnDefinition = "TIMESTAMP") // ✨ DB 타입 강제
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP) // java.util.Date 사용 시 필요
    @Column(columnDefinition = "TIMESTAMP") // ✨ DB 타입 강제
    var updatedDate: LocalDateTime? = null
)