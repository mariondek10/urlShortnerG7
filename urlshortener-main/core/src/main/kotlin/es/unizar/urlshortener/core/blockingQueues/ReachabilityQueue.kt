package es.unizar.urlshortener.core.blockingQueues

import es.unizar.urlshortener.core.usecases.IsReachableUseCase
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.BlockingQueue

/**
 * Concurrent process
 *
 * To manage the concurrence, it's being using a [BlockingQueue].
 */
@Component
open class ReachabilityQueue(
    @Qualifier("reachabilityQueue") private val reachableQueue: BlockingQueue<Pair<String, String>>,
    private val isReachableUseCase: IsReachableUseCase
) {
    @Async("configQueue")
    @Scheduled(fixedDelay = 500L)
    open
    fun executor() {
        if (reachableQueue.isNotEmpty()) {
            println("(Cola alcanzabilidad) Executorrrr")
            val result = reachableQueue.take()
            println(result)
            isReachableUseCase.isReachable(result.first, result.second)
        }
    }
}
