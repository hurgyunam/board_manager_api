package com.overtheinfinite.security

import com.overtheinfinite.user.domain.RoleType
import com.overtheinfinite.user.domain.User
import com.overtheinfinite.user.dto.TokenUserResponse
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.security.SignatureException
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.naming.AuthenticationException

@Component
class JwtTokenProvider(
    // application.yml에서 JWT 비밀 키를 주입받습니다.
    @Value("\${jwt.secret-key}")
    private val secretKeyString: String,

    // application.yml에서 토큰 만료 시간을 주입받습니다.
    @Value("\${jwt.expiration-ms}")
    private val expirationMs: Long
) {
    fun generateNewSecretKey(): String {
        // 💡 HS256 알고리즘은 256비트(32바이트) 이상의 키를 권장합니다.
        // 32바이트 길이의 강력한 무작위 키를 생성합니다.
        val keyBytes = ByteArray(32)
        SecureRandom().nextBytes(keyBytes)

        // SecretKey 객체를 생성합니다.
        val key: SecretKey = SecretKeySpec(keyBytes, "HmacSHA256")

        // 💡 생성된 키를 Base64로 인코딩하여 application.yml에 저장할 수 있도록 반환합니다.
        return io.jsonwebtoken.io.Encoders.BASE64.encode(key.encoded)
    }

    // 💡 Base64 디코딩된 비밀 키
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString))

    /**
     * 유저 정보(예: ID, 역할)를 클레임으로 포함하는 JWT 토큰을 생성합니다.
     * @param userId 토큰에 포함할 사용자 고유 ID
     * @param role 토큰에 포함할 사용자 역할 또는 권한
     * @return 생성된 JWT 문자열
     */
    fun createToken(id: String, username: String, email: String, role: RoleType): String {
        // 1. 토큰 만료 시간 설정
        val now = Date()
        val expirationDate = Date(now.time + expirationMs)

        // 2. JWT 생성
        return Jwts.builder()
            // 💡 클레임 설정 (토큰에 담을 유저 정보)
            .subject(id) // 토큰의 주제 (보통 유저 ID)
//            .claim("role", role)        // 추가적인 사용자 역할 클레임
            .claim("username", username) // 예시: 다른 유저 정보 클레임
            .claim("email", email)
            .claim("role", role)

            // 💡 발행 시간 및 만료 시간 설정
            .issuedAt(now)
            .expiration(expirationDate)

            // 💡 서명 설정 (비밀 키 및 알고리즘 사용)
            .signWith(key)

            .compact() // 토큰 문자열 생성
    }

    fun getTokenUserResponse(accessToken: String): TokenUserResponse {
        // 2. 서비스 호출 및 정보 추출
        val userInfo = getClaims(accessToken)
        return TokenUserResponse(userInfo);
    }

    /**
     * JWT 토큰의 유효성을 검증하고 파싱하여 클레임(Claims)을 반환합니다.
     * @param token 검증할 JWT 문자열
     * @return 유효한 경우 Claims 객체
     * @throws RuntimeException 유효성 검증 실패 시 해당 예외 발생
     */
    fun getClaims(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(key) // 토큰 서명 검증 (비밀 키 사용)
                .build()
                .parseSignedClaims(token) // 클레임을 파싱
                .payload
        } catch (e: SecurityException) {
            throw SignatureException("Invalid JWT signature: ${e.message}", e) // 서명 불일치
        } catch (e: MalformedJwtException) {
            throw MalformedJwtException("Invalid JWT token: ${e.message}", e) // 잘못된 형식
        } catch (e: ExpiredJwtException) {
            throw ExpiredJwtException(e.header, e.claims, "Expired JWT token: ${e.message}", e) // 만료
        } catch (e: UnsupportedJwtException) {
            throw UnsupportedJwtException("Unsupported JWT token: ${e.message}", e) // 지원되지 않는 형식
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("JWT claims string is empty: ${e.message}", e) // 토큰 문자열이 비어있음
        }
    }

    /**
     * 토큰에서 사용자 고유 ID (Subject)를 추출합니다.
     * @param token 검증된 JWT 문자열
     * @return 사용자 ID (String)
     */
    fun getUserIdFromToken(token: String): String {
        return getClaims(token).subject
    }

    /**
     * 토큰에서 사용자의 역할 (role)을 추출합니다.
     * @param token 검증된 JWT 문자열
     * @return 사용자 역할 (String)
     */
    fun getRoleFromToken(token: String): String {
        // 클레임 이름은 createToken에서 설정한 이름과 일치해야 합니다.
        return getClaims(token)["role"] as String
    }
}