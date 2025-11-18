package com.overtheinfinite.user.repository

import com.overtheinfinite.user.domain.User

// QueryDSL을 사용하여 구현할 커스텀 메서드를 정의합니다.
interface UserRepositoryCustom {

    // 예시: QueryDSL을 사용하여 특정 이름 패턴을 가진 사용자 리스트를 조회하는 메서드
    fun findUsersByEmailContains(namePattern: String): List<User>

    // 예시: loginId로 사용자 비밀번호만 업데이트하는 메서드 (벌크 업데이트)
    fun updateHashedPasswordByUserName(username: String, newHashedPassword: String): Long
}