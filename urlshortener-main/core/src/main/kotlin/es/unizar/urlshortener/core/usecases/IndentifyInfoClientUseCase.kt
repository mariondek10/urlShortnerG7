@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases


import es.unizar.urlshortener.core.*

/**
 *
 * Given an id representing a Short Url, returns accumulated User-Agent info.
 */

interface IdentifyInfoClientUseCase {
        /**
         * Retrieves accumulated User-Agent info for a Short Url.
         * @param id Short Url hash
         * @return Map containing User-Agent info grouped by platform and browser with their respective counts
         */
        fun returnInfoShortUrl(id: String): Map<String, Int>
}

/**
 * Implementation of IdentifyInfoClientUseCase interface.
 * @param clickRepository Service to interact with Click data
 */
class IdentifyInfoClientUseCaseImpl(
    private val clickRepository: ClickRepositoryService
) : IdentifyInfoClientUseCase {

    /**
     * Retrieves accumulated User-Agent info for a Short Url.
     * @param id Short Url id
     * @return Map containing User-Agent info grouped by platform and browser with their respective counts
     */
    override fun returnInfoShortUrl(id: String): Map<String, Int> {
        val clicks = clickRepository.findByUrlHash(id)
        return clicks
            .groupBy { "${it.properties.platform} - ${it.properties.browser}" }
            .mapValues { (_, value) -> value.size }
    }
}
