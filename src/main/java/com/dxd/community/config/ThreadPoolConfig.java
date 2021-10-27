package com.dxd.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author dxd
 * @create 2021-07-17 21:36
 */
@Configuration
@EnableScheduling
@EnableAsync
//spring线程池的配置类
public class ThreadPoolConfig {
}
