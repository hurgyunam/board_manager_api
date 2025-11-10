package com.overtheinfinite.user.repository

import com.overtheinfinite.user.domain.QUser
import com.overtheinfinite.user.domain.User
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
// ✨ QuerydslRepositorySupport를 상속받고 엔티티 클래스를 지정합니다.
class UserRepositoryImpl(
    private val entityManager: EntityManager
) : QuerydslRepositorySupport(User::class.java), UserRepositoryCustom {

    // JPAQueryFactory를 사용하여 쿼리를 생성합니다.
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    // Q-Class 인스턴스를 사용합니다.
    private val user: QUser = QUser.user

    override fun findUsersByNameContains(namePattern: String): List<User> {
        return queryFactory
            .selectFrom(user)
            .where(user.name.contains(namePattern))
            .fetch() // 결과 리스트를 반환
    }

    override fun updateHashedPasswordByLoginId(loginId: String, newHashedPassword: String): Long {
        // executeUpdate()를 통해 벌크 업데이트 실행
        return queryFactory
            .update(user)
            .set(user.hashedPassword, newHashedPassword)
            .where(user.loginId.eq(loginId))
            .execute()
    }
}