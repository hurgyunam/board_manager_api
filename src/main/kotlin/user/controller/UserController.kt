package com.overtheinfinite.user.controller

import com.overtheinfinite.security.JwtTokenProvider
import com.overtheinfinite.user.dto.TokenUserResponse
import com.overtheinfinite.user.dto.UserCreateRequest
import com.overtheinfinite.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @GetMapping("/me")
    fun getMyInfo(@RequestHeader("Authorization") authorizationHeader: String): ResponseEntity<TokenUserResponse> {
        // 1. Bearer 접두사 제거
        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val token = authorizationHeader.substring(7)

        // 2. 서비스 호출 및 정보 추출
        val userInfo = jwtTokenProvider.getClaims(token)
        val res = TokenUserResponse(userInfo);

        // 3. 유효성 검사 및 응답
        return ResponseEntity.ok(res)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // HTTP 201 응답 코드 설정
    fun createUser(@RequestBody request: UserCreateRequest): Map<String, String> {
        val jwtToken = userService.createUser(request)
        // 응답으로 생성된 ID를 반환
        return mapOf("token" to jwtToken)
    }
}