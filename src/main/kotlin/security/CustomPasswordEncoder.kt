package com.overtheinfinite.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class CustomPasswordEncoder {
    // Spring Security에서 권장하는 BCrypt 인코더
    private val encoder = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String {
        return encoder.encode(rawPassword)
    }

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return encoder.matches(rawPassword, encodedPassword)
    }
}