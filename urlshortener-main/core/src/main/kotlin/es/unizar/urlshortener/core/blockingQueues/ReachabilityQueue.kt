package es.unizar.urlshortener.core.blockingQueues

import es.unizar.urlshortener.core.usecases.IsReachableUseCase
import es.unizar.urlshortener.core.usecases.QRUseCase
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.BlockingQueue

/**
 * Concurrent process that generates the QR Code of an Url following a queue order.
 *
 * To manage the concurrence, it's being using a [BlockingQueue].
 */
@Component
open class ReachabilityQueue(
        private val reachabilityQueue: BlockingQueue<Pair<String, String>>,
        private val isReachableUseCase: IsReachableUseCase
) {
    @Async("ReachableQueue")
    @Scheduled(fixedDelay = 500L)
    open
    fun executor() {
        if (reachabilityQueue.isNotEmpty()) {
            println("(Cola alcanzabilidad) Executorrrr")
            val result = reachabilityQueue.take()
            println(result)
            isReachableUseCase.isReachable(result.first, result.second)
        }
    }
}
