package com.anderb.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "threadTaskExecutor")
    public ExecutorService threadPoolTaskExecutor() {
        return Executors.newCachedThreadPool();
    }
}
