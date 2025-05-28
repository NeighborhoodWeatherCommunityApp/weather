package org.pknu.weather.config;

import java.util.concurrent.Executor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "WeatherCUDExecutor")
    @ConditionalOnMissingBean(name = "WeatherCUDExecutor")
    public Executor getWeatherAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Weather Executor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "ExpCUDExecutor")
    @ConditionalOnMissingBean(name = "ExpCUDExecutor")
    public Executor getExpAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("Exp Executor-");
        executor.initialize();
        return executor;
    }
}