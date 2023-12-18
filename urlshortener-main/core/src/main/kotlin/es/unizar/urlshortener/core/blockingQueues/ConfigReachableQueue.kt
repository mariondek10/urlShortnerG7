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

private const val POOL_SIZE = 10
private const val CORE_POOL_SIZE = 5
private const val QUEUE_CAPACITY = 100
@Configuration
@EnableAsync
@EnableScheduling
open class ConfigReachableQueue {
    @Bean("ReachableQueue")
    open fun executor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.maxPoolSize = POOL_SIZE // Set the maximum pool size
        executor.corePoolSize = CORE_POOL_SIZE // Set the core pool size
        executor.setQueueCapacity(QUEUE_CAPACITY)
        executor.initialize()
        return executor
    }
}