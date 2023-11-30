@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*

/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface CreateShortUrlUseCase {
    fun create(url: String, data: ShortUrlProperties): ShortUrl
}

/**
 * Implementation of [CreateShortUrlUseCase].
 */
class CreateShortUrlUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val isReachableUseCase: IsReachableUseCase,
    private val validatorService: ValidatorService,
    private val hashService: HashService
) : CreateShortUrlUseCase {
    override fun create(url: String, data: ShortUrlProperties): ShortUrl = when {
            !validatorService.isValid(url) -> throw InvalidUrlException(url)
            !isReachableUseCase.isReachable(url) -> throw UrlToShortNotReachable(url)
            else -> {

                // RESOLVER LA ID, SI HASH O ALIAS
                // CREAR SHORTURL
                // SALVARLO CAPTURANDO ERROR SI HAY DUPLICATED KEY, SI ES CON ALIAS NO NULLO

                // POST /api/link
                // Crear una URL acortada que no existe previamente
                shortUrlRepository.findByKey(hashService.hasUrl(url))
                    ?.let { shortUrl -> // Existe una URL acortada asociada a la URL original
                    if (shortUrl.properties.qrBool == false && data.qrBool == true) { //esta el qr(false) y se requiere (true)
                        //no esta el qr(false) y se requiere (true)
                        val id: String = data.alias ?: hashService.hasUrl(url)
                        val su = ShortUrl(
                            hash = id,
                            redirection = Redirection(target = url),
                            properties = ShortUrlProperties(
                                safe = shortUrl.properties.safe,
                                ip = data.ip,
                                sponsor = data.sponsor,
                                qrBool = data.qrBool,
                            )
                        )
                        shortUrlRepository.save(su)// duplicamos
                    } else {
                        shortUrl// lo que ya estaba en la base de datos
                    }
                }?: run{// No existe la URL
                    if (validatorService.isValid(url)) { // PERO SI YA LO HE COMPROBADO, LUEGO ES TRUE
                        System.out.println("(CreateShortUrlUseCase) data: ShortUrlProperties:" + data)
                        val id: String = data.alias ?: hashService.hasUrl(url)
                        val su = ShortUrl(
                            hash = id,
                            redirection = Redirection(target = url),
                            properties = ShortUrlProperties(
                                safe = data.safe,
                                ip = data.ip,
                                sponsor = data.sponsor,
                                qrBool = data.qrBool
                            )
                        )
                        System.out.println("(CreateShortUrlUseCase) antes de save su: ShortUrl:" + su)
                        shortUrlRepository.save(su)// CREO LA URL QUE NO EXISTIA EN LA BASE DE DATOS
                    } else {
                        throw InvalidUrlException(url)
                    }
                }

            }

        }


}
