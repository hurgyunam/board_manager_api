package com.overtheinfinite.user.service

import com.overtheinfinite.security.AesEncryptor
import com.overtheinfinite.security.CustomPasswordEncoder
import com.overtheinfinite.security.JwtTokenProvider
import com.overtheinfinite.user.domain.RoleType
import com.overtheinfinite.user.domain.User
import com.overtheinfinite.user.dto.LoginRequest
import com.overtheinfinite.user.dto.UserCreateRequest
import com.overtheinfinite.user.repository.UserRepository
import org.springframework.transaction.annotation.Transactional // ⬅️ Correct Import!
import org.springframework.stereotype.Service

@Service
class UserService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val nameEncryptor: AesEncryptor, // 💡 AesEncryptor 주입
    private val customPasswordEncoder: CustomPasswordEncoder,
    private val userRepository: UserRepository,
) {

    // 임시로 성공 메시지를 반환합니다. 실제로는 Repository를 호출해야 합니다.
    fun createUser(request: UserCreateRequest): String {
        val encryptedName = nameEncryptor.encrypt(request.email)

        val hashedPassword = customPasswordEncoder.encode(request.password);

        val result = userRepository.save(
            User(
                email=encryptedName,
                username=request.username,
                hashedPassword=hashedPassword,
                role= RoleType.USER,
            )
        )
        // JWT 토큰으로 export 할때 비밀번호가 알아서 빠짐
        return jwtTokenProvider.createToken(result.id.toString(), request.username, request.email, result.role)
    }

    @Transactional(readOnly = true)
    fun validateUser(request: LoginRequest): String? {
        val user: User = userRepository.findByUsername(request.username) ?: return null;

        val decryptedEmail = nameEncryptor.decrypt(user.email)
        val passwordMatches = customPasswordEncoder.matches(
            request.password, // 💡 사용자가 입력한 평문 비밀번호
            user.hashedPassword // 💡 DB에 저장된 기존 해시값
        )

        return if (passwordMatches) {
            // 3. 토큰 생성 및 반환
            // createToken(userId: Long, role: String) 시그니처를 따른다고 가정합니다.

            jwtTokenProvider.createToken(user.id.toString(), user.username, decryptedEmail, user.role)
        } else {
            null;
        }
    }
}