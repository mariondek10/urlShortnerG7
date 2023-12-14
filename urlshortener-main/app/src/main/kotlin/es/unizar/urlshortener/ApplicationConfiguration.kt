@file:Suppress("WildcardImport")
package es.unizar.urlshortener

import es.unizar.urlshortener.core.usecases.*
import es.unizar.urlshortener.infrastructure.delivery.HashServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ValidatorServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.ClickEntityRepository
import es.unizar.urlshortener.infrastructure.repositories.ClickRepositoryServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.ShortUrlEntityRepository
import es.unizar.urlshortener.infrastructure.repositories.ShortUrlRepositoryServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashMap

/**
 * Wires use cases with service implementations, and services implementations with repositories.
 *
 * **Note**: Spring Boot is able to discover this [Configuration] without further configuration.
 */
@Suppress("TooManyFunctions") //Para que no de error el detekt
@Configuration
class ApplicationConfiguration(
    @Autowired val shortUrlEntityRepository: ShortUrlEntityRepository,
    @Autowired val clickEntityRepository: ClickEntityRepository
) {
    @Bean
    fun clickRepositoryService() = ClickRepositoryServiceImpl(clickEntityRepository)

    @Bean
    fun shortUrlRepositoryService() = ShortUrlRepositoryServiceImpl(shortUrlEntityRepository)

    @Bean
    fun validatorService() = ValidatorServiceImpl()

    @Bean
    fun hashService() = HashServiceImpl()

    @Bean
    fun redirectUseCase() = RedirectUseCaseImpl(shortUrlRepositoryService(), isReachableUseCase())

    @Bean
    fun logClickUseCase() = LogClickUseCaseImpl(clickRepositoryService())

    @Bean
    fun qrMap(): HashMap<String, ByteArray> = HashMap()

    @Bean
    fun qRUseCase() = QRUseCaseImpl(shortUrlRepositoryService(), qrMap())

    @Bean
    fun isReachableUseCase() = IsReachableUseCaseImpl(validatorService())

    @Bean
    fun createShortUrlUseCase() =
        CreateShortUrlUseCaseImpl(shortUrlRepositoryService(), isReachableUseCase(),validatorService(), hashService())
    
    @Bean
    fun csvUseCase() = CsvUseCaseImpl(0)

    @Bean
    fun identifyInfoClientUseCase() = IdentifyInfoClientUseCaseImpl(clickRepositoryService())

    @Bean
    fun qrQueue(): BlockingQueue<Pair<String, String>> = LinkedBlockingQueue()

}
