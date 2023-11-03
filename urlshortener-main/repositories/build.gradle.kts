plugins {
    id("urlshortener.spring-library-conventions")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
