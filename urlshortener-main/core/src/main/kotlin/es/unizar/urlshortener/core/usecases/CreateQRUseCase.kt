package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.QRException
import org.springframework.core.io.ByteArrayResource

/**
 * It creates a QR Code
 *
 */
interface CreateQRUseCase {
    fun getQRCode(hash: String): ByteArrayResource
}

/**
 * Implementation of [CreateQRUseCase].
 */
class CreateQRUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val qrService: QRService
) : CreateQRUseCase {
    override fun getQRCode(hash: String): ByteArrayResource {
        val shortUrl = shortUrlRepository.findByKey(hash)
        return shortUrl?.let {
            qrService.getQRCode(it.redirection.target)
        } ?: throw QRException(hash)
    }
}
