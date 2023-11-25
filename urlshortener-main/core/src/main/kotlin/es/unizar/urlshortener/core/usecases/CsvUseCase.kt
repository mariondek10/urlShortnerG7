@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.MalformedURLException

// Testing purposes only!
fun main() {
    val convertCsvUseCase = ConvertCsvUseCaseImpl(1)

    // Example CSV data
    val csvData = "https://www.example.com/blog/how-to-create-a-website;https://github.com/\nhttps://www.youtube.com/"

    val modifiedCsvData = convertCsvUseCase.convert(csvData)

    println("Original CSV data:")
    println(csvData)
    println("\nModified CSV data:")
    println(modifiedCsvData)
}

/**
 * Given CSV data (divided by ; specifically), converts it and returns the modified CSV data.
 *
 */
interface ConvertCsvUseCase {
    fun convert(csvData: String): String
}

/**
 * Implementation of [ConvertCsvUseCase].
 */
class ConvertCsvUseCaseImpl(
    // We need a numeric parameter to work as a selector
    private val selector: Int
) : ConvertCsvUseCase {
    override fun convert(csvData: String): String {
        val rows = csvData.split("\n")

        val modifiedData = rows.joinToString("\n") { row ->
            row.split(';').joinToString(";") { cell ->
                if (cell.isBlank()) {
                    ""
                } else {
                    when (selector) {
                        1 -> {
                            println("Shorten all URLs")
                            try {      
                                shortenAllUrlsInCell(cell)
                            } catch (e: MalformedURLException) {
                                // Handle invalid URL
                                "invalid_url"
                            } catch (e: Exception) {
                                // Handle other errors
                                "conversion_error"
                            }
                        }
                        2 -> { // Remove conversion errors after implementing the other cases
                            println("Generat QRs for all URLs")
                            "conversion_error"
                            // Additional actions for case 2
                        }
                        3 -> {
                            println("Personalize all URLs")
                            "conversion_error"
                            // Additional actions for case 3
                        }
                        else -> {// Dont remove this conversion error
                            println("Invalid request number: $selector")
                            "conversion_error"
                        }
                    }        
                }
            }
        }

        return modifiedData
    }

    private fun shortenAllUrlsInCell(cell: String): String {
        val urls = cell.split(';')
        val shortenedUrls = urls.map { 
            println("URL: " + it)
            shortenUri(it) 
        }
        return shortenedUrls.joinToString(";")
    }

    private fun shortenUri(originalUri: String): String {
        val accessToken = "ed77cd0ebc778daa21631a8c9a9679b30da7dc20"
        val apiUrl = "https://api-ssl.bitly.com/v4/shorten"

        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $accessToken")
        connection.setRequestProperty("Accept", "application/json") // Add Accept header
        connection.doOutput = true

        // Prepare data for the API request
        val data = "{\"long_url\": \"$originalUri\"}"

        // Write the data to the output stream
        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.writeBytes(data)
        outputStream.flush()
        outputStream.close()

        // Check the HTTP response code
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()

            // Parse the JSON response and return the shortened URL
            return response.split("\"link\":\"")[1].split("\"")[0]
        } else {
            println("HTTP request failed with response code: $responseCode")
            return "uri_shortening_error"
        }
    }
}