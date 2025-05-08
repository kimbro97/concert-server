package kr.hhplus.be.server.support.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	// @Bean("scheduleCacheManager")
	// public RedisCacheManager scheduleCacheManager(RedisConnectionFactory factory) {
	// 	RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
	// 		.entryTtl(Duration.ofMinutes(2));
	//
	// 	return RedisCacheManager.builder(factory)
	// 		.cacheDefaults(config)
	// 		.build();
	// }

	@Bean
	public CacheManager scheduleCacheManager(RedisConnectionFactory redisConnectionFactory) {
		CompositeCacheManager cacheManager = new CompositeCacheManager(
			caffeineCacheManager(), redisCacheManager(redisConnectionFactory)
		);
		cacheManager.setFallbackToNoOpCache(false); // 캐시 못 찾으면 예외 대신 무시
		return cacheManager;
	}

	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(2))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
				new GenericJackson2JsonRedisSerializer()
			));
		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(config)
			.build();
	}

	public CacheManager caffeineCacheManager() {
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofSeconds(30))
			.maximumSize(1000));
		return caffeineCacheManager;
	}
}
