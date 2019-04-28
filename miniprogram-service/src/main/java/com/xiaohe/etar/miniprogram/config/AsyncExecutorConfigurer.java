package com.xiaohe.etar.miniprogram.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Springboot 线程池配置
 *
 * @author hzh
 * @date 2018/8/9
 */
@Configuration
public class AsyncExecutorConfigurer implements AsyncConfigurer {

    @Override
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        threadPool.setCorePoolSize(2);
        // 设置最大线程数
        threadPool.setMaxPoolSize(8);
        // 线程池所使用的缓冲队列
        threadPool.setQueueCapacity(1);
        // 等待任务在shutdown时完成--表明等待所有线程执行完
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间 （默认为0，此时立即不接收请求；如设置30s：代表在30s之后不再接收请求）
        threadPool.setAwaitTerminationSeconds(0);
        // 线程名称前缀
        threadPool.setThreadNamePrefix("MyAsync-");
        // 初始化线程
        threadPool.initialize();
        return threadPool;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

}
