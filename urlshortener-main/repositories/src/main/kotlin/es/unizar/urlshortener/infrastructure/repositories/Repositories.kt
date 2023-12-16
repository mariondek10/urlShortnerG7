package es.unizar.urlshortener.infrastructure.repositories

import es.unizar.urlshortener.core.Click
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.data.jpa.repository.JpaRepository
//import org.springframework.data.repository.kotlin.CoroutineCrudRepository
//import org.springframework.stereotype.Service

/**
 * Specification of the repository of [ShortUrlEntity].
 *
 * **Note**: Spring Boot is able to discover this [JpaRepository] without further configuration.
 */
interface ShortUrlEntityRepository : JpaRepository<ShortUrlEntity, Long> {
    fun findByHash(hash: String): ShortUrlEntity?
}

/**
 * Specification of the repository of [ClickEntity].
 *
 * **Note**: Spring Boot is able to discover this [JpaRepository] without further configuration.
 */
interface ClickEntityRepository : JpaRepository<ClickEntity, Long> {
    fun findAllByHash(hash: String): List<ClickEntity>
}


