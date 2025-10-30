package dev.joseluisgs.tiendaapispringboot;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Clase base abstracta para tests de integración con TestContainers.
 * 
 * Proporciona configuración completa para la arquitectura híbrida:
 * - PostgreSQL: Productos, Categorías, Usuarios (JPA)
 * - MongoDB: Pedidos (MongoRepository)
 * - Redis: Cache y sesiones
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("tienda_test")
            .withUsername("test_user")
            .withPassword("test_pass")
            .withReuse(true);

    @Container
    static MongoDBContainer mongoDb = new MongoDBContainer("mongo:5.0")
            .withReuse(true);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL para JPA
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // MongoDB para Pedidos
        registry.add("spring.data.mongodb.uri", mongoDb::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "tienda_test");

        // Redis para cache
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);

        // Configuración adicional para tests
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @BeforeAll
    static void setUp() {
        System.out.println("🐳 TestContainers iniciados:");
        System.out.println("📊 PostgreSQL: " + postgres.getJdbcUrl());
        System.out.println("🍃 MongoDB: " + mongoDb.getConnectionString());
        System.out.println("🔴 Redis: " + redis.getHost() + ":" + redis.getFirstMappedPort());
    }
}