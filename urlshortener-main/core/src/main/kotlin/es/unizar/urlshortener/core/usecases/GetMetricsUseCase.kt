@file:Suppress("WildcardImport")


package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.MetricNotExists
import java.net.URL
import java.net.HttpURLConnection
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue





interface   GetMetricsUseCase {
    fun getMetricById(metric: String):  Map<String, Any>
    fun getAvailableMetrics(): Array<String>

    fun metricExists(metric: String): Boolean
}

class GetMetricsUseCaseImpl (
    private val availableMetrics: Array<String> = arrayOf("health", "info", "beans", "status")
) : GetMetricsUseCase {

    override fun getMetricById(metric: String): Map<String,Any> {
        if(metricExists(metric)){
            val url = URL("http://localhost:8080/actuator/$metric")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val responseBody = inputStream.bufferedReader().use { it.readText() }

                val objectMapper = jacksonObjectMapper()

                return objectMapper.readValue(responseBody)

            }
        } else {
           throw MetricNotExists(metric)
        }
        return emptyMap()
    }
    override fun getAvailableMetrics(): Array<String> {
       return availableMetrics
    }

    override fun metricExists(metric: String): Boolean {
        return availableMetrics.contains(metric)
    }


}
