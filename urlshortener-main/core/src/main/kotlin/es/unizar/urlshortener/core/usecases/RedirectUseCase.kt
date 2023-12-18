@file:Suppress("WildcardImport")
package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*

/**
 * Given a key returns a [Redirection] that contains a [URI target][Redirection.target]
 * and an [HTTP redirection mode][Redirection.mode].
 *
 * **Note**: This is an example of functionality.
 */
interface RedirectUseCase {
    fun redirectTo(key: String): Redirection
}

/**
 * Implementation of [RedirectUseCase].
 */
class RedirectUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService
) : RedirectUseCase {

    /*
        override fun redirectTo(key: String) = shortUrlRepository
        .findByKey(key)
        ?.redirection
        ?: throw RedirectionNotFound(key)

     */

    override fun redirectTo(key: String): Redirection {
       val shortUrl = shortUrlRepository.findByKey(key)
        if (shortUrl != null){
            val code = shortUrlRepository.findReachabilityCodeByKey(key)
            println("Codigo de alcanzabilidad redirectUseCase" + code)
            when (code) {
                0 -> throw UrlRegisteredButNotReachable(key)
                2 -> throw ReachabilityNotChecked(key)
                else -> return shortUrl.redirection
            }
        } else {
            throw RedirectionNotFound(key)
        }
    }
}

