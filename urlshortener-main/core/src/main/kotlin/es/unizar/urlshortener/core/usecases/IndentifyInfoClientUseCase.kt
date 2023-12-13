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
        fun returnInfoShortUrl(id: String): Map<String, Int>
}


class IdentifyInfoClientUseCaseImpl(
    private val clickRepository: ClickRepositoryService
) : IdentifyInfoClientUseCase {

    override fun returnInfoShortUrl(id: String): Map<String, Int> {
        val clicks = clickRepository.findByUrlHash(id)
        return clicks
            .groupBy { "${it.properties.platform} - ${it.properties.browser}" }
            .mapValues { (_, value) -> value.size }
    }
}