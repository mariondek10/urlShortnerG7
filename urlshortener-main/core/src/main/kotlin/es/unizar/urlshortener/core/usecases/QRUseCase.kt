@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import org.springframework.core.io.ByteArrayResource

import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import java.io.ByteArrayOutputStream
import io.github.g0dkar.qrcode.QRCode
import qrcode.QRCode


/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface QRUseCase {
    fun getQRUseCase(id: String): ByteArrayResource
}

/**
 * Implementation of [QRUseCase].
 */
class QRUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val qrMap: HashMap<String, ByteArrayResource>
) : QRUseCase {
    override fun getQRUseCase(id: String): ByteArrayResource =
    //Code based on: https://github.com/g0dkar/qrcode-kotlin#spring-framework-andor-spring-boot
    shortUrlRepository.findByKey(id)?.let { shortUrl ->
        if (shortUrl.properties.qr_bool == true) {
            qrMap.computeIfAbsent(id) {
                val pngData = QRCode.ofSquares().build(id).render()
                val  p = QRCode(id).render().writeImage(it)
                //val qr = ByteArrayResource(pngData, IMAGE_PNG_VALUE)
                //qrMap.put(id, qr)
                
                ByteArrayResource(, IMAGE_PNG_VALUE)


                }
        } else {
            throw InvalidUrlException("QR")
        }
    } ?: throw RedirectionNotFound(id)

}

/*
override fun getQRUseCase(id: String): ByteArray =
        shortUrlRepository.findByKey(id)?.let {
            
            if (it.properties.qr_bool == true) {
                qrMap.get(id)
            } else {
                throw InvalidUrlException("QR")
            }
        } ?: throw RedirectionNotFound(id)
 */