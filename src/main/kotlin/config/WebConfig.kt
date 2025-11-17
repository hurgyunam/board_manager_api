package com.overtheinfinite.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 모든 경로에 대해 설정 적용
            .allowedOrigins("http://localhost:3000") // 허용할 출처 지정
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 지정
            .allowCredentials(true) // 자격 증명(쿠키 등) 허용
            .maxAge(3600) // pre-flight 요청 캐시 시간 (1시간)
    }
}