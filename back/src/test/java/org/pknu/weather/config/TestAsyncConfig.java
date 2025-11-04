package org.pknu.weather.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@TestConfiguration
@EnableAsync
public class TestAsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return new SyncTaskExecutor(); // 동기 실행
    }
}
