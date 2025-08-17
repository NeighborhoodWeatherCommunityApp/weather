package org.pknu.weather.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

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


    /**
     * RedisConnectionFactory는 스프링 어플리케이션과 레디스를 연결하기 위해 사용된다.
     * 커넥션의 종류로는 Jedis와 Lettuce가 있는데, Lettuce의 성능이 더 좋은 것으로 알려져있다.
     */
//    @Bean
//    @ConditionalOnMissingBean(RedisConnectionFactory.class)
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
//        redisConfig.setPassword(RedisPassword.of(password));
//
////        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
////                .useSsl() // TLS 연결
////                .and()
////                .build();
//
//        // 커넥션 풀 설정 (중요!)
//        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
//        poolConfig.setMaxTotal(50);        // 최대 연결 수
//        poolConfig.setMaxIdle(20);         // 최대 유휴 연결
//        poolConfig.setMinIdle(2);          // 최소 유휴 연결
//        poolConfig.setTestOnBorrow(true);  // 연결 유효성 검사
//
//        LettucePoolingClientConfiguration poolingConfig = LettucePoolingClientConfiguration.builder()
//                .poolConfig(poolConfig)
//                .commandTimeout(Duration.ofSeconds(2))
//                .shutdownTimeout(Duration.ofMillis(100))
//                .useSsl()
//                .and()
//                .build();
//
//        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfig, clientConfig);
//        connectionFactory.setShareNativeConnection(true);
//
//        // redis 연결 정보를 토대로 LettuceConnectionFactory 객체를 생성하여 빈으로 등록한다.
//        return new LettuceConnectionFactory(
//                new RedisStandaloneConfiguration(host, port), poolingConfig);
//    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public LettuceConnectionFactory redisConnectionFactory() {
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
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        /**
         * 이 설정은 모든 Redis 값에 대해 JSON 형태로 직렬화를 수행하도록 설정합니다.
         * 키에 대해서는 StringRedisSerializer를 사용하여 문자열로 직렬화합니다.
         */
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(getObjectMapper());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

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