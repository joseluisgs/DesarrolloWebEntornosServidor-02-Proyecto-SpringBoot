package dev.joseluisgs.tiendaapispringboot.config.cache;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Configuración de caché por perfiles
 * <p>
 * OPCIONAL: Solo necesaria si quieres TTL específicos por caché
 * Sin esta clase, la configuración de application.properties es suficiente
 * Debes mirar la diferencia entre los perfiles dev y prod
 *
 * @author joseluisgs
 * @since 2025-10-21
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Caché en memoria para desarrollo
     * TTL por defecto (sin expiración)
     * No es necesario porque Spring Boot lo configura automáticamente,
     * pero se pone para dejar claro que es diferente al de producción y que dependence del perfil
     */
    @Bean
    @Profile("dev")
    public CacheManager devCacheManager() {
        // Esto es lo que hemos pesto en CacheConfig en cada servicio
        return new ConcurrentMapCacheManager("categorias", "pedidos", "productos", "users");
    }

    /**
     * Redis para producción con TTL específicos por servicio
     * Esto es opcional, si no se pone, usará el TTL por defecto de application.properties
     * en este caso de producción es 1 hora
     */
    @Bean
    @Profile("prod")
    public RedisCacheManager prodCacheManager(RedisConnectionFactory connectionFactory) {
        // Clave como String y valor como JSON
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // TTL específicos para cada servicio
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                // Categorías: TTL largo (24 horas) - casi nunca cambian
                "categorias", defaultConfig.entryTtl(Duration.ofHours(24)),

                // Usuarios: TTL medio (2 horas) - cambian ocasionalmente
                "users", defaultConfig.entryTtl(Duration.ofHours(2)),

                // Productos: TTL medio (1 hora) - cambian regularmente
                "productos", defaultConfig.entryTtl(Duration.ofHours(1)),

                // Pedidos: TTL corto (30 minutos) - cambian frecuentemente
                "pedidos", defaultConfig.entryTtl(Duration.ofMinutes(30))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig.entryTtl(Duration.ofHours(1)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}