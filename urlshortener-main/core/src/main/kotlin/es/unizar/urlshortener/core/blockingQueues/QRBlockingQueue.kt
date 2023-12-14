package es.unizar.urlshortener.core.blockingQueues

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
open class QRBlockingQueue(
        private val qrQueue: BlockingQueue<Pair<String, String>>,
        private val qrUseCase: QRUseCase
) {
    @Async("configQueue")
    @Scheduled(fixedDelay = 500L)
    open
    fun executor() {
        if (!qrQueue.isEmpty()) {
            System.out.println("(QRBlockingQueue) Executorrrr")
            val result = qrQueue.take()
            qrUseCase.generateQR(result.first, result.second)
        }
    }
}
