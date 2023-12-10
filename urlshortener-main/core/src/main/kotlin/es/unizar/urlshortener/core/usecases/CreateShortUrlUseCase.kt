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
        private val hashService: HashService,
        private var idIncremental: Int = 1
) : CreateShortUrlUseCase {
    override fun create(url: String, data: ShortUrlProperties): ShortUrl = when {
            !validatorService.isValid(url) -> throw InvalidUrlException(url)
            !isReachableUseCase.isReachable(url) -> throw UrlToShortNotReachable(url)
            else -> {
                shortUrlRepository.findByKey(hashService.hasUrl(url))?.let { shortUrl ->
                    if (shortUrl.properties.qrBool == false && data.qrBool == true) {
                        //no esta el qr (false) y se requiere (true)



                        // SALVARLO CAPTURANDO ERROR SI HAY DUPLICATED KEY, SI ES CON ALIAS NO NULLO

                        /*
                        *  tenemos que hacer un ID autoincrementable además del hash/alias para que si el usuario pone 2 veces lo mismo no se hace un update
                        * */
                        // POST /api/link
                        // Crear una URL acortada que no existe previamente



                        // RESOLVER LA ID, SI HASH O ALIAS
                        val id: String = data.alias ?: hashService.hasUrl(url)
                        // CREAR SHORTURL
                        val su = ShortUrl(
                            hash = id,
                            redirection = Redirection(target = url),
                            properties = ShortUrlProperties(
                                safe = shortUrl.properties.safe,
                                ip = data.ip,
                                sponsor = data.sponsor,
                                qrBool = data.qrBool,
                                qrReady = data.qrReady,

                            ),
                            idColision = idIncremental
                        )
                        idIncremental += 1
                        //HABRIA QUE MODIFICAR shortUrl, COMO?
                        shortUrlRepository.save(su)// duplicamos,

                    } else {
                        shortUrl// lo que ya estaba en la base de datos
                    }
                }?: run{// No existe la URL
                    System.out.println("(CreateShortUrlUseCase) data: ShortUrlProperties:" + data)
                    val id: String = data.alias ?: hashService.hasUrl(url)
                    val su = ShortUrl(
                            hash = id,
                            redirection = Redirection(target = url),
                            properties = ShortUrlProperties(
                                    safe = data.safe,
                                    ip = data.ip,
                                    sponsor = data.sponsor,
                                    qrBool = data.qrBool,
                                    qrReady = data.qrReady
                            ),
                            idColision = idIncremental
                    )
                    idIncremental += 1
                    System.out.println("(CreateShortUrlUseCase) antes de save su: ShortUrl:" + su)
                    shortUrlRepository.save(su) // CREO LA URL QUE NO EXISTIA EN LA BASE DE DATOS
                }

            }

        }


}
