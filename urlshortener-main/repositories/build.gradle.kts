plugins {
    id("urlshortener.spring-library-conventions")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("io.github.g0dkar:qrcode-kotlin:4.0.6")
    
}
