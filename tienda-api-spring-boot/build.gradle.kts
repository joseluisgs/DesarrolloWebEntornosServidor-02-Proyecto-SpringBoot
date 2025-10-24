plugins {
    java // Plugin de Java
    id("org.springframework.boot") version "3.5.6" // Plugin de Spring Boot
    id("io.spring.dependency-management") version "1.1.7" // Plugin de gestión de dependencias de Spring
    id("jacoco") // Plugin de Jacoco para test de cobertura
}

group = "dev.joseluisgs"
version = "0.0.1-SNAPSHOT"

java {
    // versión de Java
    sourceCompatibility = JavaVersion.VERSION_25 // Sintaxis de Java 25
    targetCompatibility = JavaVersion.VERSION_25 // Versión de Java 25 para ser compilado y ejecutado
    
    toolchain {
        languageVersion = JavaLanguageVersion.of(25) // Versión de Java 25 para el toolchain
    }
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    // Dependencias de Spring Web for HTML Apps y Rest
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Spring Data JPA par SQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // Spring Data JPA para MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    
    // Validación
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    
    // Thyemeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    
    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    // GraphQL con Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    
    // Cache con redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // Mail sender
    implementation("org.springframework.boot:spring-boot-starter-mail")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // H2 Database - Para desarrollo
    implementation("com.h2database:h2")
    // PostgreSQL - Para producción
    implementation("org.postgresql:postgresql")
    
    // Para usar con jackson el controlador las fechas: LocalDate, LocalDateTime, etc
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Para pasar a XML los responses, negocacion de contenido
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    
    // Para manejar los JWT tokens
    implementation("com.auth0:java-jwt:4.4.0")
    
    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    // Dependencias para Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    
    // TestContainers para tests de integración - AÑADIDO CORRECTAMENTE
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:testcontainers")
    
    // Extras para web
    implementation("org.webjars:bootstrap:4.6.2")
}

// Configuración mejorada de Jacoco - AÑADIDA CORRECTAMENTE
jacoco {
    toolVersion = "0.8.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.test {
    systemProperty("spring.profiles.active", project.findProperty("spring.profiles.active") ?: "test")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        csv.required = true
        html.required = true
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
    
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/*Application*")
                exclude("**/config/**")
                exclude("**/dto/**")
                exclude("**/models/**")
            }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "BRANCH" 
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
