package com.huuhung.exam_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;

@Configuration
public class RedisConfig {

    // 🌟 BEAN 1: Cấu hình bộ điều khiển Cache tự động (@Cacheable, @CacheEvict)
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // CÀI ĐẶT TTL: Dữ liệu trên RAM tự động bốc hơi sau 24 giờ để làm tươi hệ thống
                .entryTtl(Duration.ofDays(1)) 
                // Không lưu giá trị null vào cache để tránh lỗi Tấn công Cache lừa (Cache Penetration)
                .disableCachingNullValues() 
                // Mã hóa chữ Tiếng Việt, Tiếng Hàn chuẩn JSON khi dùng Annotation Cache
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    // 🌟 BEAN 2: Cấu hình Template để tự gọi code thao tác thủ công với Redis (nếu cần dùng sau này)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Cấu hình lưu Key dưới dạng String sạch
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Cấu hình lưu Value dưới dạng JSON thông thường
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        return template;
    }
}