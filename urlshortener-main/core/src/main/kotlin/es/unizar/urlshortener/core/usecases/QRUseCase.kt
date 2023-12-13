@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.io.ByteArrayOutputStream
import io.github.g0dkar.qrcode.QRCode



/**
 * Given a URI saves a QR code that links to the resource passed.
 */
interface QRUseCase {
    /**
     * @brief Generate the QR for the shortened URL, if it is required
     *
     * @param id The shortened URL's string
     * @param url The shortened URL's String
     */
    fun generateQR(id: String, url: String)

    /**
     * @brief Get the QR for the shortened URL, if it exists
     *
     * @param id The shortened URL's string
     * @return The QR code data in ByteArray.
     */
    fun getQRUseCase(id: String): ByteArray
}

/**
 * Implementation of [QRUseCase].
 */
class QRUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val qrMap: HashMap<String, ByteArray>
) : QRUseCase {

    override fun generateQR(id: String, url: String) {
        shortUrlRepository.findByKey(id)?.let {
            if (it.properties.qrBool == true) {
                //System.out.println("(QRUSECASE) CREANDO QR it.properties.qrBool:" + it.properties.qrBool)
                val image = ByteArrayOutputStream()
                //System.out.println("(QRUSECASE) IMAGE CREADA")
                var qr = QRCode(url).render(25)
                //System.out.println("(QRUSECASE) QRCode(url).render(10)")
                qr.writeImage(image)
                //System.out.println("(QRUSECASE) qr.writeImage(image)")
                val byteArray = image.toByteArray()
                qrMap.put(id, byteArray)
            }
        } ?: throw RedirectionNotFound(id)
    }

    override fun getQRUseCase(id: String): ByteArray =
            //Code based on: https://github.com/g0dkar/qrcode-kotlin#spring-framework-andor-spring-boot
            shortUrlRepository.findByKey(id)?.let { shortUrl ->
                //System.out.println("(QRUSECASE) shortUrl:" + shortUrl)
                if (shortUrl.properties.qrBool == true) {
                    //System.out.println("(QRUSECASE) qrBool es true y el id es:" + id)
                    qrMap.get(id)
                } else {
                    throw QRNotAvailable(id)
                }
            } ?: throw RedirectionNotFound(id)
}
