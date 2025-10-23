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
    // Lo podemos usar en el test o en el controlador, si hiciese falta, por eso está aquí
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Para pasar a XML los responses, negocacion de contenido
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    
    // Para manejar los JWT tokens
    // JWT (Json Web Token)
    implementation("com.auth0:java-jwt:4.4.0")
    
    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    
    // Dependencias para Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // Test Spring Security
    testImplementation("org.springframework.security:spring-security-test")
    
    // MongoDB para test, pero no es necesario, usamos sus repositorios
    // testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring31x:4.9.3")
    
    // Extras para web
    // Bootstrap
    implementation("org.webjars:bootstrap:4.6.2")
    
}

tasks.withType<Test> {
    useJUnitPlatform() // Usamos JUnit 5
    // finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.test {
    // Ponemos el perfil de test para que cargue el application-test.properties
    // para ahorranos hacer esto
    //./gradlew test -Pspring.profiles.active=dev
    systemProperty("spring.profiles.active", project.findProperty("spring.profiles.active") ?: "dev")
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
    reports {
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}
