@file:Suppress("WildcardImport")
package es.unizar.urlshortener.core.usecases
import es.unizar.urlshortener.core.*

/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface UrlPersoCase {
    fun create(url: String, data: UrlPersoproperties): UrlPerso
}

/**
 * Implementation of [UrlPersoCaseImpl].
 */
class UrlPersoCaseImpl(
    private val urlPersoRepository: UrlPersoRepositoryService,
    private val validatorService: ValidatorService,
    private val hashService: HashService
) : UrlPersoCase {
    override fun create(url: String, data: UrlPersoproperties): UrlPerso =
        if (validatorService.isValid(url)) {
            val id: String = hashService.hasUrl(url)
            val up = UrlPerso(
                alias = id,
                redirection = Redirection(target = url),
                properties = UrlPersoproperties(
                    safe = data.safe,
                    ip = data.ip,
                    sponsor = data.sponsor
                )
            )
            urlPersoRepository.save(up)
        } else {
            throw InvalidUrlException(url)
        }
}
