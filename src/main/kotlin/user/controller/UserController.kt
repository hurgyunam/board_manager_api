package com.overtheinfinite.user.controller

import com.overtheinfinite.common.BasicRes
import com.overtheinfinite.security.JwtTokenProvider
import com.overtheinfinite.user.dto.LoginRequest
import com.overtheinfinite.user.dto.TokenUserResponse
import com.overtheinfinite.user.dto.UserCreateRequest
import com.overtheinfinite.user.service.UserService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
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
    fun getMyInfo(@CookieValue("accessToken") accessToken: String): ResponseEntity<BasicRes<TokenUserResponse>> {
        val res = jwtTokenProvider.getTokenUserResponse(accessToken)

        // 3. 유효성 검사 및 응답
        return ResponseEntity.ok(BasicRes(true, res))
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED) // HTTP 201 응답 코드 설정
    fun createUser(@RequestBody request: UserCreateRequest): BasicRes<String?> {
        userService.createUser(request)
        // 응답으로 생성된 ID를 반환
        return BasicRes(true, null)
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody request: LoginRequest, response: HttpServletResponse): BasicRes<String?> {
        val jwtToken = userService.validateUser(request)

        val cookie = Cookie("accessToken", jwtToken).apply {
            isHttpOnly = true
            secure = true
            maxAge = 30 * 60
            path = "/"
        }

        response.addCookie(cookie)

        return if(jwtToken != null)
            BasicRes(result=true, data=null)
        else
            BasicRes(result=false, data=null)
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) {
        val cookie = Cookie("accessToken", null).apply {
            isHttpOnly = true
            secure = true
            maxAge = 0
            path = "/"
        }

        response.addCookie(cookie)

        BasicRes(result=true, data=null)
    }
}