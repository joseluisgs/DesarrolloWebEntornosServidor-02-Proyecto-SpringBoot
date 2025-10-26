import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("java")
    id("jacoco")
}

group = "dev.joseluisgs"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

// Extra properties for dependency versions
val testcontainersVersion = "1.19.3"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    
    // Thymeleaf extras
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    
    // Database Drivers
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    
    // JWT - using Auth0 JWT
    implementation("com.auth0:java-jwt:4.4.0")
    
    // Swagger/OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // WebSocket
    implementation("org.springframework:spring-websocket")
    implementation("org.springframework:spring-messaging")
    
    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.graphql:spring-graphql-test")
    
    // TestContainers
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.testcontainers:mongodb:$testcontainersVersion")
    
    // MongoDB Test
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.11.0")
    
    // Mockito
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
    
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
    
    // Separate integration tests
    if (project.hasProperty("integrationTest")) {
        filter {
            includeTestsMatching("*IntegrationTest")
            includeTestsMatching("*IT")
        }
    } else {
        filter {
            excludeTestsMatching("*IntegrationTest")
            excludeTestsMatching("*IT")
        }
    }
}

// Create separate task for integration tests
val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests with TestContainers"
    group = "verification"
    
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    
    useJUnitPlatform {
        includeTags("integration")
    }
    
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
    
    shouldRunAfter(tasks.test)
}

// Jacoco configuration
jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
    }
    
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/config/**",
                    "**/dto/**",
                    "**/exceptions/**",
                    "**/TiendaApiSpringBootApplication**"
                )
            }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    
    violationRules {
        rule {
            limit {
                minimum = "0.75".toBigDecimal()
                counter = "LINE"
            }
        }
        rule {
            limit {
                minimum = "0.70".toBigDecimal()
                counter = "BRANCH"
            }
        }
    }
    
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/config/**",
                    "**/dto/**",
                    "**/exceptions/**",
                    "**/TiendaApiSpringBootApplication**"
                )
            }
        })
    )
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
