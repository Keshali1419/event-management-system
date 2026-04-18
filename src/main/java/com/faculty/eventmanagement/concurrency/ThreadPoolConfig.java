package com.faculty.eventmanagement.concurrency;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);       // 3 threads always ready
        executor.setMaxPoolSize(10);       // max 10 threads under heavy load
        executor.setQueueCapacity(100);    // queue up to 100 tasks
        executor.setThreadNamePrefix("notification-thread-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "registrationExecutor")
    public Executor registrationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("registration-thread-");
        executor.initialize();
        return executor;
    }
}