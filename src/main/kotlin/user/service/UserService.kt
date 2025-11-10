package com.overtheinfinite.user.service

import com.overtheinfinite.security.AesEncryptor
import com.overtheinfinite.security.CustomPasswordEncoder
import com.overtheinfinite.security.JwtTokenProvider
import com.overtheinfinite.user.domain.RoleType
import com.overtheinfinite.user.domain.User
import com.overtheinfinite.user.dto.LoginRequest
import com.overtheinfinite.user.dto.UserCreateRequest
import com.overtheinfinite.user.repository.UserRepository
import io.jsonwebtoken.JwtException
import org.springframework.transaction.annotation.Transactional // ⬅️ Correct Import!
import org.springframework.stereotype.Service

@Service
class UserService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val nameEncryptor: AesEncryptor, // 💡 AesEncryptor 주입
    private val customPasswordEncoder: CustomPasswordEncoder,
    private val userRepository: UserRepository,
) {
    fun processUserRequest(authHeader: String): String {
        // 1. "Bearer " 접두사 제거
        if (!authHeader.startsWith("Bearer ")) {
            throw IllegalArgumentException("Authorization header must start with Bearer")
        }
        val token = authHeader.substring(7)

        try {
            // 2. 토큰 검증 및 클레임 추출
            val claims = jwtTokenProvider.getClaims(token)

            // 3. 추출된 정보 사용
            val userId = claims.subject.toLong()
            val role = claims["role"] as String

            println("Authenticated User ID: $userId, Role: $role")

            // 4. DB에서 실제 유저 정보 조회 등의 로직 수행...
            // userRepository.findById(userId)

            return "Request processed successfully for user $userId"

        } catch (e: JwtException) {
            // 토큰 유효성 검증 실패 (서명 오류, 만료 등)
            println("JWT Validation Failed: ${e.message}")
            throw IllegalAccessException("Invalid or expired token.")
        }
    }

    // 임시로 성공 메시지를 반환합니다. 실제로는 Repository를 호출해야 합니다.
    fun createUser(request: UserCreateRequest): String {
        val encryptedName = nameEncryptor.encrypt(request.name)

        val hashedPassword = customPasswordEncoder.encode(request.password);

        val result = userRepository.save(
            User(
                name=encryptedName,
                loginId=request.loginId,
                hashedPassword=hashedPassword,
                role= RoleType.USER,
            )
        )
        // JWT 토큰으로 export 할때 비밀번호가 알아서 빠짐
        return jwtTokenProvider.createToken(result)
    }

    @Transactional(readOnly = true)
    fun validateUser(request: LoginRequest): String? {
        val user = userRepository.findByLoginId(request.loginId) ?: return null;

        val passwordMatches = customPasswordEncoder.matches(
            request.password, // 💡 사용자가 입력한 평문 비밀번호
            user.hashedPassword // 💡 DB에 저장된 기존 해시값
        )

        return if (passwordMatches) {
            // 3. 토큰 생성 및 반환
            // createToken(userId: Long, role: String) 시그니처를 따른다고 가정합니다.

            jwtTokenProvider.createToken(user)
        } else {
            null;
        }
    }
}