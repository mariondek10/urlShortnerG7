@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.net.HttpURLConnection
import java.net.URL


/**
 *
 * Given an Url, checks if the Url is reachable
 *
 *
 */
interface IsReachableUseCase {
    fun isReachable(url: String): Boolean
}

class IsReachableUseCaseImpl(
    private val validatorService: ValidatorService
) : IsReachableUseCase {
    override fun isReachable(url: String): Boolean {
        var attempts = 0
        val maxAttempts = 3
        val delayMillis = 1000L


        while (attempts < maxAttempts) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 3000

                val responseCode = connection.responseCode

                if (responseCode in 200..299) {
                    return true
                } else {
                    Thread.sleep(delayMillis)
                    attempts++
                }
            } catch (e: Exception) {
                println(e)
                return false
            }
        }
        return false
    }
}


