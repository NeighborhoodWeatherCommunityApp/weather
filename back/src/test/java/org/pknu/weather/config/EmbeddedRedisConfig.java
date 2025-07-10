package org.pknu.weather.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
public class EmbeddedRedisConfig {

    private final RedisServer redisServer;

    public EmbeddedRedisConfig() throws IOException {
        this.redisServer = new RedisServer(63790);
    }

    @PostConstruct
    public void start() throws IOException {
        this.redisServer.start();
    }

    @PreDestroy
    public void stop() throws IOException {
        this.redisServer.stop();
    }
}
