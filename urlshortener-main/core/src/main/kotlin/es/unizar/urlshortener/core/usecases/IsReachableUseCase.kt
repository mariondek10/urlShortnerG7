@file:Suppress("WildcardImport", "TooGenericExceptionCaught", "MagicNumber")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.ShortUrlRepositoryService
import java.net.HttpURLConnection
import java.net.URL


/**
 *
 * Given an Url, checks if the Url is reachable
 *
 *
 */
interface IsReachableUseCase {
    /**
     * Checks if the provided URL is reachable.
     * @param url The URL to check for reachability
     * @return Boolean indicating if the URL is reachable
     */
    fun isReachable(url: String, hash: String): Boolean
}

/**
 * Implementation of IsReachableUseCase interface.
 */
class IsReachableUseCaseImpl (
    private val shortUrlRepository: ShortUrlRepositoryService
) : IsReachableUseCase {

    /**
     * Checks if the provided URL is reachable by making a GET request and verifying the response code.
     * @param url The URL to check for reachability
     * @return Boolean indicating if the URL is reachable
     */
    override fun isReachable(url: String, hash: String): Boolean {
        var isConnected = false
        var attempts = 0
        val maxAttempts = 3
        val delayMillis = 1000L


        while (attempts < maxAttempts && !isConnected) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 3000

                val responseCode = connection.responseCode

                if (responseCode in 200..299) {
                    isConnected = true
                } else {
                    Thread.sleep(delayMillis)
                    attempts++
                }
            } catch (e: Exception) {
                println(e)
                attempts++
            }
        }
        if (isConnected) {
            shortUrlRepository.updateReachabilityCode(hash, 1)
        } else {
            shortUrlRepository.updateReachabilityCode(hash, 0)
        }
        return isConnected
    }
}



