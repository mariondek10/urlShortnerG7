package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.ClickProperties
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.*
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import es.unizar.urlshortener.core.usecases.QRUseCase
import eu.bitwalker.useragentutils.UserAgent
import jakarta.servlet.http.HttpServletRequest
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.core.io.ByteArrayResource
import java.net.URI
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import java.util.concurrent.BlockingQueue


/**
 * The specification of the controller.
 */
interface UrlShortenerController {

    /**
     * Redirects and logs a short url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun redirectTo(id: String, request: HttpServletRequest): ResponseEntity<Unit>

    /**
     * Creates a short url from details provided in [data].
     *
     * **Note**: Delivery of use case [CreateShortUrlUseCase].
     */
    fun shortener(data: ShortUrlDataIn, request: HttpServletRequest): ResponseEntity<ShortUrlDataOut>

    /**
     * Converts CSV data provided in [data].
     *
     * **Note**: Delivery of use case [CsvUseCase].
     */
    fun csvHandler(data: CsvDataIn, request: HttpServletRequest): ResponseEntity<CsvDataOut>

    fun getQR(id: String, request: HttpServletRequest): ResponseEntity<ByteArrayResource>

    fun returnInfoHeader(id: String, request: HttpServletRequest): ResponseEntity<Any>

}

/**
 * Data required to create a short url.
 */
data class ShortUrlDataIn(
    val url: String,
    val sponsor: String? = null,
    val alias: String? = null,
    val qrBool: Boolean

)

/**
 * Data returned after the creation of a short url.
 */
data class ShortUrlDataOut(
    val url: URI? = null,
    val properties: Map<String, Any> = emptyMap()
)

/**
 * Data required to process CSV data.
 */
 data class CsvDataIn(
    val csv: String,
    val selector: String
 )

/**
 * Data returned after the processing of a CSV file.
 */
data class CsvDataOut(
    val csv: String
)

/**
 * The implementation of the controller.
 *
 * **Note**: Spring Boot is able to discover this [RestController] without further configuration.
 */
@RestController
class UrlShortenerControllerImpl(
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase,
    val createShortUrlUseCase: CreateShortUrlUseCase,
    val csvUseCase: CsvUseCase,
    val qrUseCase: QRUseCase,
    val qrQueue: BlockingQueue<Pair<String, String>>,
    val identifyInfoClientUseCase: IdentifyInfoClientUseCase

) : UrlShortenerController {

    @GetMapping("/{id:(?!api|index).*}")
    override fun redirectTo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Unit> =
        redirectUseCase.redirectTo(id).let {
            val header = request.getHeader("User-Agent")
            val userAgent = UserAgent.parseUserAgentString(header)
            val browser = userAgent.browser.getName()
            val platform = userAgent.operatingSystem.getName()
            logClickUseCase.logClick(id, ClickProperties(ip = request.remoteAddr, browser = browser, platform = platform))
            val h = HttpHeaders()
            h.location = URI.create(it.target)
            ResponseEntity<Unit>(h, HttpStatus.valueOf(it.mode))
        }

    @PostMapping("/api/link", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    override fun shortener(data: ShortUrlDataIn, request: HttpServletRequest): ResponseEntity<ShortUrlDataOut> =
        createShortUrlUseCase.create(
            url = data.url,
            data = ShortUrlProperties(
                ip = request.remoteAddr,
                sponsor = data.sponsor,
                alias = data.alias,
                qrBool = data.qrBool
            )
        ).let {
            System.out.println("(UrlShortenerController) datos APP.js: ShortUrlDataIn:" + data)
            System.out.println("(UrlShortenerController) shortURL creada:" + it)

            val h = HttpHeaders()
            val url = linkTo<UrlShortenerControllerImpl> { redirectTo(it.hash, request) }.toUri()
            h.location = url

            //val properties = mutableMapOf<String, Any>("safe" to it.properties.safe)
            val properties = mutableMapOf<String, Any>()

            if(data.qrBool){
                System.out.println("(UrlShortenerController) LLAMANDO A  generateQR")
                qrQueue.put(Pair(it.hash, url.toString()))
                System.out.println("(UrlShortenerController) LLAMANDO A getQR():" + url.toString())
                val qrUrl = linkTo<UrlShortenerControllerImpl> { getQR(it.hash, request) }.toUri()
                properties["qr"] = qrUrl
            }

            val response = ShortUrlDataOut(
                url = url,
                properties = properties
            )
            System.out.println("(UrlShortenerController) response: ShortUrlDataOut:" + response)

            ResponseEntity<ShortUrlDataOut>(response, h, HttpStatus.CREATED)
        }

    @PostMapping("/api/bulk", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]) 
    override fun csvHandler(data: CsvDataIn, request: HttpServletRequest): ResponseEntity<CsvDataOut> =
        csvUseCase.convert(data.csv, data.selector).let { processedData ->
            val response = CsvDataOut(
                csv = processedData
            )
            ResponseEntity<CsvDataOut>(response, HttpStatus.OK)
        }




    @GetMapping("/{id:(?!api|index).*}/qr")
    override fun getQR(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ByteArrayResource> =
        qrUseCase.getQRUseCase(id).let { qr ->
            val h = HttpHeaders()
            h.set(HttpHeaders.CONTENT_TYPE, IMAGE_PNG_VALUE)
            System.out.println("(UrlShortenerController) qr del getQRUseCase:" + qr)
            ResponseEntity<ByteArrayResource>(ByteArrayResource(qr, IMAGE_PNG_VALUE), h, HttpStatus.OK)
        }

    @GetMapping("/api/link/{id}")
    override fun returnInfoHeader(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Any> {
        return ResponseEntity(identifyInfoClientUseCase.returnInfoShortUrl(id), HttpStatus.OK)
    }

}
