package com.overtheinfinite.user.repository
import com.overtheinfinite.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository


// JpaRepository를 상속받아 기본적인 CRUD 기능을 제공합니다.
// 제네릭: <엔티티 클래스, 엔티티 ID 타입>
interface UserRepository : JpaRepository<User, Long> {

    // Spring Data JPA의 쿼리 메서드 기능을 사용하여 loginId로 User를 찾는 메서드를 추가할 수 있습니다.
    fun findByUsername(username: String): User?
}
