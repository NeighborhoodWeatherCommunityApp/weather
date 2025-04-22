package org.pknu.weather.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    private static final String CACHE_NAME = "duplicateRequestCache";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();

        List<Cache> caches = List.of(
                new CaffeineCache("duplicateRequestCache",
                        Caffeine.newBuilder()
                                .expireAfterWrite(3, TimeUnit.SECONDS)
                                .maximumSize(1000)
                                .build()),

                new CaffeineCache("locationCreateStore",
                        Caffeine.newBuilder()
                                .expireAfterWrite(10, TimeUnit.MINUTES)
                                .maximumSize(500)
                                .build()),

                new CaffeineCache("locationUpdateStore",
                        Caffeine.newBuilder()
                                .expireAfterWrite(30, TimeUnit.MINUTES)
                                .maximumSize(500)
                                .build())
        );

        manager.setCaches(caches);
        return manager;
    }
}
