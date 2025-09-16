package org.pknu.weather.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;


    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }


    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis Config
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
        redisConfig.setPassword(password);

        // Connection Pool을 사용할 경우 이 설정 사용
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(50);
        genericObjectPoolConfig.setMaxIdle(20);
        genericObjectPoolConfig.setMinIdle(5);

        genericObjectPoolConfig.setTestOnBorrow(false);
        genericObjectPoolConfig.setTestOnReturn(false);
        genericObjectPoolConfig.setTestWhileIdle(true);
        genericObjectPoolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));

        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(genericObjectPoolConfig)
                .commandTimeout(Duration.ofMillis(2000))
                .shutdownTimeout(Duration.ofMillis(300))
                .useSsl()
                .and()
                .build();

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfig, clientConfig);
        connectionFactory.setShareNativeConnection(false);
        return connectionFactory;
    }

    /**
     * 이 설정은 모든 Redis 값에 대해 JSON 형태로 직렬화를 수행하도록 설정합니다.
     * 키에 대해서는 StringRedisSerializer를 사용하여 문자열로 직렬화합니다.
     */
    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        /**
         * 이 설정은 모든 Redis 값에 대해 JSON 형태로 직렬화를 수행하도록 설정합니다.
         * 키에 대해서는 StringRedisSerializer를 사용하여 문자열로 직렬화합니다.
         */
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(getObjectMapper());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    private ObjectMapper getObjectMapper() {
        // 직렬화/역직렬화 설정
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("org.pknu.weather.weather.dto") // 해당 패키지에 포함되어 있으면 역직렬화 자동 매핑 지원
                        .allowIfSubType("org.pknu.weather.domain")
                        .allowIfSubType("java.util")
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        // Jackson에 LocalDateTime지원
        objectMapper.registerModule(new JavaTimeModule());
        // LocalDateTime, Date 등을 직/역직렬화할 때 숫자(ex. 20251212001212)가 아닌, 문자열(2025.12.12T00:12:12)로 쓰겠다는 설정 (true=숫자, false=문자열)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 접근 제한자를 무시합니다.
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        return objectMapper;
    }
}