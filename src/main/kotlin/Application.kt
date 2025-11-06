package com.overtheinfinite

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * Spring Boot 애플리케이션의 메인 클래스.
 * @SpringBootApplication 어노테이션은 다음을 포함합니다:
 * 1. @Configuration: 이 클래스를 설정 클래스로 지정.
 * 2. @EnableAutoConfiguration: Spring Boot 자동 설정을 활성화.
 * 3. @ComponentScan: 현재 패키지부터 하위 패키지의 컴포넌트를 스캔하여 Bean으로 등록.
 */
@SpringBootApplication
@EnableJpaAuditing // ✨ JPA Auditing 활성화
class Application

fun main(args: Array<String>) {
    // SpringApplication을 실행하여 애플리케이션을 시작합니다.
    runApplication<Application>(*args)
}