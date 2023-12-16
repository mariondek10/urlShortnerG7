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

    /**
     * Creates a short URL based on the provided long URL and optional data.
     *
     * @param url The long URL for which a short URL needs to be created.
     * @param data Optional data that can be added during URL creation.
     * @return The generated ShortUrl object representing the shortened URL.
     * @throws InvalidUrlException If the provided URL is invalid.
     * @throws UrlToShortNotReachable If the URL to be shortened is not reachable.
     *
     */
    override fun create(url: String, data: ShortUrlProperties): ShortUrl = when {
            !validatorService.isValid(url) -> throw InvalidUrlException(url)
            !isReachableUseCase.isReachable(url) -> throw UrlToShortNotReachable(url)
            !validatorService.withoutSlash(data.alias) -> throw InvalidUrlException(url)
            else -> {
                shortUrlRepository.findByKey(hashService.hasUrl(url))?.let { shortUrl ->
                    if(shortUrl.properties.qrBool == true){
                        throw KeyAlreadyExists(hashService.hasUrl(url))
                    }
                    if (shortUrl.properties.qrBool == false && data.qrBool == true) {
                        //no esta el qr(false) y se requiere (true)
                        val hash : String = if(data.alias != "" ){
                            System.out.println("AÑADIDO ALAISSSS" + data)
                            data.alias
                        } else{
                            hashService.hasUrl(url)
                        }
                        val su = ShortUrl(
                            hash = hash,
                            redirection = Redirection(target = url),
                            properties = ShortUrlProperties(
                                ip = data.ip,
                                sponsor = data.sponsor,
                                safe = shortUrl.properties.safe,
                                qrBool = data.qrBool
                            )
                        )
                        /*System.out.println("BORRANDOOOOOO")
                        //borramos la que habia
                        if (shortUrlRepository.delete(shortUrl)){
                            System.out.println("es trueeeeee")
                            //guardamos la nueva

                        }else{
                            su
                        }*/
                        if(shortUrlRepository.findByKey(hash) == null){
                            shortUrlRepository.save(su)
                        } else{
                            throw KeyAlreadyExists(hash)
                        }

                    } else {
                        shortUrl
                    }
                }?: run{
                    //System.out.println("(CreateShortUrlUseCase) data: ShortUrlProperties:" + data)
                    val hash : String = if(data.alias != "" ){
                        System.out.println("AÑADIDO ALIASSSS" + data.alias)
                        data.alias
                    }else{
                        System.out.println("AÑADIDO HASHHHHHH")
                        hashService.hasUrl(url)
                    }
                    val su = ShortUrl(
                            hash = hash,
                            redirection = Redirection(target = url),
                            properties = ShortUrlProperties(
                                    ip = data.ip,
                                    sponsor = data.sponsor,
                                    safe = data.safe,
                                    qrBool = data.qrBool
                            )
                    )
                    //System.out.println("(CreateShortUrlUseCase) antes de save su: ShortUrl:" + su)
                    if(shortUrlRepository.findByKey(hash) == null){
                        shortUrlRepository.save(su)
                    } else{
                        throw KeyAlreadyExists(hash)
                    }
                }

            }

        }

}
