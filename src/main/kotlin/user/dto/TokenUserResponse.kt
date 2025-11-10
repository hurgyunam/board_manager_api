package com.overtheinfinite.user.dto

import com.overtheinfinite.user.domain.RoleType
import io.jsonwebtoken.Claims

data class TokenUserResponse(
    val userId: Long,
    val loginId: String,
    val name: String,
    val role: RoleType // 💡 RoleType Enum으로 변환하여 저장
) {
    // 💡 Claims 객체를 받아 TokenUserResponse 객체를 생성하는 주 생성자
    constructor(claims: Claims) : this(
        // 'subject'는 기본적으로 String이므로 Long으로 변환
        userId = claims.subject.toLong(),
        // Claims에서 각 필드를 추출
        loginId = claims["loginId"] as String,
        name = claims["name"] as String,
        // String으로 저장된 role을 RoleType Enum으로 변환
        role = RoleType.valueOf(claims["role"] as String)
    )
}