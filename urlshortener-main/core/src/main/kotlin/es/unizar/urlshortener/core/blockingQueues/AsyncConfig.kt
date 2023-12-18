package es.unizar.urlshortener.core.blockingQueues

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

/**
 * Configuration used by concurrent processes that has the anotation @Async with the content configQueue
 * */
//Idea de código extraido de: https://medium.com/@databackendtech/spring-boot-java-framework-how-to-use-queue-between-threads-in-a-service-a1cfd6b78713
@Configuration
@EnableAsync
@EnableScheduling
open class AsyncConfig {
    @Bean("configQueue")
    open fun executor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.initialize()
        return executor
    }
}

