@file:Suppress("WildcardImport")

package es.unizar.urlshortener.infrastructure.delivery

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import es.unizar.urlshortener.core.*

import es.unizar.urlshortener.core.usecases.*

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.never
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.util.concurrent.BlockingQueue

@WebMvcTest
@ContextConfiguration(
    classes = [
        UrlShortenerControllerImpl::class,
        RestResponseEntityExceptionHandler::class
    ]
)
class UrlShortenerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var redirectUseCase: RedirectUseCase

    @MockBean
    private lateinit var logClickUseCase: LogClickUseCase

    @MockBean
    private lateinit var createShortUrlUseCase: CreateShortUrlUseCase

    @MockBean
    private lateinit var csvUseCase: CsvUseCase

    @MockBean
    private lateinit var qrUseCase: QRUseCase

    @MockBean
    private lateinit var isReachableUseCase: IsReachableUseCase

    @MockBean
    private lateinit var qrQueue: BlockingQueue<Pair<String, String>>

    @MockBean
    private lateinit var identifyInfoClientUseCase: IdentifyInfoClientUseCase



    @Test
    fun `redirectTo returns a redirect when the key exists`() {
        given(redirectUseCase.redirectTo("key")).willReturn(Redirection("http://example.com/"))

        mockMvc.perform(get("/{id}", "key"))
            .andExpect(status().isTemporaryRedirect)
            .andExpect(redirectedUrl("http://example.com/"))

        verify(logClickUseCase).logClick("key", ClickProperties(ip = "127.0.0.1"))
    }

    @Test
    fun `redirectTo returns a not found when the key does not exist`() {
        given(redirectUseCase.redirectTo("key"))
            .willAnswer { throw RedirectionNotFound("key") }

        mockMvc.perform(get("/{id}", "key"))
            .andDo(print())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.statusCode").value(404))

        verify(logClickUseCase, never()).logClick("key", ClickProperties(ip = "127.0.0.1"))
    }

    @Test
    fun `creates returns a basic redirect if it can compute a hash`() {
        given(
            createShortUrlUseCase.create(
                url = "http://example.com/",
                data = ShortUrlProperties(ip = "127.0.0.1")
            )
        ).willReturn(ShortUrl("f684a3c4", Redirection("http://example.com/")))

        mockMvc.perform(
            post("/api/link")
                .param("url", "http://example.com/")
                .param("qrBool", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(redirectedUrl("http://localhost/f684a3c4"))
            .andExpect(jsonPath("$.url").value("http://localhost/f684a3c4"))
    }

    @Test
    fun `creates returns a basic redirect if it can compute a hash with a qr`() {
        given(
                createShortUrlUseCase.create(
                        url = "http://example.com/",
                        data = ShortUrlProperties(ip = "127.0.0.1", qrBool = true)
                )
        ).willReturn(ShortUrl("f684a3c4", Redirection("http://example.com/")))

        mockMvc.perform(
                post("/api/link")
                        .param("url", "http://example.com/")
                        .param("qrBool", "true")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        )
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                .andExpect(jsonPath("$.url").value("http://localhost/f684a3c4"))
                .andExpect(jsonPath("$.properties.qr").value("http://localhost/f684a3c4/qr"))
    }

    @Test
    fun `Create returns a 400 response if the uri to shorten is not reachable`(){
        val urlToShorten = "http://url-unreachable.com"
        given(isReachableUseCase.isReachable(urlToShorten)).willReturn(false)
        given(
            createShortUrlUseCase.create(
                url = urlToShorten,
                data = ShortUrlProperties(ip = "127.0.0.1")
            )
        ).willAnswer { throw UrlToShortNotReachable(urlToShorten) }

        mockMvc.perform(
            post("/api/link")
                .param("url",urlToShorten)
                .contentType((MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `Redirect to returns a 403 response if the id is registered but the uri is not reachable`(){
        val urlToRedirect = "https://url-unreachable.com/"
        val id = "existing-hash"

        given(isReachableUseCase.isReachable(urlToRedirect)).willReturn(false)
        given(redirectUseCase.redirectTo(id))
            .willAnswer { throw UrlRegisteredButNotReachable(id) }


        mockMvc.perform(
            get("/{id}", id)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `creates returns bad request if it can compute a hash`() {
        given(
            createShortUrlUseCase.create(
                url = "ftp://example.com/",
                data = ShortUrlProperties(ip = "127.0.0.1")
            )
        ).willAnswer { throw InvalidUrlException("ftp://example.com/") }

        mockMvc.perform(
            post("/api/link")
                .param("url", "ftp://example.com/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        )
            .andExpect(status().isBadRequest)
    }


    @Test
    fun `returnInfoShortUrl returns information if short url has been clicked`() {

        val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/11.0.696.68 Safari/535.19"
        val data: MutableMap<String, Int> = mutableMapOf()
        data["Mac OS X - Chrome 11"] = 1

        given(redirectUseCase.redirectTo("key")).willReturn(Redirection("http://example.com/"))
        given(identifyInfoClientUseCase.returnInfoShortUrl("key")).willReturn(data)

        mockMvc.perform(get("/{id}", "key")
            .header("User-Agent", userAgent))
            .andExpect(status().isTemporaryRedirect)
            .andExpect(redirectedUrl("http://example.com/"))

        mockMvc.perform(get("/api/link/{id}", "key"))
            .andExpect(status().isOk)
            .andExpect(content().json(jacksonObjectMapper().writeValueAsString(data)))
    }

    @Test
    fun `redirectTo doesn't return a 4xx error if there are no headers`() {
        given(redirectUseCase.redirectTo("key")).willReturn(Redirection("http://example.com/"))

        // Si envio cabeceras vacias no genera ningun error de usuario
        mockMvc.perform(get("/{id}", "key"))
            .andExpect(status().isTemporaryRedirect)
            .andExpect(redirectedUrl("http://example.com/"))

    }

    @Test
    fun `if the key doesn't exist, qr will return a not found (404)`() {
        given(qrUseCase.getQRUseCase("key"))
                .willAnswer { throw RedirectionNotFound("key") }

        mockMvc.perform(get("/{id}/qr", "key"))
                .andDo(print())
    }

    @Test
    fun `if the key exists but doesn't exist a qr for that key, qr returns a bad request`() {
        given(qrUseCase.getQRUseCase("key"))
                .willAnswer { throw QRNotAvailable("key") }

        mockMvc.perform(get("/{id}/qr", "key"))
                .andDo(print())
    }

    @Test
    fun `if the key exists, qr will return an image `() {
        given(qrUseCase.getQRUseCase("key")).willReturn("Testing".toByteArray())

        mockMvc.perform(get("/{id}/qr", "key"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes("Testing".toByteArray()))
    }


    /**
     * Test that the controller returns a 400 error if the key already exists
     */
    /*
    @Test
    fun `if the key already exists, returns a 400 error`() {
        val existingKey = "existing-key"
        val existingUrl = "http://example.com/"

        // Set up mock behavior to simulate the key already existing
        given(createShortUrlUseCase.create(url = existingUrl, data = ShortUrlProperties(ip = "127.0.0.1")))
            .willAnswer { throw KeyAlreadyExists(existingKey) }

        // Perform the HTTP request and check for a 400 error
        mockMvc.perform(
            post("/api/link")
                .param("url", existingUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Key already exists: $existingKey"))
    }
    */
    
     /**
     * Test that the controller returns a 400 error if the alias contains a slash
     */

    @Test
    fun `if the alias contains a slash, returns a 400 error`(){
        val existingUrl = "http://example.com/"
        val alias = "alias/with/slash"
        mockMvc.perform(
            post("/api/link")
                .param("url", existingUrl)
                .param("alias", alias)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))

    }

    /*
    INTENTO DE  TEST DE LA BLOCKING QUEUE -> COMPROBAR QUE SE EJECUTA EXECUTOR DE LA QRBLOCKINGQUEUE
    @Test
    fun `executor processes qrQueue items`() {

        val request = MockHttpServletRequest()
        request.remoteAddr = "127.0.0.1"

        val data = ShortUrlDataIn(
                url = "http://example.com/",
                qrBool = true
        )

        mockMvc.perform(
                post("/api/link")
                        .param("url", data.url)
                        .param("qrBool", data.qrBool.toString())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated)

        verify(qrBlockingQueue).executor()
        //verify(qrBlockingQueue).add(qrQueueCaptor.capture())

        val capturedPair = qrQueueCaptor.value
        val expectedPair = Pair("hashValue", "http://example.com/") // Valores esperados
        assert(capturedPair == expectedPair) // Verificar si la captura coincide con los valores esperados
    }*/
}
