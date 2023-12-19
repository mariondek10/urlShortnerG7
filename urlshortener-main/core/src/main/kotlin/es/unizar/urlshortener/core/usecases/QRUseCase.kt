@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.io.ByteArrayOutputStream
import io.github.g0dkar.qrcode.QRCode



/**
 * Given a URI saves a QR code that links to the resource passed.
 */
interface QRUseCase {


    fun generateQR(id: String, url: String)


    fun getQRUseCase(id: String): ByteArray
}

/**
 * Implementation of [QRUseCase].
 */
class QRUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val qrMap: HashMap<String, ByteArray>,
    private val renderSize : Int = 25

) : QRUseCase {

    /**
     * @brief Generate the QR for the shortened URL, if it is required
     * @param id The shortened URL's string
     * @param url The shortened URL's String
     * @throws RedirectionNotFound Represents a redirection not found exception for an unknown key.
     */
    override fun generateQR(id: String, url: String) {
        shortUrlRepository.findByKey(id)?.let {
            if (it.properties.qrBool == true) {
                val image = ByteArrayOutputStream()
                var qr = QRCode(url).render(renderSize)
                qr.writeImage(image)
                val byteArray = image.toByteArray()
                qrMap.put(id, byteArray)
            }
        } ?: throw RedirectionNotFound(id)
    }

    /**
     * @brief Get the QR for the shortened URL, if it exists
     * @param id The shortened URL's string
     * @throws RedirectionNotFound Represents a redirection not found exception for an unknown key.
     * @throws QRNotAvailable Represents an exception for QR not available for a given key.
     * @return The QR code data in ByteArray.
     */
    override fun getQRUseCase(id: String): ByteArray =
            //Code based on: https://github.com/g0dkar/qrcode-kotlin#spring-framework-andor-spring-boot
            shortUrlRepository.findByKey(id)?.let { shortUrl ->
                if (shortUrl.properties.qrBool == true) {
                    qrMap.get(id)
                } else {
                    throw QRNotAvailable(id)
                }
            } ?: throw RedirectionNotFound(id)
}
