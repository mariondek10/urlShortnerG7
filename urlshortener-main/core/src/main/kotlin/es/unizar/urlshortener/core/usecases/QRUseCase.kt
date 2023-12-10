//@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.io.ByteArrayOutputStream
import io.github.g0dkar.qrcode.QRCode


/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
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
    private val qrMap: HashMap<String, ByteArray>
) : QRUseCase {

    override fun generateQR(id: String, url: String) {
        shortUrlRepository.findByKey(id)?.let {shortUrl ->
            if (shortUrl.properties.qrReady == false) { //No esta generado todavia
                //System.out.println("(QRUSECASE) CREANDO QR it.properties.qrBool:" + it.properties.qrBool)
                val image = ByteArrayOutputStream()
                //System.out.println("(QRUSECASE) IMAGE CREADA")
                var qr = QRCode(url).render(25)
                //System.out.println("(QRUSECASE) QRCode(url).render(10)")
                qr.writeImage(image)
                //System.out.println("(QRUSECASE) qr.writeImage(image)")
                val byteArray = image.toByteArray()
                qrMap.put(id, byteArray)
                shortUrl.properties.qrReady = true
            }
        } ?: throw RedirectionNotFound(id)
    }

    override fun getQRUseCase(id: String): ByteArray =
            //Code based on: https://github.com/g0dkar/qrcode-kotlin#spring-framework-andor-spring-boot
            shortUrlRepository.findByKey(id)?.let { shortUrl ->
                //System.out.println("(QRUSECASE) shortUrl:" + shortUrl)
                if (shortUrl.properties.qrReady == true) {
                    //System.out.println("(QRUSECASE) qrBool es true y el id es:" + id)
                    qrMap.get(id)
                } else {
                    throw QRNotAvailable(id)
                }
            } ?: throw RedirectionNotFound(id)

    /*



    when {
            !validatorService.isValid(url) -> throw InvalidUrlException(url)
            !isReachableUseCase.isReachable(url) -> throw UrlToShortNotReachable(url)
            else -> {



    override fun getQRUseCase(id: String): ByteArray =
    //Code based on: https://github.com/g0dkar/qrcode-kotlin#spring-framework-andor-spring-boot
    shortUrlRepository.findByKey(id)?.let { shortUrl ->
        System.out.println("(QRUSECASE) shortUrl:" + shortUrl)
        if (shortUrl.properties.qrBool == true) {
            qrMap.computeIfAbsent(id) {
                System.out.println("(QRUSECASE) computeIfAbsent: NO LO HA ENCONTRADO")
                val image = ByteArrayOutputStream()
                QRCode(id).render().writeImage(image)

                val byteArray = image.toByteArray()
                requireNotNull(byteArray) { "Byte array is null" }
                qrMap.put(id, byteArray)
                byteArray

                //qrMap.put(id, image.toByteArray())

            }
        } else {
            throw InvalidUrlException("QR")
        }
    } ?: throw RedirectionNotFound(id)*/
}
