@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases


import es.unizar.urlshortener.core.*

/**
 *
 * Given an id representing a Short Url, returns accumulated User-Agent info.
 *
 *
 */
interface IdentifyInfoClientUseCase {
        fun returnInfoShortUrl(id: String): List<InfoClient>
}

data class InfoClient (
    val ip: String? = null,
    val browser: String? = null,
    val platform: String? = null,
)

class IdentifyInfoClientUseCaseImpl(
    private val clickRepository: ClickRepositoryService
) : IdentifyInfoClientUseCase {

    override fun returnInfoShortUrl(id: String): List<InfoClient> {
       val clicks = clickRepository.findByUrlHash(id)
        return clicks.map{
            InfoClient(it.properties.ip, it.properties.browser, it.properties.platform)
        }
    }
}