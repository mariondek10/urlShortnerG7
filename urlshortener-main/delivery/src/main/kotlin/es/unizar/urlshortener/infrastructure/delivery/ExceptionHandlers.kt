@file:Suppress("WildcardImport")

package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.http.HttpHeaders
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ResponseBody
    @ExceptionHandler(value = [InvalidUrlException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun invalidUrls(ex: InvalidUrlException) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    /*
    @ResponseBody
    @ExceptionHandler(value = [UrlToShortNotReachable::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun urlToShortNotReachable(ex: UrlToShortNotReachable) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)
    */

    @ResponseBody
    @ExceptionHandler(value = [UrlRegisteredButNotReachable::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun urlRegisteredButNotReachable(ex: UrlRegisteredButNotReachable) = ErrorMessage(HttpStatus.FORBIDDEN.value(),
            ex.message)

    @ResponseBody
    @ExceptionHandler(value = [QRNotAvailable::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun qrNotAvailable(ex: QRNotAvailable) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [RedirectionNotFound::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun redirectionNotFound(ex: RedirectionNotFound) = ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [KeyAlreadyExists::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun keyAlreadyExists(ex: KeyAlreadyExists) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [AliasAlreadyExists::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun aliasAlreadyExists(ex: AliasAlreadyExists) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [AliasContainsSlash::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun aliasContainsSlash(ex: AliasContainsSlash) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    /*
    @ResponseBody
    @ExceptionHandler(value = [ReachabilityNotChecked::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun reachabilityNotChecked(ex: ReachabilityNotChecked): ResponseEntity<ErrorMessage> {
        val retryAfter = LocalDateTime.now().plus(3, ChronoUnit.SECONDS)
        val headers = HttpHeaders()
        headers.add(HttpHeaders.RETRY_AFTER, retryAfter.atZone(ZoneId.systemDefault()).toString())
        ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)
        return ResponseEntity<ErrorMessage>()
    }
    */

    @ResponseBody
    @ExceptionHandler(value = [ReachabilityNotChecked::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun reachabilityNotChecked(
        ex: ReachabilityNotChecked
    ): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header("Retry-After", "5")
            .body(errorMessage)
    }
}


data class ErrorMessage(
    val statusCode: Int,
    val message: String?,
    val timestamp: String = DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now()),
)
