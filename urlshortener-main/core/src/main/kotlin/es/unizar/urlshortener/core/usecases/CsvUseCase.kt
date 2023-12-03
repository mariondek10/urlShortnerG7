@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases
import es.unizar.urlshortener.core.*
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URI
import java.net.MalformedURLException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.net.URLEncoder
import java.io.File


// Testing purposes only! 
fun main() {
    val convertCsvUseCase = CsvUseCaseImpl(1)

    // Example CSV data
    val csvData = "https://www.example.com/blog/how-to-create-a-website;;https://github.com/;https://github.com/\nhttps://www.youtube.com/;https://github.com/;https://github.com/;\nhttps://github.com/;;;\nhttps://github.com/;;;\nhttps://github.com/;;;\nhttps://github.com/;;;"

    val modifiedCsvData = convertCsvUseCase.convert(csvData, "1")

    println("Original CSV data:")
    println(csvData)
    println("\nModified CSV data:")
    println(modifiedCsvData)
}

/**
 * Given CSV data (divided by ; specifically), converts it and returns the modified CSV data.
 *
 */
interface CsvUseCase {
    fun convert(csvData: String, selector:String): String
}

data class ReturnData(
    val url: String,
    val extra: String
)

/**
 * Implementation of [ConvertCsvUseCase].
 */
class CsvUseCaseImpl(
    // We need a numeric parameter to work as a selector
    private val temp: Int
) : CsvUseCase {
    override fun convert(csvData: String, selector:String): String {
        // We convert selector to int
        val selector = selector.toInt()
        val rows = csvData.split("\n")
        var newCell = ""
        //println("Rows: " + rows)
        val modifiedData = rows.joinToString("") { row ->
            //println("Row: " + row)
            row.split(";").joinToString("") { cell ->
                if (cell.isBlank()) {
                    ""
                } else {
                    when (selector) {
                        1 -> {
                            print("Shorten ")
                            try {      
                                newCell = shortenUri(cell)
                                "$cell;$newCell;\n"
                            } catch (e: MalformedURLException) {
                                // Handle invalid URL
                                println("Invalid URL: " + e)
                                "invalid_url"
                            } catch (e: Exception) {
                                // Handle other errors
                                println("Error: " + e)
                                "conversion_error"
                            }
                        }
                        2 -> { // Remove conversion errors after implementing the other cases
                            println("Generat QRs for all URLs")
                            "conversion_error2"
                            // Additional actions for case 2
                        }
                        3 -> {
                            println("Personalize all URLs")
                            "conversion_error3"
                            // Additional actions for case 3
                        }
                        else -> {// Dont remove this conversion error
                            println("Invalid request number: $selector")
                            "conversion_errorElse"
                        }
                    }        
                }
            }
        }

        return modifiedData
    }

    private fun shortenAllUrlsInCell(cell: String): String { // Can be later modified to inclu pre and post processing
        val urls = cell.split(';')
        val shortenedUrls = urls.map { 
            println("URL: " + it)
            shortenUri(it) 
        }
        return shortenedUrls.joinToString(";")
    }

    private fun shortenUri(originalUri: String): String { // Modify it so it use ajax or it's gonna be a mess (use kotlin/js)
        val apiUrl = "http://localhost:8080/api/link" 
        
        var newUrl = ""
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        connection.setRequestProperty("Accept", "application/json") 
        connection.doOutput = true

        // Prepare the data for the API request
        val postData = "url=${URLEncoder.encode(originalUri, "UTF-8")}"
        val input = postData.toByteArray(Charsets.UTF_8)

        // Send the data in the body of the request
        connection.outputStream.use { it.write(input, 0, input.size) }

        // Read the response code
        val responseCode = connection.responseCode
        println("Response Code: $responseCode")

        // If the request was successful, get the short URL from the 'Location' header
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            newUrl = connection.getHeaderField("Location")
            println("Response header: " + newUrl)
        }

        // Read the response from the server
        val response = connection.inputStream.bufferedReader().use { it.readText() }

       // Remove curly braces and split by comma
        val keyValuePairs = response
            .replace("{\"", "")
            .replace("\"}", "")
            .replace("\":\"", ";")
            .replace("\"", "")
            .split(",")

        // Extract and print key-value pairs
        keyValuePairs.forEach { pair ->
            val keyValue = pair.trim().split(";")
            //println(keyValue)
            if (keyValue.size == 2) {
                val key = keyValue[0].trim().removeSurrounding("\"")
                val value = keyValue[1].trim().removeSurrounding("\"")
                var returnData = ReturnData(key, value)
            }
        }

        //println("Full response: " + response)
        //println("Shortened URL: " + newUrl)
        return newUrl
    }
}