plugins {
    kotlin("jvm") version "1.9.21"
    // Spring Boot 플러그인 (버전 명시 필요)
    id("org.springframework.boot") version "3.2.0" // 최신 안정 버전 확인 후 사용
    // 의존성 관리 플러그인
    id("io.spring.dependency-management") version "1.1.3" // Spring Boot 버전에 맞는 버전 확인 후 사용
    // Kotlin Spring 플러그인 (Kotlin 버전과 동일하게 설정)
    kotlin("plugin.spring") version "1.9.21"
    // ✨ QueryDSL의 Q-Class 생성을 위한 kapt 플러그인 추가
    kotlin("kapt") version "1.9.21"
    // ✨ JPA 엔티티를 열어주기 위한 jpa 플러그인 추가 (open 키워드 자동 처리)
    kotlin("plugin.jpa") version "1.9.21"
}

group = "com.overtheinfinite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kotlin에서 리플렉션을 사용하기 위해 필요 (Spring Boot와 Kotlin 사용 시 권장)
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // JJWT Core
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    // JJWT Implementation (runtime 시 필요)
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    // JJWT Jackson Serializer (JSON 처리)
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Boot 3.x (Jakarta) 환경을 유지하며, 버전을 5.0.0보다 높은 버전으로 올립니다.
    val querydslVersion = "5.6.1" // 혹은 현재 시점의 최신 안정 버전
// ✅ 수정된 설정: 그룹 ID를 io.github.openfeign.querydsl로 변경
    kapt("io.github.openfeign.querydsl:querydsl-apt:$querydslVersion:jakarta")
    implementation("io.github.openfeign.querydsl:querydsl-jpa:$querydslVersion:jakarta")

    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.security:spring-security-crypto")
    // 🔗 JPA Annotation Processor (kapt를 통해 처리)
    // Java/Kotlin 엔티티를 인식하는 데 필요합니다.
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("jakarta.annotation:jakarta.annotation-api")

    // Kotlin 표준 라이브러리는 kotlin("jvm") 플러그인에 의해 자동으로 추가될 수 있지만, 명시적으로 추가할 수도 있습니다.
    // implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // 테스트 의존성 (Spring Boot Test와 통합하여 사용)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

// ✨ Q-Class를 생성할 디렉토리 설정
val generated = file("src/main/generated")

// Q-Class가 생성될 위치를 지정하고, 컴파일 시 해당 파일을 소스로 포함하도록 설정
sourceSets {
    getByName("main") {
        java {
            setSrcDirs(listOf(file("src/main/java"), generated))
        }
    }
}

// ✨ compileKotlin 태스크 실행 전에 Q-Class 생성 디렉토리를 정리
tasks.named("clean") {
    doLast {
        generated.deleteRecursively()
    }
}

// ✨ QueryDSL Q-Class 생성 위치를 Gradle이 알 수 있도록 설정
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        // ... (다른 옵션들)
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}