package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ResponseBody
    @ExceptionHandler(value = [InvalidUrlException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun invalidUrls(ex: InvalidUrlException) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [UrlToShortNotReachable::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun urlToShortNotReachable(ex: UrlToShortNotReachable) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)


    @ResponseBody
    @ExceptionHandler(value = [UrlRegisteredButNotReachable::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun urlRegisteredButNotReachable(ex: UrlRegisteredButNotReachable) = ErrorMessage(HttpStatus.FORBIDDEN.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [QRNotAvailable::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun qrNotAvailable(ex: QRNotAvailable) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [RedirectionNotFound::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun redirectionNotFound(ex: RedirectionNotFound) = ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [MetricNotExists::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun metricNotExists(ex: MetricNotExists) = ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.message)
}

data class ErrorMessage(
    val statusCode: Int,
    val message: String?,
    val timestamp: String = DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())
)
