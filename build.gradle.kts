plugins {
    kotlin("jvm") version "1.9.21"
    // Spring Boot 플러그인 (버전 명시 필요)
    id("org.springframework.boot") version "3.2.0" // 최신 안정 버전 확인 후 사용
    // 의존성 관리 플러그인
    id("io.spring.dependency-management") version "1.1.3" // Spring Boot 버전에 맞는 버전 확인 후 사용
    // Kotlin Spring 플러그인 (Kotlin 버전과 동일하게 설정)
    kotlin("plugin.spring") version "1.9.21"
}

group = "com.overtheinfinite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {// Spring Boot Core 및 Web Starter (웹 애플리케이션 구동에 필수)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kotlin에서 리플렉션을 사용하기 위해 필요 (Spring Boot와 Kotlin 사용 시 권장)
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Kotlin 표준 라이브러리는 kotlin("jvm") 플러그인에 의해 자동으로 추가될 수 있지만, 명시적으로 추가할 수도 있습니다.
    // implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // 테스트 의존성 (Spring Boot Test와 통합하여 사용)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}